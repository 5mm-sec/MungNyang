package com.example.mungnyang.DiaryPage.Symptom
import com.google.gson.annotations.SerializedName

data class SearchSymptomDTO (

    @SerializedName("petID")
    val petID: Int?,

    @SerializedName("selectedDay")
    val selectedDay: String?,

    @SerializedName("symptomList")
    val symptomList: List<String>?
)