package com.example.mungnyang.Fragment.Walking

import com.example.mungnyang.DiaryPage.Diary.SearchDiaryDTO
import com.google.gson.annotations.SerializedName

data class SearchResponseWalkingDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("walkingDiaryList")
    val walkingDiaryList: List<SearchWalkingDTO>

)