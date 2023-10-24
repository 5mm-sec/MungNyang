package com.example.mungnyang.Friend.AcceptFriend

import com.google.gson.annotations.SerializedName

data class AcceptFriendDTO(

    @SerializedName("userEmail")
    var userEmail: String?,

    @SerializedName("friendEmail")
    var friendEmail: String?,

    @SerializedName("status")
    val status: String?
)