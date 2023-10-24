package com.example.mungnyang.DiaryPage.Schedule

class Schedule {
    var petID : String = ""
    var scheduleTitle : String = ""
    var scheduleDetail: String = ""
    var scheduleTime: String = ""
    var scheduleDay: String = "" // 날짜 필드 추가

    constructor(petID: String, scheduleTitle: String, scheduleDetail: String, scheduleTime: String, scheduleDay: String) {
        this.petID = petID
        this.scheduleTitle = scheduleTitle
        this.scheduleDetail = scheduleDetail
        this.scheduleTime = scheduleTime
        this.scheduleDay = scheduleDay // 초기화
    }
}