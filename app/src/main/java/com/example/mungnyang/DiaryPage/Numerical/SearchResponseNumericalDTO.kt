package com.example.mungnyang.DiaryPage.Numerical

import com.google.gson.annotations.SerializedName

data class SearchResponseNumericalDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("numericalList")
    val numericalList: List<SearchNumericalDTO>

)