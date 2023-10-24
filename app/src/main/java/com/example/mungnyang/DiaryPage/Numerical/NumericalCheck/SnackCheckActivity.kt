package com.example.mungnyang.DiaryPage.Numerical.NumericalCheck

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mungnyang.R
import com.example.mungnyang.databinding.ActivitySnackCheckPageBinding


class SnackCheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnackCheckPageBinding

    fun concat(s1: String, s2: String): String {
        return StringBuilder(s1).append(s2).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnackCheckPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calendardata = intent.getStringExtra("snack_calendardata")

        binding.snackCheckPageDaytextview.text = calendardata.toString()


        var sData = resources.getStringArray(R.array.snack_page_items)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, sData)
        binding.snackSpinner.adapter = adapter

        binding.snackCheckPageBackImagebuttonEk1.setOnClickListener {
            finish()
        }




        binding.snackCheckPageNotedButton.setOnClickListener {

            val num = binding.snackEdittext.text.toString()

            if (num.isEmpty()){
                Toast.makeText(this@SnackCheckActivity, "수치를 작성해주세요!", Toast.LENGTH_SHORT).show()
            }
            else{
                val unit = binding.snackSpinner.selectedItem.toString()
                val result = concat(num, unit)
                Log.d("간식량", result)

                Toast.makeText(this@SnackCheckActivity, "기록 되었습니다!", Toast.LENGTH_SHORT).show()
                intent.putExtra("snack_result", result)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}