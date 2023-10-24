package com.example.mungnyang.DiaryPage.Numerical.NumericalCheck

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mungnyang.R
import com.example.mungnyang.databinding.ActivityKgCheckPageBinding

class KGPageActivity: AppCompatActivity(){
    private lateinit var binding: ActivityKgCheckPageBinding

    fun concat(s1: String, s2: String): String {
        return StringBuilder(s1).append(s2).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKgCheckPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calendardata = intent.getStringExtra("kg_calendardata")
        var petID = intent.getStringExtra("petID")

        Log.d("KGPAGE에서의 PETID", petID.toString())

        binding.kgCheckPageDaytextview.text = calendardata.toString()

        var sData = resources.getStringArray(R.array.kg_page_items)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, sData)
        binding.spinner.adapter = adapter

        binding.kgCheckPageBackImagebuttonEk1.setOnClickListener {
            finish()
        }

        binding.kgCheckPageNoted.setOnClickListener {

            val num = binding.kgEdittext.text.toString()

            if (num.isEmpty()){
                Toast.makeText(this@KGPageActivity, "수치를 작성해주세요!", Toast.LENGTH_SHORT).show()
            }
            else{
                val unit = binding.spinner.selectedItem.toString()
                val result = concat(num, unit)

                Log.d("몸무게", result)

                Toast.makeText(this@KGPageActivity, "기록 되었습니다!", Toast.LENGTH_SHORT).show()

                intent.putExtra("kg_result", result)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}