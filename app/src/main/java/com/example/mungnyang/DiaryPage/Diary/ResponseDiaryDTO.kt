package com.example.mungnyang.DiaryPage.Diary

import com.google.gson.annotations.SerializedName

data class ResponseDiaryDTO (
    @SerializedName("response")
    var response : Boolean
)