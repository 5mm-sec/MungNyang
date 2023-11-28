package com.example.mungnyang.DiaryPage.Symptom

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mungnyang.Fragment.EventDecorator
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivitySymptomCheckPageBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SymptomCheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySymptomCheckPageBinding

    private val symptomCheckmarks: List<ImageView> by lazy {
        listOf (
        binding.symptomCheckmark1, binding.symptomCheckmark2, binding.symptomCheckmark3,
        binding.symptomCheckmark4, binding.symptomCheckmark5, binding.symptomCheckmark6,
        binding.symptomCheckmark7, binding.symptomCheckmark8, binding.symptomCheckmark9,
        binding.symptomCheckmark10, binding.symptomCheckmark11, binding.symptomCheckmark12,
        binding.symptomCheckmark13
        )
    }

    private var num : Int = 0

    private val selectedImageNumbers = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySymptomCheckPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = RetrofitManager.instance

        binding.symptomCheckBackImagebutton.setOnClickListener {
            finish()
        }

        symptomCheckmarks.forEachIndexed  { index, symptomCheckmark ->
            symptomCheckmark.setOnClickListener {
                val clicked = symptomCheckmark.tag as? Boolean ?: false
                if (!clicked) {
                    Log.d("클릭됨", "true")

                    symptomCheckmark.setImageResource(R.drawable.checkmark2)
                    selectedImageNumbers.add(index + 1) // 선택한 이미지 번호 저장
                    num++

                    Log.d("선택한 이미지 번호", (index + 1).toString())

                } else {
                    Log.d("해제됨", "false")

                    symptomCheckmark.setImageResource(R.drawable.__symptom_checkmark1)
                    selectedImageNumbers.remove(index + 1) // 이미지 해제 시 번호 삭제
                    num--

                }
                symptomCheckmark.tag = !clicked
            }
        }

        binding.symptomCheckAddtextview.setOnClickListener {

            val symptomList = mutableListOf<String>()

            if (binding.detailEdittext.text.length < 4 || binding.detailEdittext.text.length > 50) {
                Toast.makeText(
                    this,
                    "기타 증상의 길이를 4자이상 20자 이내로 작성해 주세요!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if(binding.detailEdittext.text.isEmpty() && selectedImageNumbers.isEmpty()) {
                Toast.makeText(
                    this,
                    "기타 증상을 작성하거나 증상을 선택해야 합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                for (number in selectedImageNumbers) {

                    val textId =
                        resources.getIdentifier("symptom_check_text$number", "id", packageName)
                    val textView = findViewById<TextView>(textId)

                    if (textView != null) {
                        val text = textView.text.toString()
                        symptomList.add(text) // 텍스트를 symptomList에 추가합니다.
                        Log.d("선택한 이미지의 텍스트", text)
                    }
                }

                val detailEditText = binding.detailEdittext.text.toString()

                if (detailEditText.isNotEmpty()) {
                    symptomList.add(detailEditText)
                    Log.d("추가한 detail_edittext", detailEditText)
                }

                Log.d("선택한 개수", selectedImageNumbers.size.toString())
                val selectedDate = intent.getStringExtra("symptom_calendardata")
                val petID = intent.getStringExtra("petID")

                val addSymptomDTO = SymptomDTO(
                    selectedDate,
                    petID?.toInt(),
                    symptomList
                )

                Log.d("API BEFORE", "hi $addSymptomDTO")

                val sendAdd = retrofit.apiService.addSymptom(addSymptomDTO);
                sendAdd.enqueue(object : Callback<ResponseSymptomDTO> {

                    override fun onResponse(
                        call: Call<ResponseSymptomDTO>,
                        response: Response<ResponseSymptomDTO>
                    ) {

                        val responseDto = response.body()
                        Log.d(ContentValues.TAG, "Response received: $responseDto");
                        if (responseDto != null) {
                            Log.d(ContentValues.TAG, "Response received: $responseDto");

                            if (responseDto.response) {

                                Toast.makeText(
                                    this@SymptomCheckActivity,
                                    "저장 완료!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // 서버로 데이터가 전송이 됐으면, dot 찍기
                                val dateParts = selectedDate!!.split("-")
                                val year = dateParts[0].toInt()
                                val month = dateParts[1].toInt()
                                val day = dateParts[2].toInt()

                                val eventDates: List<CalendarDay> = listOf(
                                    CalendarDay.from(year, month, day), // 예시: 일정이 있는 날짜
                                )
                                Log.d("메인 이상 증상 eventDates", eventDates.toString())

                                val serializedEventDates =
                                    ArrayList(eventDates.map { it.toString() }) // CalendarDay를 문자열로 변환
                                Log.d(
                                    "메인 이상 증상 serializedEventDates",
                                    serializedEventDates.toString()
                                )

                                intent.putStringArrayListExtra(
                                    "eventDates",
                                    serializedEventDates
                                ) // eventDates는 데이터의 예시로, 실제 데이터로 대체해야 합니다.
                                intent.putStringArrayListExtra(
                                    "symptomList",
                                    ArrayList(symptomList)
                                )
                                setResult(RESULT_OK, intent)
                                finish()

                            } else {
                                Toast.makeText(
                                    this@SymptomCheckActivity,
                                    "저장 불가능!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                this@SymptomCheckActivity,
                                "응답이 없습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseSymptomDTO>, t: Throwable) {
                        Toast.makeText(
                            this@SymptomCheckActivity,
                            "등록 불가능 네트워크 에러!",
                            Toast.LENGTH_SHORT
                        )
                            .show();
                    }
                })


            }
        }
    }
}