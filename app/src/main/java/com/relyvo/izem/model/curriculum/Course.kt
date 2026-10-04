package com.relyvo.izem.model.curriculum

data class Course(
    val id: String = "tamazight_a1",
    val titleEn: String = "Tamazight Foundations (A1)",
    val titleAr: String = "أساسيات الأمازيغية (المستوى الأول)",
    val descriptionEn: String = "Master Tifinagh script, essential vocabulary, grammar foundations, and everyday phrases.",
    val descriptionAr: String = "تعلم كتابة ونطق حروف التيفيناغ، أهم المفردات اليومية، وقواعد تكوين الجمل.",
    val units: List<LearningUnit> = emptyList()
)
