package com.example.mungnyang.Fragment.ShareToDiary.Answer

import com.example.mungnyang.DiaryPage.Symptom.SearchSymptomDTO
import com.google.gson.annotations.SerializedName

data class SearchResponseDiaryAnswerDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("answerList")
    val answerList: List<SearchDiaryAnswerDTO>

)