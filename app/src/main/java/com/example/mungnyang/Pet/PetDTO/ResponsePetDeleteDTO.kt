package com.example.mungnyang.Pet.PetDTO

import com.google.gson.annotations.SerializedName

data class ResponsePetDeleteDTO (
    @SerializedName("response")
    var response : Boolean
)