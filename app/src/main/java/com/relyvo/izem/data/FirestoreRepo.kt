package com.relyvo.izem.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Suggestion
import com.relyvo.izem.model.UserProfile
import com.relyvo.izem.model.Word
import com.relyvo.izem.model.Phrase
import com.relyvo.izem.model.Verb
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject<Word>()?.copy(id = doc.id)
                    }
                    onResult(list)
                }
            }
    }

    fun listenAllWords(
        onResult: (List<Word>) -> Unit
    ): ListenerRegistration {
        return db.collection("words")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject<Word>()?.copy(id = doc.id)
                    }.filter { it.categoryId != "alphabet" }

                    onResult(list)
                }
            }
    }

    fun saveQuizResult(userId: String, score: Int, totalQuestions: Int) {
        val quizData = hashMapOf(
            "score" to score,
            "totalQuestions" to totalQuestions,
            "timestamp" to Timestamp.now(),
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
                "lastActive" to Timestamp.now()
            )
            transaction.set(userRef, data, SetOptions.merge())

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
                    .set(mapOf("currentLevel" to levelName), SetOptions.merge())
            }
    }

    fun listenToUserProfile(userId: String, onResult: (UserProfile) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject<UserProfile>()?.copy(userId = snapshot.id)
                    onResult(profile ?: UserProfile(userId = userId))
                } else {
                    onResult(UserProfile(userId = userId))
                }
            }
    }

    fun updateActiveDays(userId: String) {
        val userRef = db.collection("users").document(userId)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val lastDate = snapshot.getString("lastDateActive") ?: ""

            if (lastDate != today) {
                val currentDays = snapshot.getLong("learningDays")?.toInt() ?: 0
                transaction.update(userRef, "learningDays", currentDays + 1)
                transaction.update(userRef, "lastDateActive", today)
            }
            null
        }
    }

    fun markWordAsLearned(userId: String, wordId: String) {
        val userRef = db.collection("users").document(userId)

        userRef.update("learnedWords", FieldValue.arrayUnion(wordId))
            .addOnFailureListener {
                val data = hashMapOf("learnedWords" to listOf(wordId))
                userRef.set(data, SetOptions.merge())
            }
    }

    fun submitFullSuggestion(suggestion: Suggestion) {
        db.collection("suggestions")
            .add(suggestion.copy(createdAt = Timestamp.now()))
            .addOnSuccessListener {
                println("✅ Suggestion submitted successfully")
            }
            .addOnFailureListener { e ->
                println("❌ Error: ${e.message}")
            }
    }

    private val storage = com.google.firebase.storage.FirebaseStorage.getInstance()

    suspend fun uploadSuggestionFile(uri: android.net.Uri, folder: String): String {
        return try {
            val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val fileRef = storage.reference.child("suggestions/$folder/$fileName")

            fileRef.putFile(uri).await()

            val downloadUrl = fileRef.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            println("❌ Storage Upload Error: ${e.message}")
            ""
        }
    }

    fun updateUserLanguage(userId: String, isArabic: Boolean) {
        val lang = if (isArabic) "ar" else "en"
        db.collection("users").document(userId)
            .set(mapOf("language" to lang), SetOptions.merge())
    }

    fun updateFcmToken(userId: String, token: String) {
        val userRef = db.collection("users").document(userId)
        val data = hashMapOf("fcmToken" to token)

        userRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                // android.util.Log.d("IzemFCM", "🔥 Firestore Document Created/Updated for: $userId")
            }
    }

    fun syncUserProfile(user: com.google.firebase.auth.FirebaseUser) {
        val userRef = db.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { snapshot ->
            val data = if (snapshot.exists() && snapshot.contains("displayName")) {
                hashMapOf(
                    "lastActive" to Timestamp.now()
                )
            } else {
                hashMapOf(
                    "displayName" to (user.displayName ?: "Izem"),
                    "lastActive" to Timestamp.now()
                )
            }

            userRef.set(data, SetOptions.merge())
        }
    }

    fun updateDisplayName(userId: String, newName: String, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .update("displayName", newName)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // 🔹 1. Listen to Phrases (Realtime)
    fun listenPhrases(
        categoryId: String? = null, // Optional filter by category (e.g., "market")
        onResult: (List<Phrase>) -> Unit
    ): ListenerRegistration {

        var query: com.google.firebase.firestore.Query = db.collection("phrases")

        // If a category is provided, filter the phrases
        if (categoryId != null) {
            query = query.whereEqualTo("categoryId", categoryId)
        }

        return query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("❌ Error listening to phrases: ${error.message}")
                onResult(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val list = snapshot.toObjects(Phrase::class.java)
                // Optional: Sort by ID or a specific 'order' field if you have one
                onResult(list.sortedBy { it.id })
            } else {
                onResult(emptyList())
            }
        }
    }

    // 🔹 2. Listen to Verbs (Realtime)
    fun listenVerbs(
        onResult: (List<Verb>) -> Unit
    ): ListenerRegistration {

        return db.collection("verbs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ Error listening to verbs: ${error.message}")
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.toObjects(Verb::class.java)
                    // Sort alphabetically by English infinitive (or any other field)
                    onResult(list.sortedBy { it.infinitiveEn })
                } else {
                    onResult(emptyList())
                }
            }
    }

    fun listenLeaderboard(
        limit: Long = 20L,
        onResult: (List<UserProfile>) -> Unit
    ): ListenerRegistration {
        return db.collection("users")
            .orderBy("totalXP", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ Error listening to leaderboard: ${error.message}")
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject<UserProfile>()?.copy(userId = doc.id)
                    }
                    onResult(list)
                } else {
                    onResult(emptyList())
                }
            }
    }

}