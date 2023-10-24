package com.example.mungnyang.DiaryPage.Numerical.NumericalCheck

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mungnyang.R
import com.example.mungnyang.databinding.ActivityPetbowlCheckPageBinding


class PetbowlCheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPetbowlCheckPageBinding

    fun concat(s1: String, s2: String): String {
        return StringBuilder(s1).append(s2).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetbowlCheckPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calendardata = intent.getStringExtra("petbowl_calendardata")

        binding.petbowlCheckPageDaytextview.text = calendardata.toString()

        var sData = resources.getStringArray(R.array.petbowl_page_items)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, sData)
        binding.petbowlSpinner.adapter = adapter

        binding.petbowlCheckPageBackImagebuttonEk1.setOnClickListener {
            finish()
        }

        binding.petbowlCheckPageNotedButton.setOnClickListener {

            val num = binding.petbowlEdittext.text.toString()

            if (num.isEmpty()){
                Toast.makeText(this@PetbowlCheckActivity, "수치를 작성해주세요!", Toast.LENGTH_SHORT).show()
            }
            else{
                val unit = binding.petbowlSpinner.selectedItem.toString()
                val result = concat(num, unit)
                Log.d("식사량", result)

                Toast.makeText(this@PetbowlCheckActivity, "기록 되었습니다!", Toast.LENGTH_SHORT).show()
                intent.putExtra("petbowl_result", result)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}