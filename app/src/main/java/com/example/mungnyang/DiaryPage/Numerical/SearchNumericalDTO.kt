package com.example.mungnyang.DiaryPage.Numerical

import com.google.gson.annotations.SerializedName

data class SearchNumericalDTO (

    @SerializedName("petID")
    val petID: Int?,

    @SerializedName("selectedDay")
    val selectedDay: String?,

    @SerializedName("petKg")
    val petKg: String?,

    @SerializedName("petTemperature")
    val petTemperature: String?,

    @SerializedName("petBowl")
    val petBowl: String?,

    @SerializedName("petSnack")
    val petSnack: String?,

    @SerializedName("petWater")
    val petWater: String?
)