package com.example.mungnyang.Fragment

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.example.mungnyang.DiaryPage.Diary.ResponseDiaryDTO
import com.example.mungnyang.DiaryPage.Diary.initSearchDayOBJ
import com.example.mungnyang.DiaryPage.Numerical.ResponseNumericalDTO
import com.example.mungnyang.DiaryPage.Schedule.ResponseScheduleDTO
import com.example.mungnyang.DiaryPage.Symptom.ResponseSymptomDTO
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.FragmentDiaryBinding
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DeleteDiaryToSelectedDayOBJ {

    val retrofit = RetrofitManager.instance


    // 수치 작성 데이터 삭제
    
    fun numericalDelete(
        selectedDate: String,
        petIDInt: Int,
        activity: diaryFragment,
        binding: FragmentDiaryBinding,
        calendarView: MaterialCalendarView
    ) {
        val sendNumericalDelete = retrofit.apiService.deleteNumerical(selectedDate, petIDInt)
        sendNumericalDelete.enqueue(object : Callback<ResponseNumericalDTO> {

            override fun onResponse(call: Call<ResponseNumericalDTO>, response: Response<ResponseNumericalDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "Response received: $responseDto");
                if (responseDto != null) {
                    Log.d(ContentValues.TAG, "Response received: $responseDto");

                    if (responseDto.response) {

                        if (binding.diarypageKgResult.text.toString() == "" || binding.diarypageTempResult.text.toString() == "" ||
                            binding.diarypagePetbowlResult.text.toString() == "" || binding.diarypageCannedfoodResult.text.toString() == "" ||
                            binding.diarypageWaterResult.text.toString() == "") {

                            Toast.makeText(activity.context, "삭제 할 항목이 없습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(activity.context, "삭제 완료!", Toast.LENGTH_SHORT).show()

                            binding.diarypageScaleImage.setImageResource(R.drawable.diarypage_scale_image)
                            binding.diarypageTemperatureImage.setImageResource(R.drawable.diarypage_temperature_image)
                            binding.diarypagePetbowlImage.setImageResource(R.drawable.diarypage_petbowl_image)
                            binding.diarypageCannedfoodImage.setImageResource(R.drawable.diarypage_cannedfood_image)
                            binding.diarypageWaterImage.setImageResource(R.drawable.diarypage_water_image)

                            binding.diarypageKgResult.text = ""
                            binding.diarypageTempResult.text = ""
                            binding.diarypagePetbowlResult.text = ""
                            binding.diarypageCannedfoodResult.text = ""
                            binding.diarypageWaterResult.text = ""

                            // 캘린더에서 dot을 제거하는 코드 추가
                            calendarView.removeDecorators() // 모든 데코레이터 제거
                            initSearchDayOBJ.searchAllDay(petIDInt) // 변경된 데이터로 다시 dot을 추가
                        }

                    } else {
                        Toast.makeText(activity.context, "삭제 불가능!", Toast.LENGTH_SHORT)
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
    
    // 이상 증상 작성 데이터 삭제
    fun symptomDelete(
        selectedDate: String,
        petIDInt: Int,
        activity: diaryFragment,
        binding: FragmentDiaryBinding,
        calendarView: MaterialCalendarView
    ) {
        val sendSymptomDelete = retrofit.apiService.deleteSymptom(selectedDate, petIDInt)
        sendSymptomDelete.enqueue(object : Callback<ResponseSymptomDTO> {

            override fun onResponse(call: Call<ResponseSymptomDTO>, response: Response<ResponseSymptomDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "Response received: $responseDto");
                if (responseDto != null) {
                    Log.d(ContentValues.TAG, "Response received: $responseDto");

                    if (responseDto.response) {

                        if (binding.diarypageSymptomCheckResult.text.toString() == "") {
                            Toast.makeText(activity.context, "삭제 할 항목이 없습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(activity.context, "삭제 완료!", Toast.LENGTH_SHORT).show()

                            binding.diarypageSymptomCheckResult.text = ""

                            // 캘린더에서 dot을 제거하는 코드 추가
                            calendarView.removeDecorators() // 모든 데코레이터 제거
                            initSearchDayOBJ.searchAllDay(petIDInt) // 변경된 데이터로 다시 dot을 추가
                        }

                    } else {
                        Toast.makeText(activity.context, "삭제 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity.context, "응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseSymptomDTO>, t: Throwable) {
                Toast.makeText(activity.context, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    
    // 일정 작성 데이터 삭제
    fun scheduleDelete(
        selectedDate: String,
        petIDInt: Int,
        activity: diaryFragment,
        binding: FragmentDiaryBinding,
        calendarView: MaterialCalendarView
    ) {
        val sendScheduleDelete = retrofit.apiService.deleteSchedule(selectedDate, petIDInt)
        sendScheduleDelete.enqueue(object : Callback<ResponseScheduleDTO> {

            override fun onResponse(call: Call<ResponseScheduleDTO>, response: Response<ResponseScheduleDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "Response received: $responseDto");
                if (responseDto != null) {
                    Log.d(ContentValues.TAG, "Response received: $responseDto");

                    if (responseDto.response) {

                        if (binding.diarypageScheduleResult.text.toString() == "") {
                            Toast.makeText(activity.context, "삭제 할 항목이 없습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(activity.context, "삭제 완료!", Toast.LENGTH_SHORT).show()
                            binding.diarypageScheduleResult.text = ""
                            // 캘린더에서 dot을 제거하는 코드 추가
                            calendarView.removeDecorators() // 모든 데코레이터 제거
                            initSearchDayOBJ.searchAllDay(petIDInt) // 변경된 데이터로 다시 dot을 추가
                        }

                    } else {
                        Toast.makeText(activity.context, "삭제 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity.context, "응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseScheduleDTO>, t: Throwable) {
                Toast.makeText(activity.context, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // 일지 작성 데이터 삭제
    fun diaryDelete(
        selectedDate: String,
        petIDInt: Int,
        activity: diaryFragment,
        binding: FragmentDiaryBinding,
        calendarView: MaterialCalendarView
    ) {
        val sendDiaryDelete = retrofit.apiService.deleteDiary(selectedDate, petIDInt)
        sendDiaryDelete.enqueue(object : Callback<ResponseDiaryDTO> {

            override fun onResponse(call: Call<ResponseDiaryDTO>, response: Response<ResponseDiaryDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "Response received: $responseDto");
                if (responseDto != null) {
                    Log.d(ContentValues.TAG, "Response received: $responseDto");

                    if (responseDto.response) {

                        if (binding.diaryResult.text.toString() == "") {
                            Toast.makeText(activity.context, "삭제 할 항목이 없습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(activity.context, "삭제 완료!", Toast.LENGTH_SHORT).show()
                            binding.diaryResult.text = ""
                            // 캘린더에서 dot을 제거하는 코드 추가
                            calendarView.removeDecorators() // 모든 데코레이터 제거
                            initSearchDayOBJ.searchAllDay(petIDInt) // 변경된 데이터로 다시 dot을 추가
                        }

                    } else {
                        Toast.makeText(activity.context, "삭제 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity.context, "응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseDiaryDTO>, t: Throwable) {
                Toast.makeText(activity.context, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}