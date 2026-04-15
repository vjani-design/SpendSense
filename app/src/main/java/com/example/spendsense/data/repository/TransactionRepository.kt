package com.example.spendsense.repository

import com.example.spendsense.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TransactionRepository {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // =========================
    // GET USER ID
    // =========================
    private fun uid(): String {
        return auth.currentUser?.uid
            ?: throw Exception("User not logged in")
    }

    // =========================
    // ADD TRANSACTION
    // =========================
    suspend fun addTransaction(transaction: Transaction) {
        val ref = db.collection("users")
            .document(uid())
            .collection("transactions")
            .document()

        val data = transaction.copy(id = ref.id)

        ref.set(data).await()
    }

    // =========================
    // GET ONCE
    // =========================
    suspend fun getTransactions(): List<Transaction> {
        val snap = db.collection("users")
            .document(uid())
            .collection("transactions")
            .get()
            .await()

        return snap.documents.mapNotNull { doc ->
            doc.toObject(Transaction::class.java)?.copy(id = doc.id)
        }
    }

    // =========================
    // REAL-TIME LISTENER
    // =========================
    fun listenTransactions(onChange: (List<Transaction>) -> Unit) {
        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .collection("transactions")
            .addSnapshotListener { snap, _ ->

                if (snap == null) {
                    onChange(emptyList())
                    return@addSnapshotListener
                }

                val list = snap.documents.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)?.copy(id = doc.id)
                }

                onChange(list)
            }
    }

    // =========================
    // DELETE TRANSACTION
    // =========================
    suspend fun deleteTransaction(id: String) {
        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .collection("transactions")
            .document(id)
            .delete()
            .await()
    }

    // =========================
    // UPDATE TRANSACTION
    // =========================
    suspend fun updateTransaction(transaction: Transaction) {
        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .collection("transactions")
            .document(transaction.id)
            .set(transaction)
            .await()
    }
}