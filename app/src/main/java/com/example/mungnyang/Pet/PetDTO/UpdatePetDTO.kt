package com.example.mungnyang.Pet.PetDTO

import com.google.gson.annotations.SerializedName

data class UpdatePetDTO(

    @SerializedName("petID")
    var petID: Int?,

    @SerializedName("accountEmail")
    var accountEmail: String?,

    @SerializedName("petName")
    var petName: String?,

    @SerializedName("birthday")
    var birthday: String?,

    @SerializedName("gender")
    var gender: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("species")
    val species: String?,

    @SerializedName("weight")
    val weight: Double?,

    @SerializedName("neutered")
    val neutered: Boolean?,

    @SerializedName("image")
    val image: String?
)