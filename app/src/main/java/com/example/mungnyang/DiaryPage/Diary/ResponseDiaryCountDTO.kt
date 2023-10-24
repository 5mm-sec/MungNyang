package com.example.mungnyang.DiaryPage.Diary

import com.google.gson.annotations.SerializedName

data class ResponseDiaryCountDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("diaryCount")
    val diaryCount: Int

)