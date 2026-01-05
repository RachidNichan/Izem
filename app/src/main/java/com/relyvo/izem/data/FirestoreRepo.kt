package com.relyvo.izem.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Word
import kotlinx.coroutines.tasks.await

class FirestoreRepo {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getCategories(): List<Category> {
        return try {
            val snapshot = db.collection("categories").get().await()
            val list = snapshot.toObjects<Category>()

            list.sortedBy { it.id }
        } catch (e: Exception) {
            println("❌ Error fetching categories: ${e.message}")
            emptyList()
        }
    }

    suspend fun getWordsByCategory(categoryId: String): List<Word> {
        return try {
            val snapshot = db.collection("words")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()

            val list = snapshot.toObjects<Word>()

            list.sortedBy { it.id }

        } catch (e: Exception) {
            println("❌ Error fetching words: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAllWords(): List<Word> {
        return try {
            val snapshot = db.collection("words").get().await()
            snapshot.toObjects<Word>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun uploadDataToFirestore() {
        val batch = db.batch()

        DataSource.categories.forEach { category ->
            val ref = db.collection("categories").document(category.id)
            batch.set(ref, category)
        }

        val allWords = DataSource.greetingsList + DataSource.familyList +
                DataSource.numbersList + DataSource.colorsList +
                DataSource.animalsList + DataSource.alphabetList

        allWords.forEach { word ->
            val docId = if(word.id.length < 5) "${word.categoryId}_${word.id}" else word.id
            val ref = db.collection("words").document(docId)
            batch.set(ref, word)
        }

        batch.commit().addOnSuccessListener {
            println("✅ تم رفع جميع البيانات بنجاح إلى Firebase!")
        }.addOnFailureListener { e ->
            println("❌ حدث خطأ أثناء الرفع: ${e.message}")
        }
    }
}