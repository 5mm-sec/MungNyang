package com.example.mungnyang.DiaryPage.Numerical

import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import com.example.mungnyang.DiaryPage.Diary.SearchResponseDiaryDTO
import com.example.mungnyang.DiaryPage.Schedule.SearchResponseScheduleDTO
import com.example.mungnyang.DiaryPage.Symptom.SearchResponseSymptomDTO
import com.example.mungnyang.DiaryPage.Symptom.SymptomOBJ
import com.example.mungnyang.Fragment.EventDecorator
import com.example.mungnyang.Fragment.diaryFragment
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.FragmentDiaryBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NumericalOBJ {

    private lateinit var calendarView: MaterialCalendarView // MaterialCalendarView 타입으로 선언
    val retrofit = RetrofitManager.instance

    // calendarView를 초기화하는 함수
    fun initCalendarView(view: MaterialCalendarView) {
        calendarView = view
    }

    fun addNumerical(
        petID: Int,
        selectedDate: String,
        petKg: String,
        petTemperature: String,
        petBowl: String,
        petSnack: String,
        petWater: String,
        activity: diaryFragment,
        binding: FragmentDiaryBinding// 이 부분에 실제 액티비티 타입을 넣어야 합니다.
    ) {

        val addNumericalDTO = NumericalDTO(
            petID,
            selectedDate,
            petKg,
            petTemperature,
            petBowl,
            petSnack,
            petWater
        )

        Log.d("API BEFORE", "hi $addNumericalDTO")

        val sendAdd = retrofit.apiService.addNumerical(addNumericalDTO)
        sendAdd.enqueue(object : Callback<ResponseNumericalDTO> {

            override fun onResponse(call: Call<ResponseNumericalDTO>, response: Response<ResponseNumericalDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "Response received: $responseDto");
                if (responseDto != null) {
                    Log.d(ContentValues.TAG, "Response received: $responseDto");

                    if (responseDto.response) {

                        if (activity.binding.diarypageKgResult.text == "" ||
                            activity.binding.diarypageTempResult.text == "" ||
                            activity.binding.diarypagePetbowlResult.text == "" ||
                            activity.binding.diarypageCannedfoodResult.text == "" ||
                            activity.binding.diarypageWaterResult.text == ""
                        ) {
                            Toast.makeText(activity.context, "필수 항목을 작성해주세요.", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(activity.context, "저장 완료!", Toast.LENGTH_SHORT).show()

                            // 서버로 데이터가 전송이 됐으면, dot 찍기
                            val dateParts = selectedDate.split("-")
                            val year = dateParts[0].toInt()
                            val month = dateParts[1].toInt()
                            val day = dateParts[2].toInt()

                            val eventDates: List<CalendarDay> = listOf(
                                CalendarDay.from(year, month, day) // 예시: 일정이 있는 날짜
                            )
                            calendarView.addDecorators(EventDecorator(Color.RED, eventDates))
                            Log.d("수치 증상 eventDates", eventDates.toString())
                        }

                    } else {
                        Toast.makeText(activity.context, "저장 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity.context, "응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseNumericalDTO>, t: Throwable) {
                Toast.makeText(activity.context, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}