package com.example.mungnyang.DiaryPage.Schedule

import com.google.gson.annotations.SerializedName


data class SearchScheduleDTO (

    @SerializedName("petID")
    val petID: Int?,

    @SerializedName("selectedDay")
    val selectedDay: String?,

    @SerializedName("scheduleTitle")
    val scheduleTitle: String?,

    @SerializedName("scheduleDetail")
    val scheduleDetail: String?,

    @SerializedName("scheduleTime")
    val scheduleTime: String?
)