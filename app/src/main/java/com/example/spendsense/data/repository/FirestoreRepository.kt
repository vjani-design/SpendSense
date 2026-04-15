package com.example.spendsense.data.repository

import com.example.spendsense.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
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
        val code = groupId.take(6).uppercase()

        val group = hashMapOf(
            "id" to groupId,
            "name" to groupName,
            "code" to code,
            "members" to listOf(userId)
        )

        db.collection("groups")
            .document(groupId)
            .set(group)
            .addOnSuccessListener {
                onSuccess(groupId)
            }
    }

    fun joinGroup(userId: String, code: String, onResult: (String?) -> Unit) {
        db.collection("groups")
            .whereEqualTo("code", code)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val groupId = doc.id

                    db.collection("groups")
                        .document(groupId)
                        .update("members", FieldValue.arrayUnion(userId))

                    onResult(groupId)
                } else {
                    onResult(null)
                }
            }
    }
}