package com.relyvo.izem.model.curriculum

data class Lesson(
    val id: String = "",
    val unitId: String = "",
    val order: Int = 1,
    val titleEn: String = "",
    val titleAr: String = "",
    val descriptionEn: String = "",
    val descriptionAr: String = "",
    val xpReward: Int = 15,
    val steps: List<ExerciseStep> = emptyList()
)

enum class LessonStatus {
    LOCKED,
    AVAILABLE,
    COMPLETED
}
