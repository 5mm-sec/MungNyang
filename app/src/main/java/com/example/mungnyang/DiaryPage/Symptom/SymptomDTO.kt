package com.example.mungnyang.DiaryPage.Symptom

data class SymptomDTO(
    val selectedDay: String?,
    val petID: Int?,
    val symptomList: List<String>?
)