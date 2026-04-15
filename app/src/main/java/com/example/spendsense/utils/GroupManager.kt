package com.example.spendsense.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object GroupManager {

    private var cachedGroupId: String? = null

    fun init(onReady: () -> Unit) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->

                cachedGroupId = doc.getString("groupId") ?: uid

                onReady()
            }
    }

    fun getGroupId(): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return cachedGroupId ?: uid
    }
}