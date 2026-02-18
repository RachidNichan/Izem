package com.relyvo.izem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.relyvo.izem.data.AuthRepo
import com.relyvo.izem.data.FirestoreRepo
import com.relyvo.izem.data.SettingsRepo
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Suggestion
import com.relyvo.izem.model.UserProfile
import com.relyvo.izem.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = FirestoreRepo()
    private val authRepo = AuthRepo()
    private val auth = FirebaseAuth.getInstance()
    private val settingsRepo = SettingsRepo(application)

    // --- UI States ---
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

    private val _isUserAnonymous = MutableStateFlow(true)
    val isUserAnonymous = _isUserAnonymous.asStateFlow()

    // --- Listeners ---
    private var wordsListener: ListenerRegistration? = null
    private var profileListener: ListenerRegistration? = null

    init {
        observeUserStatus()

        viewModelScope.launch {
            settingsRepo.isArabic.collect { _isArabic.value = it }
        }

        repo.listenCategories { _categories.value = it }
        repo.listenAllWords { _allWords.value = it }
    }

    private fun observeUserStatus() {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                _isUserAnonymous.value = user.isAnonymous
                startProfileListener(user.uid)
            } else {
                signInAnonymously()
            }
        }
    }

    private fun startProfileListener(uid: String) {
        profileListener?.remove()
        profileListener = repo.listenToUserProfile(uid) { profile ->
            _userProfile.value = profile
        }
    }

    private fun signInAnonymously() {
        viewModelScope.launch {
            authRepo.signInAnonymously()
        }
    }

    fun linkWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            if (authRepo.linkAccount(credential)) {
                onSuccess()
            } else {
                onError("Account linking failed.")
            }
        }
    }

    fun linkWithEmail(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.length < 6) {
            onError("Invalid email or password.")
            return
        }
        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, pass)
        viewModelScope.launch {
            if (authRepo.linkAccount(credential)) {
                authRepo.currentUser?.sendEmailVerification()
                onSuccess()
            } else {
                onError("Linking failed.")
            }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            if (authRepo.signInWithCredential(credential)) {
                onSuccess()
            } else { onError("Login failed.") }
        }
    }

    fun signInWithEmail(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, pass)
        viewModelScope.launch {
            if (authRepo.signInWithCredential(credential)) {
                onSuccess()
            } else { onError("Login failed.") }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            profileListener?.remove()
            auth.signOut()
            onComplete()
        }
    }

    fun onWordClicked(wordId: String) {
        val uid = authRepo.currentUserId ?: return
        if (!_userProfile.value.learnedWords.contains(wordId)) {
            viewModelScope.launch { repo.markWordAsLearned(uid, wordId) }
        }
    }

    fun trackVisit() {
        val uid = authRepo.currentUserId ?: return
        repo.updateActiveDays(uid)
    }

    fun finishQuizSession(scoreInSession: Int) {
        val uid = authRepo.currentUserId ?: return
        repo.saveQuizResult(uid, scoreInSession, 10)
        repo.updateUserProgress(uid, scoreInSession) { /* سيتحدث تلقائياً عبر الـ Listener */ }
    }

    fun listenWordsByCategory(categoryId: String) {
        wordsListener?.remove()
        wordsListener = repo.listenWordsByCategory(categoryId) { _currentWords.value = it }
    }

    fun toggleLanguage() {
        val newValue = !_isArabic.value
        viewModelScope.launch { settingsRepo.setArabic(newValue) }
    }

    fun submitSuggestion(
        suggestion: Suggestion,
        imageUri: android.net.Uri?,
        audioUri: android.net.Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = authRepo.currentUserId ?: return

        viewModelScope.launch {
            try {
                val imageUrl = imageUri?.let { repo.uploadSuggestionFile(it, "images") } ?: ""
                val audioUrl = audioUri?.let { repo.uploadSuggestionFile(it, "audio") } ?: ""

                val finalSuggestion = suggestion.copy(
                    userId = uid,
                    imageUrl = imageUrl,
                    audioUrl = audioUrl,
                    status = "pending"
                )

                repo.submitFullSuggestion(finalSuggestion)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error submitting")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wordsListener?.remove()
        profileListener?.remove()
    }
}