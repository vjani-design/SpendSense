package com.example.spendsense.data.repository

import com.example.spendsense.model.Transaction
import com.example.spendsense.data.model.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.Timestamp

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // ================= PERSONAL TRANSACTIONS =================

    fun listenPersonalTransactions(
        userId: String,
        onResult: (List<Transaction>) -> Unit
    ): ListenerRegistration {

        return db.collection("users")
            .document(userId)
            .collection("transactions")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->

                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)?.apply {
                        id = doc.id   // 🔥 attach document ID
                    }
                } ?: emptyList()

                onResult(list)
            }
    }

    fun addPersonalTransaction(userId: String, transaction: Transaction) {

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .add(
                transaction.copy(
                    type = transaction.type.uppercase(),
                    date = transaction.date ?: Timestamp.now(),
                    createdAt = Timestamp.now()
                )
            )
    }

    fun updatePersonalTransaction(userId: String, transaction: Transaction) {
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .document(transaction.id)
            .set(transaction.copy())   // id excluded automatically
    }

    fun deletePersonalTransaction(userId: String, id: String) {
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .document(id)
            .delete()
    }

    // ================= GROUP TRANSACTIONS =================

    fun listenGroupTransactions(
        groupId: String,
        onResult: (List<Transaction>) -> Unit
    ): ListenerRegistration {

        return db.collection("groups")
            .document(groupId)
            .collection("transactions")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->

                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)?.apply {
                        id = doc.id
                    }
                } ?: emptyList()

                onResult(list)
            }
    }

    fun addGroupTransaction(groupId: String, transaction: Transaction) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("groups")
            .document(groupId)
            .collection("transactions")
            .add(
                transaction.copy(
                    paidBy = uid,
                    type = transaction.type.uppercase(),
                    date = transaction.date ?: Timestamp.now(),
                    createdAt = Timestamp.now()
                )
            )
    }

    fun updateGroupTransaction(groupId: String, transaction: Transaction) {
        db.collection("groups")
            .document(groupId)
            .collection("transactions")
            .document(transaction.id)
            .set(transaction.copy())
    }

    fun deleteGroupTransaction(groupId: String, id: String) {
        db.collection("groups")
            .document(groupId)
            .collection("transactions")
            .document(id)
            .delete()
    }

    // ================= BUDGET =================

    fun saveBudget(
        baseId: String,
        budget: Double,
        type: String,
        isGroup: Boolean
    ) {

        val data = mapOf(
            "budget" to budget,
            "type" to type
        )

        val path = if (isGroup) {
            db.collection("groups").document(baseId)
        } else {
            db.collection("users").document(baseId)
        }

        path.collection("budget")
            .document("main")
            .set(data)
    }

    fun getBudget(
        baseId: String,
        isGroup: Boolean,
        onResult: (Double, String) -> Unit
    ) {

        val path = if (isGroup) {
            db.collection("groups").document(baseId)
        } else {
            db.collection("users").document(baseId)
        }

        path.collection("budget")
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
            "members" to hashMapOf(userId to true), // ⚠️ keep for now
            "invitedEmails" to hashMapOf<String, Boolean>()
        )

        db.collection("groups")
            .document(groupId)
            .set(group)
            .addOnSuccessListener { onSuccess(groupId) }
            .addOnFailureListener { onSuccess("") }
    }

    fun inviteUser(groupId: String, email: String, onResult: (Boolean) -> Unit) {

        val safeEmail = email.trim().lowercase().replace(".", ",")

        db.collection("groups")
            .document(groupId)
            .update("invitedEmails.$safeEmail", true)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

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

                    val safeEmail = currentEmail.trim().lowercase().replace(".", ",")

                    if (group?.invitedEmails?.containsKey(safeEmail) == true) {

                        db.collection("groups")
                            .document(groupId)
                            .update("members.$userId", true)
                            .addOnSuccessListener { onResult(groupId) }
                            .addOnFailureListener { onResult(null) }

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
                onResult(if (doc.exists()) doc.toObject(Group::class.java) else null)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun removeInvite(groupId: String, safeEmail: String) {
        db.collection("groups")
            .document(groupId)
            .update("invitedEmails.$safeEmail", FieldValue.delete())
    }

    fun getAllGroups(onResult: (List<Group>) -> Unit) {
        db.collection("groups")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull {
                    try { it.toObject(Group::class.java) } catch (e: Exception) { null }
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}