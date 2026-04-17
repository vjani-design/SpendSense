package com.example.spendsense.data.repository

import com.example.spendsense.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.example.spendsense.data.model.Group
import com.google.firebase.auth.FirebaseAuth

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // ================= TRANSACTIONS =================

    fun addTransaction(transaction: Transaction, onResult: (Boolean) -> Unit) {

        val docRef = db.collection("groups")
            .document(transaction.groupId)
            .collection("transactions")
            .document()

        val data = transaction.copy(id = docRef.id)

        docRef.set(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getTransactions(groupId: String, onResult: (List<Transaction>) -> Unit) {

        db.collection("groups")
            .document(groupId)
            .collection("transactions")
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    onResult(snap.toObjects(Transaction::class.java))
                } else {
                    onResult(emptyList())
                }
            }
    }

    fun deleteTransaction(groupId: String, id: String, onResult: (Boolean) -> Unit) {

        db.collection("groups")
            .document(groupId)
            .collection("transactions")
            .document(id)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateTransaction(transaction: Transaction, onResult: (Boolean) -> Unit) {

        db.collection("groups")
            .document(transaction.groupId)
            .collection("transactions")
            .document(transaction.id)
            .set(transaction)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // ================= BUDGET =================

    fun saveBudget(groupId: String, budget: Double, type: String) {

        val data = mapOf(
            "budget" to budget,
            "type" to type
        )

        db.collection("groups")
            .document(groupId)
            .collection("budget")
            .document("main")
            .set(data)
    }

    fun getBudget(groupId: String, onResult: (Double, String) -> Unit) {

        db.collection("groups")
            .document(groupId)
            .collection("budget")
            .document("main")
            .get()
            .addOnSuccessListener {

                val budget = it.getDouble("budget") ?: 0.0
                val type = it.getString("type") ?: "MONTHLY"

                onResult(budget, type)
            }
            .addOnFailureListener {
                onResult(0.0, "MONTHLY")
            }
    }

    // ================= GROUPS =================

    fun createGroup(userId: String, groupName: String, onSuccess: (String) -> Unit) {

        val groupId = db.collection("groups").document().id

        val code = (System.currentTimeMillis().toString().takeLast(6) +
                (100..999).random()).take(6)

        val group = hashMapOf(
            "id" to groupId,
            "name" to groupName,
            "code" to code,
            "createdBy" to userId,
            "members" to hashMapOf(userId to true),
            "invitedEmails" to hashMapOf<String, Boolean>()
        )

        db.collection("groups")
            .document(groupId)
            .set(group)
            .addOnSuccessListener {
                onSuccess(groupId)
            }
            .addOnFailureListener {
                onSuccess("")
            }
    }

    // ================= INVITE =================
    fun inviteUser(groupId: String, email: String, onResult: (Boolean) -> Unit) {

        val safeEmail = email.trim().lowercase().replace(".", ",") // ✅ FIX

        db.collection("groups")
            .document(groupId)
            .update("invitedEmails.$safeEmail", true)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // ================= JOIN =================
    fun joinGroup(userId: String, code: String, onResult: (String?) -> Unit) {

        db.collection("groups")
            .whereEqualTo("code", code)
            .get()
            .addOnSuccessListener { result ->

                if (!result.isEmpty) {

                    val doc = result.documents[0]
                    val groupId = doc.id
                    val group = doc.toObject(Group::class.java)

                    val currentEmail = FirebaseAuth
                        .getInstance().currentUser?.email ?: ""

                    val safeEmail = currentEmail.trim().lowercase().replace(".", ",") // ✅ FIX

                    // ✅ ONLY ALLOW IF INVITED
                    if (group?.invitedEmails?.containsKey(safeEmail) == true) {

                        db.collection("groups")
                            .document(groupId)
                            .update("members.$userId", true)
                            .addOnSuccessListener {
                                onResult(groupId)
                            }
                            .addOnFailureListener {
                                onResult(null)
                            }

                    } else {
                        onResult(null)
                    }

                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getGroup(groupId: String, onResult: (Group?) -> Unit) {

        db.collection("groups")
            .document(groupId)
            .get()
            .addOnSuccessListener { doc ->

                if (doc.exists()) {
                    val group = doc.toObject(Group::class.java)
                    onResult(group)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
    fun getAllGroups(onResult: (List<Group>) -> Unit) {
        db.collection("groups")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull {
                    try {
                        it.toObject(Group::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}