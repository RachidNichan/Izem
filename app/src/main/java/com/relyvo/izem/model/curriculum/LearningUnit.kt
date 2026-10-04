package com.relyvo.izem.model.curriculum

data class LearningUnit(
    val id: String = "",
    val order: Int = 1,
    val titleEn: String = "",
    val titleAr: String = "",
    val descriptionEn: String = "",
    val descriptionAr: String = "",
    val themeColorHex: Long = 0xFF2E7D32, // Default Forest Green / Izem Green
    val iconName: String = "school",
    val guidebook: UnitGuidebook? = null,
    val lessons: List<Lesson> = emptyList()
)

data class UnitGuidebook(
    val titleEn: String = "",
    val titleAr: String = "",
    val summaryEn: String = "",
    val summaryAr: String = "",
    val sections: List<GuidebookSection> = emptyList()
)

data class GuidebookSection(
    val headingEn: String = "",
    val headingAr: String = "",
    val bodyEn: String = "",
    val bodyAr: String = "",
    val examples: List<GuidebookExample> = emptyList()
)

data class GuidebookExample(
    val tamazight: String = "",
    val tifinagh: String = "",
    val translationEn: String = "",
    val translationAr: String = ""
)
