package com.example.mungnyang.DiaryPage.Symptom

import com.google.gson.annotations.SerializedName

data class SearchResponseSymptomDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("symptomList")
    val symptomList: List<SearchSymptomDTO>

)