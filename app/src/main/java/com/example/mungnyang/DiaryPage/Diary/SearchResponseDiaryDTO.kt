package com.example.mungnyang.DiaryPage.Diary

import com.google.gson.annotations.SerializedName

data class SearchResponseDiaryDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("diaryList")
    val diaryList: List<SearchDiaryDTO>

)