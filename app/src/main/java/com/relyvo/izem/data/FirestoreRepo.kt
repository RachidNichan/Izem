package com.relyvo.izem.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.UserProfile
import com.relyvo.izem.model.Word
import kotlinx.coroutines.tasks.await

class FirestoreRepo {

    private val db = FirebaseFirestore.getInstance()

    fun listenCategories(onResult: (List<Category>) -> Unit): ListenerRegistration {
        return db.collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val list = it.toObjects<Category>().sortedBy { c -> c.id }
                    onResult(list)
                }
            }
    }

    fun listenWordsByCategory(
        categoryId: String,
        onResult: (List<Word>) -> Unit
    ): ListenerRegistration {

        return db.collection("words")
            .whereEqualTo("categoryId", categoryId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.toObjects<Word>()
                        .sortedBy { it.id }

                    onResult(list)
                }
            }
    }

    fun listenAllWords(
        onResult: (List<Word>) -> Unit
    ) {
        db.collection("words")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    onResult(snapshot.toObjects())
                }
            }
    }

    fun saveQuizResult(userId: String, score: Int, totalQuestions: Int) {
        val quizData = hashMapOf(
            "score" to score,
            "totalQuestions" to totalQuestions,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "completed" to true
        )
        db.collection("users").document(userId)
            .collection("quiz_history").add(quizData)
    }

    fun updateUserProgress(userId: String, xpGained: Int, onNewTotalXp: (Int) -> Unit) {
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)

            val currentXP = if (snapshot.exists()) {
                snapshot.getLong("totalXP")?.toInt() ?: 0
            } else { 0 }

            val newTotalXP = currentXP + xpGained

            val data = hashMapOf(
                "totalXP" to newTotalXP,
                "lastActive" to com.google.firebase.Timestamp.now()
            )
            transaction.set(userRef, data, com.google.firebase.firestore.SetOptions.merge())

            newTotalXP
        }.addOnSuccessListener { newTotalXP ->
            onNewTotalXp(newTotalXP.toInt())
        }.addOnFailureListener { e ->
            println("❌ Error: ${e.message}")
        }
    }

    fun updateUserLevel(userId: String, levelName: String) {
        db.collection("users").document(userId)
            .update("currentLevel", levelName)
            .addOnFailureListener {
                db.collection("users").document(userId)
                    .set(mapOf("currentLevel" to levelName), com.google.firebase.firestore.SetOptions.merge())
            }
    }

    fun listenToUserProfile(userId: String, onResult: (UserProfile) -> Unit) {
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ Firestore Listen Error: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    try {
                        val user = snapshot.toObject<UserProfile>()

                        if (user != null) {
                            onResult(user)
                        } else {
                            onResult(UserProfile())
                        }
                    } catch (e: Exception) {
                        println("❌ Error converting data: ${e.message}")
                        onResult(UserProfile())
                    }
                } else {
                    onResult(UserProfile())
                }
            }
    }
}