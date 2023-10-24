package com.example.mungnyang.Fragment.ShareToDiary.Answer

import com.google.gson.annotations.SerializedName

data class ResponseDiaryAnswerCountDTO(
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("answerCount")
    var answerCount: Int
)

