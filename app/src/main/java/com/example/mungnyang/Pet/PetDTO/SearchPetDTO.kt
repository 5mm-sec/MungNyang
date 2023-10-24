package com.example.mungnyang.Pet.PetDTO

import com.google.gson.annotations.SerializedName

data class SearchPetDTO (

    @SerializedName("petID")
    val petID: String?,

    @SerializedName("accountEmail")
    val accountEmail: String?,

    @SerializedName("petName")
    val petName: String?,

    @SerializedName("birthday")
    val birthday: String?,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("species")
    val species: String?,

    @SerializedName("weight")
    val weight: Double,

    @SerializedName("neutered")
    val neutered: Boolean,

    @SerializedName("image")
    val image: String?
)