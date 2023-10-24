package com.example.mungnyang.DiaryPage.Diary

import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import com.example.mungnyang.DiaryPage.Numerical.NumericalOBJ
import com.example.mungnyang.DiaryPage.Numerical.SearchResponseNumericalDTO
import com.example.mungnyang.DiaryPage.Schedule.SearchResponseScheduleDTO
import com.example.mungnyang.DiaryPage.Symptom.SearchResponseSymptomDTO
import com.example.mungnyang.Fragment.EventDecorator
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object initSearchDayOBJ {

    private lateinit var calendarView: MaterialCalendarView // MaterialCalendarView 타입으로 선언
    val retrofit = RetrofitManager.instance

    // calendarView를 초기화하는 함수
    fun initCalendarView(view: MaterialCalendarView) {
        calendarView = view
    }

    fun searchAllDay(petID : Int) {

        // 수치 작성 페이지에 대해 DB에 저장되어 있는 데이터가 있으면 불러오고 빨간색 점을 찍어줌.
        val sendSearchDay = NumericalOBJ.retrofit.apiService.searchDay(petID)
        sendSearchDay.enqueue(object : Callback<SearchResponseNumericalDTO> {
            override fun onResponse(
                call: Call<SearchResponseNumericalDTO>,
                response: Response<SearchResponseNumericalDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val numericalList = responseDto.numericalList

                    if (numericalList.isNotEmpty()) {
                        for (searchList in numericalList) {

                            Log.d(ContentValues.TAG, "searchList : $searchList")

                            val searchPetID = searchList.petID.toString()
                            val searchSelectedDay = searchList.selectedDay.toString()

                            // 만약에 선택한 pet과 DB에서 불러온 pet이 일치하고, 선태한 날짜와 가져온 날짜가 일치하면 text에 출력
                            if (petID.toString() == searchPetID) {
                                Log.d("searchDay", searchSelectedDay)

                                // 서버로 petID에 해당하는 날짜들을 가져왔으면 그 날짜들에 dot찍기
                                val dateParts = searchSelectedDay.split("-")
                                val year = dateParts[0].toInt()
                                val month = dateParts[1].toInt()
                                val day = dateParts[2].toInt()

                                val eventDates: List<CalendarDay> = listOf(
                                    CalendarDay.from(year, month, day), // 예시: 일정이 있는 날짜
                                    // 추가적인 일정이 있는 날짜들을 리스트에 포함
                                )
                                calendarView.addDecorators(EventDecorator(Color.RED, eventDates))

                            }

                        }

                    } else {
                        Log.d(ContentValues.TAG, "No numerical found for the provided day")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }

            }

            override fun onFailure(call: Call<SearchResponseNumericalDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })

        // 이상 증상 페이지에 대해 DB에 저장되어 있는 데이터가 있으면 불러오고 해당 날짜에 빨간색 점을 찍어줌.
        val sendSymptomSearchDay = NumericalOBJ.retrofit.apiService.searchSymptomDay(petID)
        sendSymptomSearchDay.enqueue(object : Callback<SearchResponseSymptomDTO> {
            override fun onResponse(call: Call<SearchResponseSymptomDTO>, response: Response<SearchResponseSymptomDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val symptomList = responseDto.symptomList
                    if (symptomList.isNotEmpty()) {
                        for (searchList in symptomList) {

                            Log.d(ContentValues.TAG, "searchList : $searchList")

                            val searchPetID = searchList.petID.toString()
                            val searchSelectedDay = searchList.selectedDay.toString()

                            // 만약에 선택한 pet과 DB에서 불러온 pet이 일치하고, 선태한 날짜와 가져온 날짜가 일치하면 text에 출력
                            if (petID.toString() == searchPetID) {
                                Log.d("searchDay", searchSelectedDay)

                                // 서버로 petID에 해당하는 날짜들을 가져왔으면 그 날짜들에 dot찍기
                                val dateParts = searchSelectedDay.split("-")
                                val year = dateParts[0].toInt()
                                val month = dateParts[1].toInt()
                                val day = dateParts[2].toInt()

                                val eventDates: List<CalendarDay> = listOf(
                                    CalendarDay.from(year, month, day), // 예시: 일정이 있는 날짜
                                    // 추가적인 일정이 있는 날짜들을 리스트에 포함
                                )
                                calendarView.addDecorators(EventDecorator(Color.RED, eventDates))
                            }

                        }

                    } else {
                        Log.d(ContentValues.TAG, "No numerical found for the provided day")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }

            }

            override fun onFailure(call: Call<SearchResponseSymptomDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })

        // 일정 작성 페이지에 대해 DB에 저장되어 있는 데이터가 있으면 불러오고 해당 날짜에 빨간색 점을 찍어줌.
        val sendScheduleSearchDay = NumericalOBJ.retrofit.apiService.searchScheduleDay(petID)
        sendScheduleSearchDay.enqueue(object : Callback<SearchResponseScheduleDTO> {
            override fun onResponse(call: Call<SearchResponseScheduleDTO>, response: Response<SearchResponseScheduleDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val scheduleList = responseDto.scheduleList
                    if (scheduleList.isNotEmpty()) {
                        for (searchList in scheduleList) {

                            Log.d(ContentValues.TAG, "scheduleList : $scheduleList")

                            val searchPetID = searchList.petID.toString()
                            val searchSelectedDay = searchList.selectedDay.toString()

                            if (petID.toString() == searchPetID) {

                                // 서버로 petID에 해당하는 날짜들을 가져왔으면 그 날짜들에 dot찍기
                                val dateParts = searchSelectedDay.split("-")
                                val year = dateParts[0].toInt()
                                val month = dateParts[1].toInt()
                                val day = dateParts[2].toInt()

                                val eventDates: List<CalendarDay> = listOf(
                                    CalendarDay.from(year, month, day), // 예시: 일정이 있는 날짜
                                    // 추가적인 일정이 있는 날짜들을 리스트에 포함
                                )
                                calendarView.addDecorators(EventDecorator(Color.RED, eventDates))


                            }

                        }

                    } else {
                        Log.d(ContentValues.TAG, "No numerical found for the provided day")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }

            }

            override fun onFailure(call: Call<SearchResponseScheduleDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })

        // 일지 작성 페이지에 대해 DB에 저장되어 있는 데이터가 있으면 불러오고 해당 날짜에 빨간색 점을 찍어줌.
        val sendDiarySearchDay = NumericalOBJ.retrofit.apiService.searchDiaryDay(petID)
        sendDiarySearchDay.enqueue(object : Callback<SearchResponseDiaryDTO> {
            override fun onResponse(call: Call<SearchResponseDiaryDTO>, response: Response<SearchResponseDiaryDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val diaryList = responseDto.diaryList
                    if (diaryList.isNotEmpty()) {
                        for (searchList in diaryList) {

                            Log.d(ContentValues.TAG, "scheduleList : $diaryList")

                            val searchPetID = searchList.petID.toString()
                            val searchSelectedDay = searchList.selectedDay.toString()

                            if (petID.toString() == searchPetID) {

                                // 서버로 petID에 해당하는 날짜들을 가져왔으면 그 날짜들에 dot찍기
                                val dateParts = searchSelectedDay.split("-")
                                val year = dateParts[0].toInt()
                                val month = dateParts[1].toInt()
                                val day = dateParts[2].toInt()

                                val eventDates: List<CalendarDay> = listOf(
                                    CalendarDay.from(year, month, day), // 예시: 일정이 있는 날짜
                                    // 추가적인 일정이 있는 날짜들을 리스트에 포함
                                )
                                calendarView.addDecorators(EventDecorator(Color.RED, eventDates))

                            }

                        }

                    } else {
                        Log.d(ContentValues.TAG, "No numerical found for the provided day")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }

            }

            override fun onFailure(call: Call<SearchResponseDiaryDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })

    }
}