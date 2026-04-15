package com.example.spendsense.data.repository

import com.google.firebase.auth.FirebaseAuth

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
}