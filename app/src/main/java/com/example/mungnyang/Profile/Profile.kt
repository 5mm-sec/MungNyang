package com.example.mungnyang.Profile

class Profile  : Comparable<Profile> {
    var petID : String = ""
    var petImage : String = ""
    var petName: String = ""
    var accountEmail: String = ""
    var isClicked: Boolean = false

    constructor(petID: String, petImage: String, petName: String, accountEmail: String) {
        this.petID = petID
        this.petImage = petImage
        this.petName = petName
        this.accountEmail = accountEmail
    }

    override fun compareTo(other: Profile): Int {
        return compareValuesBy(this, other, {it.petName});
    }

    fun test(): String{
        return "$petImage)"
    }
    fun WalkingGetPetID(): String{
        return "$petID"
    }

    override fun toString(): String {
        return "Profile( petID = '$petID', petName='$petName', isClicked=$isClicked, accountEmail=$accountEmail)"
    }

    // 프로필 정보 업데이트 메서드
    fun updateProfile(petName: String, petImage: String) {
        this.petName = petName
        this.petImage = petImage
    }

}