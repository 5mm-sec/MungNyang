package com.example.mungnyang.DiaryPage.Numerical.NumericalCheck

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mungnyang.R

import com.example.mungnyang.databinding.ActivityTempCheckPageBinding

class TEMPPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTempCheckPageBinding

    fun concat(s1: String, s2: String): String {
        return StringBuilder(s1).append(s2).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTempCheckPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calendardata = intent.getStringExtra("temp_calendardata")

        binding.tempCheckPageDaytextview.text = calendardata.toString()

        var sData = resources.getStringArray(R.array.temp_page_items)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, sData)
        binding.tempSpinner.adapter = adapter


        binding.tempCheckPageBackImagebuttonEk1.setOnClickListener {
            finish()
        }

        binding.tempCheckPageNoted.setOnClickListener {
            val num = binding.tempEdittext.text.toString()
            if (num.isEmpty()){
                Toast.makeText(this@TEMPPageActivity, "수치를 작성해주세요!", Toast.LENGTH_SHORT).show()
            }
            else{
                val unit = binding.tempSpinner.selectedItem.toString()
                val result = concat(num, unit)
                Log.d("체온", result)

                Toast.makeText(this@TEMPPageActivity, "기록 되었습니다!", Toast.LENGTH_SHORT).show()
                intent.putExtra("temp_result", result)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}