package com.example.mungnyang.Fragment

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.example.mungnyang.DiaryPage.Diary.ResponseDiaryDTO
import com.example.mungnyang.DiaryPage.Diary.initSearchDayOBJ
import com.example.mungnyang.DiaryPage.Numerical.ResponseNumericalDTO
import com.example.mungnyang.DiaryPage.Schedule.ResponseScheduleDTO
import com.example.mungnyang.DiaryPage.Symptom.ResponseSymptomDTO
import com.example.mungnyang.Pet.PetDTO.ResponsePetDeleteDTO
import com.example.mungnyang.Pet.PetDTO.UpdatePetActivity
import com.example.mungnyang.Profile.ProfileManagementOBJ
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DeleteDiaryToPetIDOBJ {

    private val retrofit = RetrofitManager.instance

    // 수치 작성 데이터 삭제
    fun petDelete(
        petIDInt: Int,
        activity: UpdatePetActivity
    ){
        val sendPetDelete = retrofit.apiService.deletePet(petIDInt)
        sendPetDelete.enqueue(object : Callback<ResponsePetDeleteDTO> {

            override fun onResponse(call: Call<ResponsePetDeleteDTO>, response: Response<ResponsePetDeleteDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "펫 삭제 Response received: $responseDto");
                if (responseDto != null) {

                    if (responseDto.response) {

                        ProfileManagementOBJ.deleteProfile(petIDInt.toString())

                        deletePetToNumerical(petIDInt, activity)
                        deletePetToSymptom(petIDInt, activity)
                        deletePetToSchedule(petIDInt, activity)
                        deletePetToDiary(petIDInt, activity)
                        Toast.makeText(activity, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                        activity.finish()

                    } else {
                        Toast.makeText(activity, "펫 삭제 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity, "펫 삭제 응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponsePetDeleteDTO>, t: Throwable) {
                Toast.makeText(activity, "펫 삭제 등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun deletePetToNumerical(
        petIDInt: Int,
        activity: UpdatePetActivity

    ) {
        val sendNumericalDelete = retrofit.apiService.deletePetToNumerical(petIDInt)
        sendNumericalDelete.enqueue(object : Callback<ResponseNumericalDTO> {

            override fun onResponse(call: Call<ResponseNumericalDTO>, response: Response<ResponseNumericalDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "수치 삭제 Response received: $responseDto");
                if (responseDto != null) {
                    if (responseDto.response) {

                        initSearchDayOBJ.searchAllDay(petIDInt) // 변경된 데이터로 다시 dot을 추가

                    } else {
                        Toast.makeText(activity, "수치 삭제 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity, "수치 삭제 응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseNumericalDTO>, t: Throwable) {
                Toast.makeText(activity, "수치 삭제 등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // 이상 증상 작성 데이터 삭제
    fun deletePetToSymptom(
        petIDInt: Int,
        activity: UpdatePetActivity
    ) {
        val sendSymptomDelete = retrofit.apiService.deletePetToSymptom(petIDInt)
        sendSymptomDelete.enqueue(object : Callback<ResponseSymptomDTO> {

            override fun onResponse(call: Call<ResponseSymptomDTO>, response: Response<ResponseSymptomDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "이상 증상 삭제 Response received: $responseDto");
                if (responseDto != null) {
                    if (responseDto.response) {
                        initSearchDayOBJ.searchAllDay(petIDInt) // 변경된 데이터로 다시 dot을 추가
                    } else {
                        Toast.makeText(activity, "이상 증상 삭제 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity, "이상 증상 응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseSymptomDTO>, t: Throwable) {
                Toast.makeText(activity, "이상 증상 삭제 등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // 일정 작성 데이터 삭제
    fun deletePetToSchedule(
        petIDInt: Int,
        activity: UpdatePetActivity
    ) {
        val sendScheduleDelete = retrofit.apiService.deletePetToSchedule(petIDInt)
        sendScheduleDelete.enqueue(object : Callback<ResponseScheduleDTO> {

            override fun onResponse(call: Call<ResponseScheduleDTO>, response: Response<ResponseScheduleDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "일정 삭제 Response received: $responseDto");
                if (responseDto != null) {
                    if (responseDto.response) {
                        initSearchDayOBJ.searchAllDay(petIDInt) // 변경된 데이터로 다시 dot을 추가
                    } else {
                        Toast.makeText(activity, "일정 삭제 삭제 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity, "일정 삭제 응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseScheduleDTO>, t: Throwable) {
                Toast.makeText(activity, "일정 삭제 등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // 일지 작성 데이터 삭제
    fun deletePetToDiary(
        petIDInt: Int,
        activity: UpdatePetActivity
    ) {
        val sendDiaryDelete = retrofit.apiService.deletePetToDiary(petIDInt)
        sendDiaryDelete.enqueue(object : Callback<ResponseDiaryDTO> {

            override fun onResponse(call: Call<ResponseDiaryDTO>, response: Response<ResponseDiaryDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "일지 삭제 Response received: $responseDto");
                if (responseDto != null) {
                    if (responseDto.response) {
                        initSearchDayOBJ.searchAllDay(petIDInt) // 변경된 데이터로 다시 dot을 추가
                    } else {
                        Toast.makeText(activity, "일지 삭제 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(activity, "일지 삭제 응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseDiaryDTO>, t: Throwable) {
                Toast.makeText(activity, "일지 삭제 등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}