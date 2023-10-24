package com.example.mungnyang.User.UserDTO

import com.google.gson.annotations.SerializedName

data class SearchResponseAccountDTO (

    @SerializedName("email")
    val email: String?,

    @SerializedName("password")
    var password: String?,

    @SerializedName("userName")
    val userName: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("nickName")
    val nickName: String?,

    @SerializedName("imageUrl")
    val imageUrl: String?
)