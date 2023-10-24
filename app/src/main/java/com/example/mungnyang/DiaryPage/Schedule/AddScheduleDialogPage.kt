package com.example.mungnyang.DiaryPage.Schedule

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mungnyang.DiaryPage.Numerical.NumericalOBJ
import com.example.mungnyang.Profile.Profile
import com.example.mungnyang.Profile.ProfileManagementOBJ
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityAddSchedulePageBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddScheduleDialogPage (context : Context, scheduleinterface : ScheduleInterface,  private val calendarData: String?,  private val petID: String?)
                            : Dialog(context) , View.OnClickListener {

    private lateinit var binding: ActivityAddSchedulePageBinding

    // 인터페이스 선언
    private var scheduleinterface : ScheduleInterface? = null

    // 인터페이스 연결
    init{
        this.scheduleinterface = scheduleinterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSchedulePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.addbutton.setOnClickListener(this)


    }

    // onclick에 대한 implement
    override fun onClick(view: View?) {

        val retrofit = RetrofitManager.instance

        when(view){
            binding.addbutton -> {
                Log.d("로그", "AddScheduleDialogPage - 등록 버튼 클릭")
                val title = binding.titleText.text.toString()
                val detail = binding.detailText.text.toString()
                val bfTime = binding.bfTime.text.toString()
                val afTime = binding.afTime.text.toString()
                val time = "$bfTime~$afTime"

                if (title.length < 4 || title.length > 20) {
                    Toast.makeText(context, "제목의 길이를 4자이상 20자 이내로 작성해 주세요!", Toast.LENGTH_SHORT)
                        .show()
                } else if (detail.length < 4 || detail.length > 200) {
                    Toast.makeText(context, "내용의 길이를 4자이상 50자 이내로 작성해 주세요!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val addScheduleDTO = ScheduleDTO(
                        calendarData,
                        petID!!.toInt(),
                        title,
                        detail,
                        time
                    )
                    Log.d("API BEFORE 스케쥴", "$addScheduleDTO")

                    val sendAdd = retrofit.apiService.addSchedule(addScheduleDTO);
                    sendAdd.enqueue(object : Callback<ResponseScheduleDTO> {
                        override fun onResponse(
                            call: Call<ResponseScheduleDTO>,
                            response: Response<ResponseScheduleDTO>
                        ) {

                            val responseDto = response.body()
                            Log.d(ContentValues.TAG, "Response received: $responseDto");
                            if (responseDto != null) {
                                Log.d(ContentValues.TAG, "Response received: $responseDto");

                                if (responseDto.response) {

                                    Toast.makeText(context, "저장 완료!", Toast.LENGTH_SHORT).show()
                                    val schedule = Schedule(
                                        "$petID",
                                        "$title",
                                        "$detail",
                                        "$time",
                                        "$calendarData"
                                    )
                                    ScheduleManagementOBJ.addSchedule(schedule)

                                    // 다이얼로그를 종료
                                    dismiss()


                                } else {
                                    Toast.makeText(context, "저장 불가능!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                Toast.makeText(context, "응답이 없습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseScheduleDTO>, t: Throwable) {
                            Toast.makeText(context, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                                .show();
                        }
                    })

                    this.scheduleinterface?.onAddBtnClicked(calendarData.toString(), title, time)
                }
            }
        }
    }

}