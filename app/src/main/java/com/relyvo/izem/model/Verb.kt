package com.relyvo.izem.model

data class Verb(
    val id: String = "",
    val infinitiveEn: String = "", // To eat
    val infinitiveAr: String = "", // أكل
    val infinitiveTamazight: String = "", // Ech
    val infinitiveTifinagh: String = "", // ⵛⵛ
    val audioUrl: String = "",

    // تصريف الماضي (Key = الضمير، Value = الفعل المصرف)
    val pastTense: Map<String, String> = emptyMap(),
    // تصريف المضارع
    val presentTense: Map<String, String> = emptyMap(),
    // تصريف المستقبل
    val futureTense: Map<String, String> = emptyMap()
)