package com.example.mungnyang.Friend.AcceptFriend

import com.google.gson.annotations.SerializedName

data class ResponseWaitFriendDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("waitFriendList")
    val waitFriendList: List<SearchWaitFriendDTO>

)