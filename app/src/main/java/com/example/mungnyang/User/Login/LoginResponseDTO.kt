package com.example.mungnyang.User.Login

import com.google.gson.annotations.SerializedName

data class LoginResponseDTO (
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("token")
    val token: String?
)