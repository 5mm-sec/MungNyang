package com.example.mungnyang.Fragment.Walking

import com.google.gson.annotations.SerializedName

data class ResponseWalkingCountDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("walkingCount")
    val walkingCount: Int

)