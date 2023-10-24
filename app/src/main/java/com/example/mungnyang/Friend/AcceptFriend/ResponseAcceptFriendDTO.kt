package com.example.mungnyang.Friend.AcceptFriend

import com.google.gson.annotations.SerializedName

data class ResponseAcceptFriendDTO (
    @SerializedName("response")
    var response : Boolean
)