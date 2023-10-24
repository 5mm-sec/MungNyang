package com.example.mungnyang.DiaryPage.Schedule

import android.content.ContentValues
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ScheduleManagementOBJ {
    val List = mutableListOf<Schedule>()

    // 함수 하나 만들어서 init 됐을 때 리스트 내용 비우고 새로고침
    var mainAdapter: ScheduleAdapter? = null

    fun addSchedule(schedule: Schedule) {
        List.add(schedule)
        mainAdapter?.notifyDataSetChanged()
    }

    // 현재 날짜에 해당하는 일정만 필터링하는 함수
    fun filterSchedulesByDate(selectedDay: String) {
        val filteredList = List.filter { it.scheduleDay == selectedDay }
        mainAdapter?.updateData(filteredList)
    }

    fun refreshFrom(petID: String, selectedDay : String) {
        var petId = ""
        var scheduleDay = ""
        var scheduleTitle = ""
        var scheduleDetail = ""
        var scheduleTime = ""

        val retrofit = RetrofitManager.instance;

        // 사용자의 펫 정보를 받아옴.
        val sendSearch = retrofit.apiService.searchSchedule(selectedDay)
        sendSearch.enqueue(object : Callback<SearchResponseScheduleDTO> {
            override fun onResponse(call: Call<SearchResponseScheduleDTO>, response: Response<SearchResponseScheduleDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val scheduleList = responseDto.scheduleList

                    if (scheduleList.isNotEmpty()) {
                        for (schedule in scheduleList) {
                            petId = schedule.petID.toString()
                            scheduleDay = schedule.selectedDay.toString()
                            scheduleTitle = schedule.scheduleTitle.toString()
                            scheduleDetail = schedule.scheduleDetail.toString()
                            scheduleTime = schedule.scheduleTime.toString()

                            Log.d(ContentValues.TAG, "schedule : $schedule")

                            // 선택한 펫과 선택한 날짜와 맞는 일정만 추가
                            if (petID == petId && selectedDay == scheduleDay) {
                                val schedule = Schedule(petId, scheduleTitle, scheduleDetail, scheduleTime, selectedDay)
                                addSchedule(schedule)
                            }

                        }


                    } else {
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
    }
}
