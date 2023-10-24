package com.example.mungnyang.DiaryPage.Numerical.NumericalCheck

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mungnyang.R
import com.example.mungnyang.databinding.ActivityWaterCheckPageBinding


class WaterCheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterCheckPageBinding

    fun concat(s1: String, s2: String): String {
        return StringBuilder(s1).append(s2).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterCheckPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calendardata = intent.getStringExtra("water_calendardata")

        binding.waterCheckPageDaytextview.text = calendardata.toString()

        var sData = resources.getStringArray(R.array.water_page_items)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, sData)
        binding.waterSpinner.adapter = adapter

        binding.waterCheckPageBackImagebuttonEk1.setOnClickListener {
            finish()
        }

        binding.waterCheckPageNotedButton.setOnClickListener {

            val num = binding.waterEdittext.text.toString()

            if (num.isEmpty()){
                Toast.makeText(this@WaterCheckActivity, "수치를 작성해주세요!", Toast.LENGTH_SHORT).show()
            }
            else{
                val unit = binding.waterSpinner.selectedItem.toString()
                val result = concat(num, unit)
                Log.d("음수량", result)

                Toast.makeText(this@WaterCheckActivity, "기록 되었습니다!", Toast.LENGTH_SHORT).show()
                intent.putExtra("water_result", result)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}