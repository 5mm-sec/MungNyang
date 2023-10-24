package com.example.mungnyang.Fragment.ShareToDiary.Answer

import com.google.gson.annotations.SerializedName

data class SearchDiaryAnswerDTO (

    @SerializedName("diaryAnswerID")
    val diaryAnswerID: Int?,

    @SerializedName("userEmail")
    val userEmail: String?,

    @SerializedName("boardEmail")
    val boardEmail: String?,

    @SerializedName("userNickName")
    val userNickName: String?,

    @SerializedName("boardTitle")
    val boardTitle: String?,

    @SerializedName("answerText")
    val answerText: String?,

    @SerializedName("time")
    val time: String?,

    @SerializedName("userImageURL")
    val userImageURL: String?
)