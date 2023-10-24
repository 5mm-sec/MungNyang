package com.example.mungnyang.Friend.AddFriend

import com.google.gson.annotations.SerializedName

data class ResponseAddFriendDTO (
    @SerializedName("response")
    var response : Boolean
)