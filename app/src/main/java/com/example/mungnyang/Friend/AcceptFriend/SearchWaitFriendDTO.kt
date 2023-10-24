package com.example.mungnyang.Friend.AcceptFriend

import com.google.gson.annotations.SerializedName


data class SearchWaitFriendDTO (

    @SerializedName("addFriendNum")
    val addFriendNum: Int?,

    @SerializedName("userEmail")
    val userEmail: String?,

    @SerializedName("friendEmail")
    val friendEmail: String?,

    @SerializedName("status")
    val status: String?
)