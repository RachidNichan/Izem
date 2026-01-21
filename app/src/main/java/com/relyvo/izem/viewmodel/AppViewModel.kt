package com.relyvo.izem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.relyvo.izem.data.AuthRepo
import com.relyvo.izem.data.FirestoreRepo
import com.relyvo.izem.data.SettingsRepo
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.UserProfile
import com.relyvo.izem.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = FirestoreRepo()
    private val authRepo = AuthRepo()
    private val settingsRepo = SettingsRepo(application)

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _currentWords = MutableStateFlow<List<Word>>(emptyList())
    val currentWords = _currentWords.asStateFlow()

    private val _allWords = MutableStateFlow<List<Word>>(emptyList())
    val allWords = _allWords.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    private val _isArabic = MutableStateFlow(false)
    val isArabic = _isArabic.asStateFlow()

    private var wordsListener: ListenerRegistration? = null

    init {

        signInAnonymously()

        viewModelScope.launch {
            settingsRepo.isArabic.collect { savedIsArabic ->
                _isArabic.value = savedIsArabic
            }
        }

        // 🔹 Categories (Cloud + Offline + Live)
        repo.listenCategories { list ->
            _categories.value = list
        }

        // 🔹 All Words
        repo.listenAllWords { list ->
            _allWords.value = list
        }

        viewModelScope.launch {
            val uid = authRepo.currentUserId
            if (uid != null) {
                repo.listenToUserProfile(uid) { profile ->
                    _userProfile.value = profile
                }
            }
        }
    }

    private fun signInAnonymously() {
        viewModelScope.launch {
            if (!authRepo.isUserLoggedIn) {
                val success = authRepo.signInAnonymously()
                if (success) {
                    println("🦁 User signed in anonymously! UID: ${authRepo.currentUserId}")
                }
            } else {
                println("🦁 User already logged in. UID: ${authRepo.currentUserId}")
            }
        }
    }

    private fun getLevelName(xp: Int): String {
        return when {
            xp < 100 -> "Izem Amezwaru"
            xp < 500 -> "Izem Anlmad"
            xp < 1000 -> "Izem Amqran"
            else -> "Agellid n Izmawn"
        }
    }

    fun finishQuizSession(scoreInSession: Int) {
        val uid = authRepo.currentUserId ?: return

        repo.saveQuizResult(userId = uid, score = scoreInSession, totalQuestions = 10)

        repo.updateUserProgress(userId = uid, xpGained = scoreInSession) { newTotalXP ->

            val newLevel = getLevelName(newTotalXP)
            repo.updateUserLevel(uid, newLevel)

            println("🦁 Updated! XP: $newTotalXP, Level: $newLevel")
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
        val newValue = !_isArabic.value
        viewModelScope.launch {
            settingsRepo.setArabic(newValue)
        }
    }
}