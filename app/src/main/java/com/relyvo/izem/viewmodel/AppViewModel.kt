package com.relyvo.izem.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.relyvo.izem.data.FirestoreRepo
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val repo = FirestoreRepo()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _currentWords = MutableStateFlow<List<Word>>(emptyList())
    val currentWords = _currentWords.asStateFlow()

    private val _allWords = MutableStateFlow<List<Word>>(emptyList())
    val allWords = _allWords.asStateFlow()

    private val _isArabic = MutableStateFlow(false)
    val isArabic = _isArabic.asStateFlow()

    private var wordsListener: ListenerRegistration? = null

    init {

        // 🔹 Categories (Cloud + Offline + Live)
        repo.listenCategories { list ->
            _categories.value = list
        }

        // 🔹 All Words
        repo.listenAllWords { list ->
            _allWords.value = list
        }
    }

    // 🔹 Words by Category (Realtime)
    fun listenWordsByCategory(categoryId: String) {

        wordsListener?.remove()
        wordsListener = null

        wordsListener = repo.listenWordsByCategory(categoryId) { list ->
            _currentWords.value = list
        }
    }

    override fun onCleared() {
        super.onCleared()
        wordsListener?.remove()
    }

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }
}