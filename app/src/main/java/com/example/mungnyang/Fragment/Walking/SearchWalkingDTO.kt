package com.example.mungnyang.Fragment.Walking

import com.google.gson.annotations.SerializedName


data class SearchWalkingDTO (

    @SerializedName("walkingID")
    val walkingID: Int?,

    @SerializedName("petID")
    val petID: Int?,

    @SerializedName("accountEmail")
    val accountEmail: String?,

    @SerializedName("selectedDay")
    val selectedDay: String?,

    @SerializedName("startTime")
    val startTime: String?,

    @SerializedName("endTime")
    val endTime: String?,

    @SerializedName("elapsedTime")
    val elapsedTime: String?,

    @SerializedName("petKcalAmount")
    val petKcalAmount: String?,

    @SerializedName("myKcalAmount")
    val myKcalAmount: String?,

    @SerializedName("distance")
    val distance: String?,

    @SerializedName("mapImageURL")
    val mapImageURL: String?


)