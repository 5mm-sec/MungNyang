package com.example.mungnyang.User.Login

import com.google.gson.annotations.SerializedName

data class LoginDTO (
    @SerializedName("email")
    val email: String?,

    @SerializedName("password")
    var password: String?
    )