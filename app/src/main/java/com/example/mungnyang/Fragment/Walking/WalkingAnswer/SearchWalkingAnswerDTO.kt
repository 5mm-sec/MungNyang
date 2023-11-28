package com.example.mungnyang.Fragment.Walking.WalkingAnswer

import com.google.gson.annotations.SerializedName

data class SearchWalkingAnswerDTO (

    @SerializedName("walkingAnswerID")
    val walkingAnswerID: Int?,

    @SerializedName("walkingID")
    val walkingID: Int?,

    @SerializedName("userEmail")
    val userEmail: String?,

    @SerializedName("boardEmail")
    val boardEmail: String?,

    @SerializedName("userNickName")
    val userNickName: String?,

    @SerializedName("petName")
    val petName: String?,

    @SerializedName("selectedDay")
    val selectedDay: String?,

    @SerializedName("answerText")
    val answerText: String?,

    @SerializedName("time")
    val time: String?,

    @SerializedName("userImageURL")
    val userImageURL: String?
)