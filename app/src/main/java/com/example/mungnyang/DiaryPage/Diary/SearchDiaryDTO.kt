package com.example.mungnyang.DiaryPage.Diary

import com.google.gson.annotations.SerializedName


data class SearchDiaryDTO (

    @SerializedName("petID")
    val petID: Int?,

    @SerializedName("selectedDay")
    val selectedDay: String?,

    @SerializedName("diaryTime")
    val diaryTime: String?,

    @SerializedName("diaryDetail")
    val diaryDetail: String?,

    @SerializedName("diaryTitle")
    val diaryTitle: String?,

    @SerializedName("userEmail")
    val userEmail: String?


)