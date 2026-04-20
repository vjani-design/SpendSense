package com.example.spendsense.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    // =========================
    // REGISTER USER
    // =========================
    fun register(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .set(
                            mapOf(
                                "email" to email,
                                "password" to password
                            )
                        )

                    onResult(true, null)

                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // =========================
    // LOGIN USER
    // =========================
    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // =========================
    // GET CURRENT USER ID
    // =========================
    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    // =========================
    // LOGOUT USER
    // =========================
    fun logout() {
        auth.signOut()
    }

    // =========================
    // CHECK LOGIN STATUS
    // =========================
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    // =========================
// CHECK USER EXISTS (Firestore)
// =========================
    fun checkUserExists(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("email", email.trim())
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val docId = result.documents[0].id
                    onResult(true, docId)
                } else {
                    onResult(false, null)
                }
            }
            .addOnFailureListener {
                onResult(false, null)
            }
    }

    // =========================
// UPDATE PASSWORD (Firestore)
// =========================
    fun updatePasswordInFirestore(
        docId: String,
        newPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(docId)
            .update("password", newPassword)
            .addOnSuccessListener {
                onResult(true, "Password updated successfully")
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    fun addPasswordToAllUsers() {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->

                for (doc in result.documents) {

                    db.collection("users")
                        .document(doc.id)
                        .update("password", "123456")
                }
            }
    }

    // =========================
// LOGIN WITH FIRESTORE (NEW)
// =========================
    fun loginHybrid(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("email", email.trim().lowercase())
            .get()
            .addOnSuccessListener { result ->

                if (!result.isEmpty) {
                    // ✅ Found in Firestore
                    val storedPassword = result.documents[0].getString("password")

                    if (storedPassword == password) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Wrong password")
                    }

                } else {
                    // 🔁 Fallback to Firebase Auth
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener {
                            onResult(false, "User not found")
                        }
                }
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }
}