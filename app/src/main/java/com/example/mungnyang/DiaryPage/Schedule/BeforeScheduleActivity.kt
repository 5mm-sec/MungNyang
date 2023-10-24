package com.example.mungnyang.DiaryPage.Schedule

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mungnyang.Profile.ProfileAdapter
import com.example.mungnyang.Profile.ProfileManagementOBJ
import com.example.mungnyang.databinding.ActivityBfSchedulePageBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class BeforeScheduleActivity(
) : AppCompatActivity() , ScheduleInterface {

    private lateinit var binding: ActivityBfSchedulePageBinding

    val scheduleTitle : String = ""
    val scheduleDetail : String = ""
    val scheduleTime : String = ""
    var scheduleEventDate : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBfSchedulePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendarData = intent.getStringExtra("notification_calendardata")
        val petID = intent.getStringExtra("petID")

        Log.d("일정 페이지 날짜 : ", calendarData.toString() )

        binding.backImagebutton.setOnClickListener {
            finish()
        }

        binding.addScheduleButton.setOnClickListener {
            val myCustomDialog = AddScheduleDialogPage(this, this , calendarData, petID)

            myCustomDialog.show()
        }

        initializeViews(petID.toString(), calendarData.toString())

        if (ScheduleManagementOBJ.List.isEmpty()) {
            ScheduleManagementOBJ.refreshFrom(petID.toString(), calendarData.toString())
        } else {
            ScheduleManagementOBJ.filterSchedulesByDate(calendarData.toString())
        }

    }

    private fun initializeViews(petID: String, selectedDay: String)  {
        // 아이템들을 어떻게 배열할지 정해줌 ( Linear는 세로로 나열)
        binding.recyclerSchedule.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 필터링된 일정 데이터를 어댑터에 전달
        ScheduleManagementOBJ.filterSchedulesByDate(selectedDay)
        // 실제로 넣어주는 역할
        binding.recyclerSchedule.adapter = this?.let { ScheduleAdapter(it, ScheduleManagementOBJ.List ) }

        // 어댑터 설정
        ScheduleManagementOBJ.mainAdapter = binding.recyclerSchedule.adapter as ScheduleAdapter?;

    }

    // implement 재정의
    // 등록 버튼 클릭시
    override fun onAddBtnClicked(
        selectedDay: String,
        scheduleTitle: String,
        scheduleTime: String
    ) {
        val dateParts = selectedDay!!.split("-")
        val year = dateParts[0].toInt()
        val month = dateParts[1].toInt()
        val day = dateParts[2].toInt()

        val eventDates: List<CalendarDay> = listOf(
            CalendarDay.from(year, month, day), // 예시: 일정이 있는 날짜
        )

        val serializedEventDates = ArrayList(eventDates.map { it.toString() }) // CalendarDay를 문자열로 변환
        intent.putStringArrayListExtra("eventDates", serializedEventDates) // eventDates는 데이터의 예시로, 실제 데이터로 대체해야 합니다.
        intent.putExtra("scheduleTitle", scheduleTitle)
        intent.putExtra("scheduleTime", scheduleTime)
        setResult(RESULT_OK, intent)
        finish()

    }



}