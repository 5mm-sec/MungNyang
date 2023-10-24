package com.example.mungnyang.Fragment.Walking.WalkingAnswer

import com.google.gson.annotations.SerializedName

data class SearchResponseWalkingAnswerDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("answerList")
    val answerList: List<SearchWalkingAnswerDTO>

)