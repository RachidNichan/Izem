package com.relyvo.izem.model

data class Phrase(
    val id: String = "",
    val categoryId: String = "", // مثلاً: "market", "emergency"
    val english: String = "",
    val arabic: String = "",
    val tamazight: String = "",
    val tifinagh: String = "",
    val audioUrl: String = "",
    val breakdownEn: String = "", // شرح تفكيك الجملة (اختياري)
    val breakdownAr: String = ""
)