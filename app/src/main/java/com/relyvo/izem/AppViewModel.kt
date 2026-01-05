package com.relyvo.izem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relyvo.izem.data.FirestoreRepo
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    init {
        fetchCategories()
        fetchAllWords()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _categories.value = repo.getCategories()
        }
    }

    private fun fetchAllWords() {
        viewModelScope.launch {
            _allWords.value = repo.getAllWords()
        }
    }

    fun fetchWordsByCategory(categoryId: String) {
        viewModelScope.launch {
            _currentWords.value = emptyList()
            _currentWords.value = repo.getWordsByCategory(categoryId)
        }
    }

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }
}