package com.relyvo.izem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.relyvo.izem.data.AuthRepo
import com.relyvo.izem.data.FirestoreRepo
import com.relyvo.izem.data.SettingsRepo
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Phrase
import com.relyvo.izem.model.Suggestion
import com.relyvo.izem.model.UserProfile
import com.relyvo.izem.model.Verb
import com.relyvo.izem.model.Word
// استيراد نماذج الاختبار من مجلد الـ model
import com.relyvo.izem.model.QuizQuestion
import com.relyvo.izem.model.QuizUiState
import com.relyvo.izem.model.IndexedLetter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repo: FirestoreRepo,
    private val authRepo: AuthRepo,
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

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

    private val _quizUiState = MutableStateFlow(QuizUiState())
    val quizUiState = _quizUiState.asStateFlow()

    // --- Listeners ---
    private var wordsListener: ListenerRegistration? = null
    private var allWordsListener: ListenerRegistration? = null
    private var profileListener: ListenerRegistration? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    // Grammar State: Phrases
    private val _phrases = MutableStateFlow<List<Phrase>>(emptyList())
    val phrases = _phrases.asStateFlow()

    // Grammar State: Verbs
    private val _verbs = MutableStateFlow<List<Verb>>(emptyList())
    val verbs = _verbs.asStateFlow()

    // Listeners to prevent memory leaks
    private var phrasesListener: ListenerRegistration? = null
    private var verbsListener: ListenerRegistration? = null

    private val _leaderboardUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val leaderboardUsers: StateFlow<List<UserProfile>> = _leaderboardUsers.asStateFlow()

    private var leaderboardListener: ListenerRegistration? = null

    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    init {
        observeUserStatus()

        viewModelScope.launch {
            settingsRepo.isArabic.collect { _isArabic.value = it }
        }

        repo.listenCategories { _categories.value = it }

        allWordsListener?.remove()
        allWordsListener = repo.listenAllWords { list ->
            _allWords.value = list
        }

        listenToVerbs()
        listenToPhrases()
        listenToLeaderboard()
        setupAuthObserver()
    }

    private fun setupAuthObserver() {
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            listenToLeaderboard()
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener!!)
    }

    private fun listenToLeaderboard() {
        leaderboardListener?.remove()

        leaderboardListener = repo.listenLeaderboard(limit = 20) { users ->
            _leaderboardUsers.value = users
        }
    }

    val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private fun observeUserStatus() {
        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                _isUserAnonymous.value = user.isAnonymous
                startProfileListener(user.uid)

                // Sync profile for leaderboard
                repo.syncUserProfile(user)

                saveFcmToken()
            } else {
                signInAnonymously()
            }
        }
        authListener?.let { auth.addAuthStateListener(it) }
    }

    private fun startProfileListener(uid: String) {
        profileListener?.remove()
        profileListener = repo.listenToUserProfile(uid) { profile ->
            _userProfile.value = profile
        }
    }

    fun updateDisplayName(newName: String, onComplete: (Boolean) -> Unit) {
        val uid = currentUserId ?: return
        repo.updateDisplayName(uid, newName) { success ->
            onComplete(success)
        }
    }

    private fun signInAnonymously() {
        viewModelScope.launch {
            authRepo.signInAnonymously()
        }
    }

    fun linkWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (idToken.isBlank()) {
            onError("ID Token is empty.")
            return
        }
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            if (authRepo.linkAccount(credential)) {
                _isUserAnonymous.value = auth.currentUser?.isAnonymous ?: false
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
                _isUserAnonymous.value = auth.currentUser?.isAnonymous ?: false
                onSuccess()
            } else {
                onError("Linking failed.")
            }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (idToken.isBlank()) {
            onError("ID Token is empty.")
            return
        }
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            if (authRepo.signInWithCredential(credential)) {
                onSuccess()
            } else { onError("Login failed.") }
        }
    }

    fun signInWithEmail(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Email or password cannot be empty.")
            return
        }
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

    fun listenWordsByCategory(categoryId: String) {
        wordsListener?.remove()
        wordsListener = repo.listenWordsByCategory(categoryId) { _currentWords.value = it }
    }

    // Fetch Phrases
    fun listenToPhrases(categoryId: String? = null) {
        phrasesListener?.remove() // Stop old listener
        phrasesListener = repo.listenPhrases(categoryId) { list ->
            _phrases.value = list
        }
    }

    // Fetch Verbs
    fun listenToVerbs() {
        verbsListener?.remove() // Stop old listener
        verbsListener = repo.listenVerbs { list ->
            _verbs.value = list
        }
    }

    fun toggleLanguage() {
        val newValue = !_isArabic.value
        viewModelScope.launch {
            settingsRepo.setArabic(newValue)
            authRepo.currentUserId?.let { uid ->
                repo.updateUserLanguage(uid, newValue)
            }
        }
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
                val imageUrl = if (imageUri != null) {
                    repo.uploadSuggestionFile(imageUri, "images")
                } else {
                    suggestion.imageUrl
                }

                val audioUrl = if (audioUri != null) {
                    repo.uploadSuggestionFile(audioUri, "audio")
                } else {
                    suggestion.audioUrl
                }

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

    fun saveFcmToken() {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                viewModelScope.launch {
                    kotlinx.coroutines.delay(1000)

                    val uid = auth.currentUser?.uid
                    if (uid != null && token != null) {
                        repo.updateFcmToken(uid, token)
                        repo.updateUserLanguage(uid, _isArabic.value)
                    }
                }
            }
    }

    fun startNewQuiz() {
        val words = _allWords.value
        if (words.size >= 4) {
            _quizUiState.value = QuizUiState(
                questions = generateDynamicQuiz(words),
                isLoading = false
            )
        }
    }

    fun selectOption(option: String) {
        if (!_quizUiState.value.isAnswerChecked) {
            _quizUiState.value = _quizUiState.value.copy(selectedAnswer = option)
        }
    }

    fun addLetterToSpelling(id: Int, char: String) {
        val state = _quizUiState.value
        if (!state.isAnswerChecked) {
            val updatedList = state.spellingGuessedLetters + IndexedLetter(id, char)
            val currentWordGuessed = updatedList.joinToString("") { it.char }
            _quizUiState.value = state.copy(
                spellingGuessedLetters = updatedList,
                selectedAnswer = currentWordGuessed
            )
        }
    }

    fun removeLetterFromSpelling(indexedLetter: IndexedLetter) {
        val state = _quizUiState.value
        if (!state.isAnswerChecked) {
            val updatedList = state.spellingGuessedLetters.filter { it.id != indexedLetter.id }
            val currentWordGuessed = updatedList.joinToString("") { it.char }
            _quizUiState.value = state.copy(
                spellingGuessedLetters = updatedList,
                selectedAnswer = currentWordGuessed
            )
        }
    }

    fun checkAnswer() {
        val state = _quizUiState.value
        if (state.questions.isEmpty() || state.isAnswerChecked) return

        val currentQuestion = state.questions[state.currentIndex]
        val correctAnswer = when (currentQuestion) {
            is QuizQuestion.TextQuestion -> currentQuestion.correctAnswer
            is QuizQuestion.ImageQuestion -> currentQuestion.correctAnswer
            is QuizQuestion.SpellingQuestion -> currentQuestion.correctAnswer
        }

        val isCorrect = state.selectedAnswer.trim().uppercase() == correctAnswer.trim().uppercase()
        val newScore = if (isCorrect) state.score + 1 else state.score

        _quizUiState.value = state.copy(
            isAnswerChecked = true,
            isCorrect = isCorrect,
            score = newScore
        )
    }

    fun moveToNextQuestion() {
        val state = _quizUiState.value
        val nextIndex = state.currentIndex + 1

        if (nextIndex < state.questions.size) {
            _quizUiState.value = state.copy(
                currentIndex = nextIndex,
                selectedAnswer = "",
                isAnswerChecked = false,
                isCorrect = false,
                spellingGuessedLetters = emptyList()
            )
        } else {
            _quizUiState.value = state.copy(isCompleted = true)
            saveQuizSessionResults(state.score, state.questions.size)
        }
    }

    private fun saveQuizSessionResults(score: Int, totalQuestions: Int) {
        val userId = currentUserId ?: return

        val xpGained = score * 10

        repo.saveQuizResult(userId, score, totalQuestions)

        if (xpGained > 0) {
            repo.updateUserProgress(userId, xpGained) { newTotalXp ->
                val newLevel = when {
                    newTotalXp >= 5000 -> "level_4" // Agellid n Izmawn
                    newTotalXp >= 1500 -> "level_3" // Izem Amqran
                    newTotalXp >= 300  -> "level_2" // Izem Anlmad
                    else -> "level_1"               // Izem Amezwaru
                }
                repo.updateUserLevel(userId, newLevel)
            }
        }
        repo.updateActiveDays(userId)
    }

    private fun generateDynamicQuiz(allWords: List<Word>): List<QuizQuestion> {
        if (allWords.size < 4) return emptyList()

        val quizWords = allWords.shuffled().take(10)

        return quizWords.map { currentWord ->
            val incorrectOptions = allWords
                .filter { it.id != currentWord.id }
                .shuffled()
                .take(3)
                .map { "${it.tifinagh} (${it.tamazight})" }

            val correctAnswerText = "${currentWord.tifinagh} (${currentWord.tamazight})"
            val options = (incorrectOptions + correctAnswerText).shuffled()

            if (currentWord.imageUrl.isNotEmpty()) {
                val randomChoice = (1..3).random()
                when (randomChoice) {
                    1 -> {
                        QuizQuestion.TextQuestion(
                            word = currentWord,
                            options = options,
                            correctAnswer = correctAnswerText
                        )
                    }
                    2 -> {
                        QuizQuestion.ImageQuestion(
                            word = currentWord,
                            imageUrl = currentWord.imageUrl,
                            options = options,
                            correctAnswer = correctAnswerText
                        )
                    }
                    else -> {
                        val targetWordText = currentWord.tifinagh.trim()

                        val scrambled = targetWordText.filter { !it.isWhitespace() }
                            .map { it.toString() }
                            .shuffled()

                        QuizQuestion.SpellingQuestion(
                            word = currentWord,
                            imageUrl = currentWord.imageUrl,
                            correctAnswer = targetWordText,
                            scrambledLetters = scrambled
                        )
                    }
                }
            } else {
                QuizQuestion.TextQuestion(
                    word = currentWord,
                    options = options,
                    correctAnswer = correctAnswerText
                )
            }
        }
    }

    fun sendRoarChallenge(targetUserId: String, onResult: (Boolean) -> Unit) {
        val senderId = currentUserId
        if (senderId == null) {
            // android.util.Log.e("IzemInteraction", "Cannot send roar: currentUserId is NULL")
            onResult(false)
            return
        }
        val senderName = _userProfile.value.displayName.ifEmpty { "Izem" }

        viewModelScope.launch {
            try {
                repo.sendInteraction(
                    senderId = senderId,
                    senderName = senderName,
                    targetId = targetUserId,
                    type = "roar"
                )
                onResult(true)
            } catch (e: Exception) {
                android.util.Log.e("IzemInteraction", "Failed to send roar: ${e.message}")
                onResult(false)
            }
        }
    }

    fun updateAvatarId(avatarId: Int, onComplete: (Boolean) -> Unit) {
        val uid = currentUserId ?: return
        repo.updateAvatarId(uid, avatarId, onComplete)
    }

    override fun onCleared() {
        super.onCleared()
        wordsListener?.remove()
        profileListener?.remove()
        allWordsListener?.remove()
        phrasesListener?.remove()
        verbsListener?.remove()
        leaderboardListener?.remove()
        authStateListener?.let {
            FirebaseAuth.getInstance().removeAuthStateListener(it)
        }
        authListener?.let { auth.removeAuthStateListener(it) }
    }
}