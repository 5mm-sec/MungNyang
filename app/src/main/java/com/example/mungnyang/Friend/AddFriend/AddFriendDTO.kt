package com.example.mungnyang.Friend.AddFriend

import com.google.gson.annotations.SerializedName

data class AddFriendDTO(
    @SerializedName("userEmail")
    var userEmail: String?,

    @SerializedName("friendEmail")
    var friendEmail: String?,

    @SerializedName("status")
    var status: String?

)