package com.example.mungnyang.DiaryPage.Diary

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityDiaryPageBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryPageActivity   : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryPageBinding

    private var diaryTitle: String = ""
    private var detailEditText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendarData = intent.getStringExtra("diary_calendardata")
        val petID = intent.getStringExtra("petID")
        val userEmail = intent.getStringExtra("userEmail")

        binding.dayTextview.text = "선택한 날짜 :  " + calendarData.toString()

        binding.backImagebutton.setOnClickListener {
            finish()
        }

        binding.addbutton.setOnClickListener {
            val bfTime = binding.bfTime.text.toString()
            val afTime = binding.afTime.text.toString()

            var timeResult = ""

            if (bfTime.isNotEmpty() && afTime.isNotEmpty()) {
                timeResult = "$bfTime~$afTime"

                diaryTitle = binding.titleText.text.toString()
                detailEditText = binding.detailEdittext.text.toString()

                if (diaryTitle.length < 4 || diaryTitle.length > 20) {
                    Toast.makeText(
                        this,
                        "제목의 길이를 4자이상 20자 이내로 작성해 주세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (detailEditText.length < 4 || detailEditText.length > 200) {
                    Toast.makeText(
                        this,
                        "내용의 길이를 4자이상 200자 이내로 작성해 주세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val retrofit = RetrofitManager.instance

                    val addDiaryDTO = DiaryDTO(
                        calendarData,
                        petID!!.toInt(),
                        timeResult,
                        detailEditText,
                        diaryTitle,
                        userEmail.toString()
                    )

                    Log.d("API BEFORE", "hi $addDiaryDTO")

                    val sendAdd = retrofit.apiService.addDiary(addDiaryDTO)
                    sendAdd.enqueue(object : Callback<ResponseDiaryDTO> {

                        override fun onResponse(
                            call: Call<ResponseDiaryDTO>,
                            response: Response<ResponseDiaryDTO>
                        ) {

                            val responseDto = response.body()
                            Log.d(ContentValues.TAG, "Response received: $responseDto");
                            if (responseDto != null) {
                                Log.d(ContentValues.TAG, "Response received: $responseDto");

                                if (responseDto.response) {

                                    if (binding.timeTextview.text == "" || binding.bfTime.text.toString() == "" || binding.afTime.text.toString() == "" ||
                                        binding.detailEdittext.text.toString() == ""
                                    ) {
                                        Toast.makeText(
                                            this@DiaryPageActivity,
                                            "항목을 모두 작성해주세요.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        Toast.makeText(
                                            this@DiaryPageActivity,
                                            "저장 완료!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // 서버로 데이터가 전송이 됐으면, dot 찍기
                                        val dateParts = calendarData!!.split("-")
                                        val year = dateParts[0].toInt()
                                        val month = dateParts[1].toInt()
                                        val day = dateParts[2].toInt()

                                        val eventDates: List<CalendarDay> = listOf(
                                            CalendarDay.from(year, month, day) // 예시: 일정이 있는 날짜
                                        )

                                        val serializedEventDates =
                                            ArrayList(eventDates.map { it.toString() }) // CalendarDay를 문자열로 변환

                                        intent.putStringArrayListExtra(
                                            "eventDates",
                                            serializedEventDates
                                        ) // eventDates는 데이터의 예시로, 실제 데이터로 대체해야 합니다.
                                        intent.putExtra("diaryTitle", diaryTitle)
                                        intent.putExtra("diaryDetail", detailEditText)
                                        setResult(RESULT_OK, intent)
                                        finish()
                                    }

                                } else {
                                    Toast.makeText(
                                        this@DiaryPageActivity,
                                        "저장 불가능!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } else {
                                Toast.makeText(this@DiaryPageActivity, "응답이 없습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseDiaryDTO>, t: Throwable) {
                            Toast.makeText(
                                this@DiaryPageActivity,
                                "등록 불가능 네트워크 에러!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
                }
            }
            else{
                Toast.makeText(this, "시간을 작성해주세요", Toast.LENGTH_SHORT).show()
            }



        }
    }
}