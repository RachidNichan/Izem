package com.relyvo.izem.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepo {
    private val auth = FirebaseAuth.getInstance()

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun signInAnonymously(): Boolean {
        return try {
            auth.signInAnonymously().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}