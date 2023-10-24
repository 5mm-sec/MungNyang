package com.example.mungnyang.DiaryPage.Schedule

import com.google.gson.annotations.SerializedName

data class SearchResponseScheduleDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("scheduleList")
    val scheduleList: List<SearchScheduleDTO>

)