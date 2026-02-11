package com.relyvo.izem.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepo {
    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isUserLoggedIn: Boolean get() = auth.currentUser != null
    val currentUserId: String? get() = auth.currentUser?.uid

    val isAnonymous: Boolean get() = auth.currentUser?.isAnonymous ?: true

    suspend fun signInAnonymously(): Boolean {
        return try {
            auth.signInAnonymously().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun linkAccount(credential: AuthCredential): Boolean {
        return try {
            auth.currentUser?.linkWithCredential(credential)?.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun signInWithCredential(credential: AuthCredential): Boolean {
        return try {
            auth.signInWithCredential(credential).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}