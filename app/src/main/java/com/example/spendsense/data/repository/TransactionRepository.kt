package com.example.spendsense.repository

import com.example.spendsense.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TransactionRepository {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private fun uid(): String =
        auth.currentUser?.uid ?: throw Exception("User not logged in")

    suspend fun addTransaction(transaction: Transaction) {
        val ref = db.collection("users")
            .document(uid())
            .collection("transactions")
            .document()

        ref.set(transaction.copy(id = ref.id)).await()
    }

    suspend fun getTransactions(): List<Transaction> {
        val snap = db.collection("users")
            .document(uid())
            .collection("transactions")
            .get()
            .await()

        return snap.documents.mapNotNull {
            it.toObject(Transaction::class.java)
        }
    }

    fun listenTransactions(onChange: (List<Transaction>) -> Unit) {
        db.collection("users")
            .document(uid())
            .collection("transactions")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Transaction::class.java)
                } ?: emptyList()

                onChange(list)
            }
    }
}