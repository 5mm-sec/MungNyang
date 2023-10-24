package com.example.mungnyang.Friend

import com.google.gson.annotations.SerializedName

data class ResponseFriendCountDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("count")
    val count: Int

)