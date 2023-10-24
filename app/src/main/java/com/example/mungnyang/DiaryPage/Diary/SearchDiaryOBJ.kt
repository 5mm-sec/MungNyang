package com.example.mungnyang.DiaryPage.Diary

import android.content.ContentValues
import android.util.Log
import com.example.mungnyang.DiaryPage.Numerical.SearchResponseNumericalDTO
import com.example.mungnyang.DiaryPage.Schedule.SearchResponseScheduleDTO
import com.example.mungnyang.DiaryPage.Symptom.SearchResponseSymptomDTO
import com.example.mungnyang.Fragment.diaryFragment
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.FragmentDiaryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SearchDiaryOBJ {
    private var searchPetID = ""
    private var searchSelectedDay = ""

    val retrofit = RetrofitManager.instance

    // calendarView를 초기화하는 함수

    fun search(
        selectedDate: String,
        petID: String,
        activity: diaryFragment,
        binding: FragmentDiaryBinding// 이 부분에 실제 액티비티 타입을 넣어야 합니다.
    ) {
        Log.d("오비제이 선택 날짜", selectedDate)

        val sendSearch = retrofit.apiService.searchNumerical(selectedDate)
        sendSearch.enqueue(object : Callback<SearchResponseNumericalDTO> {
            override fun onResponse(call: Call<SearchResponseNumericalDTO>, response: Response<SearchResponseNumericalDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val numericalList = responseDto.numericalList

                    if (numericalList.isNotEmpty()) {
                        for (numerical in numericalList) {

                            Log.d(ContentValues.TAG, "numerical : $numerical")

                            searchPetID = numerical.petID.toString()
                            searchSelectedDay = numerical.selectedDay.toString()

                            // 만약에 선택한 pet과 DB에서 불러온 pet이 일치하고, 선태한 날짜와 가져온 날짜가 일치하면 text에 출력
                            if (petID == searchPetID && selectedDate == searchSelectedDay) {

                                activity.binding.diarypageKgResult.text = numerical.petKg.toString()
                                activity.binding.diarypageTempResult.text = numerical.petTemperature.toString()
                                activity.binding.diarypagePetbowlResult.text = numerical.petBowl.toString()
                                activity.binding.diarypageCannedfoodResult.text = numerical.petSnack.toString()
                                activity.binding.diarypageWaterResult.text = numerical.petWater.toString()

                                activity.binding.diarypageScaleImage.setImageResource(R.drawable.scale_click)
                                activity.binding.diarypageTemperatureImage.setImageResource(R.drawable.temperature_click)
                                activity.binding.diarypagePetbowlImage.setImageResource(R.drawable.petbowl_click)
                                activity.binding.diarypageCannedfoodImage.setImageResource(R.drawable.snack_click)
                                activity.binding.diarypageWaterImage.setImageResource(R.drawable.water_click)

                            } else {
                                activity.binding.diarypageKgResult.text = ""
                                activity.binding.diarypageTempResult.text = ""
                                activity.binding.diarypagePetbowlResult.text = ""
                                activity.binding.diarypageCannedfoodResult.text = ""
                                activity.binding.diarypageWaterResult.text = ""

                                activity.binding.diarypageScaleImage.setImageResource(R.drawable.diarypage_scale_image)
                                activity.binding.diarypageTemperatureImage.setImageResource(R.drawable.diarypage_temperature_image)
                                activity.binding.diarypagePetbowlImage.setImageResource(R.drawable.diarypage_petbowl_image)
                                activity.binding.diarypageCannedfoodImage.setImageResource(R.drawable.diarypage_cannedfood_image)
                                activity.binding.diarypageWaterImage.setImageResource(R.drawable.diarypage_water_image)
                            }

                        }

                    } else {
                        activity.binding.diarypageKgResult.text = ""
                        activity.binding.diarypageTempResult.text = ""
                        activity.binding.diarypagePetbowlResult.text = ""
                        activity.binding.diarypageCannedfoodResult.text = ""
                        activity.binding.diarypageWaterResult.text = ""

                        activity.binding.diarypageScaleImage.setImageResource(R.drawable.diarypage_scale_image)
                        activity.binding.diarypageTemperatureImage.setImageResource(R.drawable.diarypage_temperature_image)
                        activity.binding.diarypagePetbowlImage.setImageResource(R.drawable.diarypage_petbowl_image)
                        activity.binding.diarypageCannedfoodImage.setImageResource(R.drawable.diarypage_cannedfood_image)
                        activity.binding.diarypageWaterImage.setImageResource(R.drawable.diarypage_water_image)
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


        // 이상증상 데이터를 불러옴 (병렬 처리)
        val sendSymptomSearch = retrofit.apiService.searchSymptom(selectedDate)
        sendSymptomSearch.enqueue(object : Callback<SearchResponseSymptomDTO> {
            override fun onResponse(call: Call<SearchResponseSymptomDTO>, response: Response<SearchResponseSymptomDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    var symptomList = responseDto.symptomList

                    if (symptomList.isNotEmpty()) {
                        for (symptom in symptomList) {

                            Log.d(ContentValues.TAG, "symptom : $symptom")

                            searchPetID = symptom.petID.toString()
                            searchSelectedDay = symptom.selectedDay.toString()

                            Log.d("이상증상 확인", symptom.symptomList.toString())


                            // 만약에 선택한 pet과 DB에서 불러온 pet이 일치하고, 선태한 날짜와 가져온 날짜가 일치하면 text에 출력
                            if (petID == searchPetID && selectedDate == searchSelectedDay) {
                                activity.binding.diarypageSymptomCheckResult.text = symptom.symptomList.toString()


                            } else {
                                activity.binding.diarypageSymptomCheckResult.text = ""
                            }
                        }

                    } else {
                        activity.binding.diarypageSymptomCheckResult.text = ""
                        Log.d(ContentValues.TAG, "No symptom found for the provided day")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<SearchResponseSymptomDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })


        val sendScheduleSearch = retrofit.apiService.searchSchedule(selectedDate)
        sendScheduleSearch.enqueue(object : Callback<SearchResponseScheduleDTO> {
            override fun onResponse(call: Call<SearchResponseScheduleDTO>, response: Response<SearchResponseScheduleDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val scheduleList = responseDto.scheduleList

                    if (scheduleList.isNotEmpty()) {
                        for (schedule in scheduleList) {
                            searchPetID = schedule.petID.toString()
                            searchSelectedDay = schedule.selectedDay.toString()


                            Log.d(ContentValues.TAG, "schedule : $schedule")

                            // 선택한 펫과 선택한 날짜와 맞는 일정만 추가
                            if (petID == searchPetID && selectedDate == searchSelectedDay) {

                                activity.binding.diarypageScheduleResult.text = "일정 제목 : ${schedule.scheduleTitle.toString()} \n일정 시간 : ${schedule.scheduleTime.toString()}"
                            }
                            else {
                                activity.binding.diarypageScheduleResult.text = ""
                            }

                        }


                    } else {
                        activity.binding.diarypageScheduleResult.text = ""
                        Log.d(ContentValues.TAG, "No schedule found for the provided email")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<SearchResponseScheduleDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })

        val sendDiarySearch = retrofit.apiService.searchDiary(selectedDate)
        sendDiarySearch.enqueue(object : Callback<SearchResponseDiaryDTO> {
            override fun onResponse(call: Call<SearchResponseDiaryDTO>, response: Response<SearchResponseDiaryDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val diaryList = responseDto.diaryList

                    if (diaryList.isNotEmpty()) {
                        for (diary in diaryList) {
                            searchPetID = diary.petID.toString()
                            searchSelectedDay = diary.selectedDay.toString()

                            // 선택한 펫과 선택한 날짜와 맞는 일정만 추가
                            if (petID == searchPetID && selectedDate == searchSelectedDay) {

                                activity.binding.diaryResult.text = "일지 제목 : ${diary.diaryTitle.toString()}\n일지 내용 : ${diary.diaryDetail.toString()}"
                            }
                            else {
                                activity.binding.diaryResult.text = ""
                            }

                        }


                    } else {
                        activity.binding.diaryResult.text = ""
                        Log.d(ContentValues.TAG, "No diary found for the provided email")
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