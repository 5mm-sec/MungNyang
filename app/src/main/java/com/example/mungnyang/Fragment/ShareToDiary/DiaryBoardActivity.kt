package com.example.mungnyang.Fragment.ShareToDiary

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.DiaryPage.Diary.ResponseDiaryDTO
import com.example.mungnyang.Fragment.DeleteDiaryToSelectedDayOBJ
import com.example.mungnyang.Fragment.ShareToDiary.Answer.DiaryAnswer
import com.example.mungnyang.Fragment.ShareToDiary.Answer.DiaryAnswerAdapter
import com.example.mungnyang.Fragment.ShareToDiary.Answer.DiaryAnswerFriendAdapter
import com.example.mungnyang.Fragment.ShareToDiary.Answer.DiaryBoardAnswerDTO
import com.example.mungnyang.Fragment.ShareToDiary.Answer.ResponseDiaryAnswerDTO
import com.example.mungnyang.Fragment.ShareToDiary.Answer.SearchResponseDiaryAnswerDTO
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityDiaryBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DiaryBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryBoardBinding
    private var sendText : String = ""
    private var imageURL : String = ""
    private var accountNickName : String = ""
    private var userEmail : String = ""
    private var boardEmail : String = ""
    private var diaryTitle : String = ""

    private var findUserEmail : String = ""
    private var findUserURL : String = ""
    private var findUserNickName : String = ""
    private var findBoardTitle : String = ""
    private var findAnswerText : String = ""
    private var findTime : String = ""
    private var findBoardEmail : String = ""
    private var findAnswerID : Int = 0

    private var petID : String = ""
    private var selectedDay : String = ""


    private var diaryAnswerList : MutableList<DiaryAnswer> = mutableListOf()

    private val retrofit = RetrofitManager.instance
    val TAG = "DiaryBoardActivity"
    val TAG2 = "DiaryBoardActivity22222"

    private lateinit var diaryAnswerAdapter: DiaryAnswerAdapter
    private lateinit var diaryAnswerFriendAdapter: DiaryAnswerFriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val userNickName = intent.getStringExtra("userNickName")
        val diaryContent = intent.getStringExtra("diaryContent")
        val userImageURL = intent.getStringExtra("userImageURL")

        diaryTitle = intent.getStringExtra("diaryTitle").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        boardEmail = intent.getStringExtra("boardEmail").toString()

        petID = intent.getStringExtra("petID").toString()
        selectedDay = intent.getStringExtra("selectedDay").toString()

        //Log.d("테테테ㅔ테테ㅔ테텥3444444", userEmail)
        //Log.d("테테테ㅔ테테ㅔ테텥122222222", boardEmail)

        accountNickName = intent.getStringExtra("accountNickName").toString()
        imageURL = intent.getStringExtra("imageURL").toString()

        Log.d("게시판 작성자 이메일", boardEmail)
        Log.d("댓글 작성자 이메일", userEmail)


        //searchByUserEmail(userEmail)
        searchByBoardEmail(boardEmail)
        val defaultImage = R.drawable.defaultuserimage;

        Glide.with(this@DiaryBoardActivity)
            .load(userImageURL)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(defaultImage)
            .error(defaultImage)
            .fallback(defaultImage)
            .into(binding.userImage)

        binding.userNickname.text = userNickName
        binding.diaryTitle.text = diaryTitle
        binding.diaryContent.text = diaryContent

        Glide.with(this@DiaryBoardActivity)
            .load(imageURL)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(defaultImage)
            .error(defaultImage)
            .fallback(defaultImage)
            .into(binding.sendUserImage)

        binding.backImagebutton.setOnClickListener {
            finish()
        }


        if(userEmail == boardEmail) {
            binding.boardDelete.setOnClickListener {

                val builder = AlertDialog.Builder(this@DiaryBoardActivity)
                builder.setTitle("삭제")
                builder.setMessage("게시물을 삭제 하시겠습니까?")

                // 예 버튼 처리
                builder.setPositiveButton("예") { dialog, which ->
                    // 예 버튼을 클릭하면 로그아웃 처리를 수행
                    val sendDiaryDelete =
                        DeleteDiaryToSelectedDayOBJ.retrofit.apiService.deleteDiary(selectedDay, petID.toInt())
                    sendDiaryDelete.enqueue(object : Callback<ResponseDiaryDTO> {

                        override fun onResponse(
                            call: Call<ResponseDiaryDTO>,
                            response: Response<ResponseDiaryDTO>
                        ) {

                            val responseDto = response.body()
                            Log.d(ContentValues.TAG, "Response received: $responseDto");
                            if (responseDto != null) {
                                Log.d(ContentValues.TAG, "Response received: $responseDto");

                                if (responseDto.response) {

                                    Toast.makeText(this@DiaryBoardActivity, "삭제 완료!", Toast.LENGTH_SHORT).show()

                                    deleteAllAnswer(boardEmail, findBoardTitle)
                                } else {
                                    Toast.makeText(this@DiaryBoardActivity, "삭제 불가능!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@DiaryBoardActivity, "응답이 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseDiaryDTO>, t: Throwable) {
                            Toast.makeText(
                                this@DiaryBoardActivity,
                                "등록 불가능 네트워크 에러!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
                }

                // 아니오 버튼 처리
                builder.setNegativeButton("아니오") { dialog, which ->
                    // 아니오 버튼을 클릭하면 다이얼로그를 닫음 (아무 동작 없음)
                    dialog.dismiss()
                }

                // 다이얼로그를 표시
                val dialog = builder.create()
                dialog.show()

            }
        }
        else{
            binding.boardDelete.visibility = View.GONE
        }


        binding.send.setOnClickListener {

            sendText = binding.commentEditText.text.toString()

            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val formattedTime = currentTime.format(formatter)

            val addAnswerDTO = DiaryBoardAnswerDTO(userEmail,
                boardEmail,
                accountNickName,
                diaryTitle,
                sendText,
                formattedTime,
                imageURL )

            Log.d("API BEFORE","hi $addAnswerDTO")

            val sendAdd = retrofit.apiService.addDiaryAnswer(addAnswerDTO);
            sendAdd.enqueue(object : Callback<ResponseDiaryAnswerDTO> {

                override fun onResponse(call: Call<ResponseDiaryAnswerDTO>, response: Response<ResponseDiaryAnswerDTO>) {

                    val responseDto = response.body()
                    Log.d(ContentValues.TAG, "Response received: $responseDto");
                    if (responseDto != null) {

                        Log.d(ContentValues.TAG, "Response received: $responseDto");

                        if (responseDto.response) {
                            Toast.makeText(this@DiaryBoardActivity, "작성 완료!", Toast.LENGTH_SHORT).show()

                            diaryAnswerList.clear() // 기존 목록을 지우고 새로 불러오기

                            binding.commentEditText.text = null

                            if( boardEmail == userEmail){
                                searchByBoardEmail(boardEmail)
                                diaryAnswerAdapter.notifyDataSetChanged()
                            }
                            else{
                                searchByBoardEmail(boardEmail)
                                diaryAnswerFriendAdapter.notifyDataSetChanged()
                            }
                            // 스크롤을 맨 아래로 이동
                            //binding.recyclerAnswerList.scrollToPosition(diaryAnswerList.size - 1)

                            //diaryAnswerAdapter.notifyDataSetChanged()
                            //diaryAnswerFriendAdapter.notifyDataSetChanged()

                        } else {
                            Toast.makeText(this@DiaryBoardActivity, "작성 불가능!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this@DiaryBoardActivity, "응답이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseDiaryAnswerDTO>, t: Throwable) {
                    Toast.makeText(this@DiaryBoardActivity, "작성 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                        .show();
                }
            })
        }

        initializeViews()
    }

    private fun deleteAllAnswer(boardEmail: String, boardTitle: String){
        val sendAnswerDelete = retrofit.apiService.deleteAllAnswer(boardEmail,boardTitle)
        sendAnswerDelete.enqueue(object : Callback<ResponseDiaryAnswerDTO> {

            override fun onResponse(call: Call<ResponseDiaryAnswerDTO>, response: Response<ResponseDiaryAnswerDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "댓글 삭제 Response received: $responseDto");
                if (responseDto != null) {

                    if (responseDto.response) {
                        finish()
                    } else {
                        Toast.makeText(this@DiaryBoardActivity, "댓글 삭제 불가능!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DiaryBoardActivity, "댓글 삭제 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDiaryAnswerDTO>, t: Throwable) {
                Toast.makeText(this@DiaryBoardActivity, "댓글 삭제 등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌 ( Linear는 세로로 나열)
        binding.recyclerAnswerList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        if(userEmail == boardEmail){
            diaryAnswerAdapter = DiaryAnswerAdapter(this, diaryAnswerList, boardEmail)
            binding.recyclerAnswerList.adapter = diaryAnswerAdapter
        }
        else{
            diaryAnswerFriendAdapter = DiaryAnswerFriendAdapter(this, diaryAnswerList, userEmail)
            binding.recyclerAnswerList.adapter = diaryAnswerFriendAdapter
        }
        //communityDiaryAdapter.notifyDataSetChanged()

    }


    private fun searchByBoardEmail(boardEmail1: String){

        val sendDiaryAnswerSearch = retrofit.apiService.searchByBoardEmail(boardEmail1)
        sendDiaryAnswerSearch.enqueue(object : Callback<SearchResponseDiaryAnswerDTO> {
            override fun onResponse(
                call: Call<SearchResponseDiaryAnswerDTO>,
                response: Response<SearchResponseDiaryAnswerDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {

                    val answerList = responseDto.answerList

                    if (answerList.isNotEmpty()) {

                        for (answerList in answerList) {

                            findUserEmail = answerList.userEmail.toString()
                            findUserNickName = answerList.userNickName.toString()
                            findAnswerText = answerList.answerText.toString()
                            findBoardTitle = answerList.boardTitle.toString()
                            findTime = answerList.time.toString()
                            findUserURL = answerList.userImageURL.toString()
                            findBoardEmail = answerList.boardEmail.toString()
                            findAnswerID = answerList.diaryAnswerID!!.toInt()

                            if (findBoardTitle == diaryTitle && findBoardEmail == boardEmail1){

                                val diaryAnswer = DiaryAnswer(findAnswerID, findUserEmail, findUserNickName, findBoardTitle, findAnswerText, findTime, findUserURL)

                                diaryAnswerList.add(diaryAnswer)
                                if(userEmail == boardEmail){
                                    diaryAnswerAdapter.notifyDataSetChanged()
                                }
                                else{
                                    diaryAnswerFriendAdapter.notifyDataSetChanged()
                                }
                                //diaryAnswerAdapter.notifyDataSetChanged()
                                //diaryAnswerFriendAdapter.notifyDataSetChanged()
                            }

                        }
                    } else {
                        Log.d(TAG2, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG2, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<SearchResponseDiaryAnswerDTO>, t: Throwable) {
                Log.e(TAG2, "Search Request Failed: ${t.message}", t)
            }
        })

    }





}