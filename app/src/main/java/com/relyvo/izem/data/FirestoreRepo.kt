package com.relyvo.izem.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObjects
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Word
import kotlinx.coroutines.tasks.await

class FirestoreRepo {

    private val db = FirebaseFirestore.getInstance()

    fun listenCategories(
        onResult: (List<Category>) -> Unit
    ) {
        db.collection("categories")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    println("❌ ${error.message}")
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.toObjects<Category>()
                        .sortedBy { it.id }

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