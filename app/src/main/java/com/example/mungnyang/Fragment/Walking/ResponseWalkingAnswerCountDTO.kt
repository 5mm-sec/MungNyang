package com.example.mungnyang.Fragment.Walking

import com.google.gson.annotations.SerializedName

data class ResponseWalkingAnswerCountDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("walkingAnswerCount")
    val walkingAnswerCount: Int

)