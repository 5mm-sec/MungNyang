package com.example.mungnyang.Pet.PetDTO

import com.google.gson.annotations.SerializedName

data class ResponsePetDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("petList")
    val petList: List<SearchPetDTO>
)