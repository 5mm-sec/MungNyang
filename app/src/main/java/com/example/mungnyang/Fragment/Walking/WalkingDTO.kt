package com.example.mungnyang.Fragment.Walking

data class WalkingDTO(
    var petID: Int?,
    var accountEmail: String?,
    var selectedDay: String?,
    var startTime: String?,
    var endTime: String?,
    var elapsedTime: CharSequence,
    var petKcalAmount: String?,
    var myKcalAmount: String?,
    var distance: String?,
    var mapImageURL: String?
)