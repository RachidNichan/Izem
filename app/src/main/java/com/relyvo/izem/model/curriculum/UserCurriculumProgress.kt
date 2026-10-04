package com.relyvo.izem.model.curriculum

data class UserCurriculumProgress(
    val completedLessons: Map<String, Int> = emptyMap(), // lessonId -> stars earned (1..3)
    val currentUnitId: String = "unit_1",
    val currentLessonId: String = "unit_1_lesson_1",
    val hearts: Int = 5,
    val maxHearts: Int = 5,
    val lastHeartRefillTimestamp: Long = 0L,
    val weakWordIds: List<String> = emptyList(), // Words needing SRS review
    val isPremium: Boolean = false
) {
    fun isLessonCompleted(lessonId: String): Boolean = completedLessons.containsKey(lessonId)

    fun getLessonStars(lessonId: String): Int = completedLessons[lessonId] ?: 0

    fun isLessonUnlocked(lessonId: String, allUnitLessons: List<Lesson>): Boolean {
        val index = allUnitLessons.indexOfFirst { it.id == lessonId }
        if (index <= 0) return true // First lesson is always unlocked
        val previousLessonId = allUnitLessons[index - 1].id
        return isLessonCompleted(previousLessonId)
    }
}
