package com.example.mungnyang.Fragment.ShareToWalking

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
import com.example.mungnyang.Fragment.ShareToDiary.Answer.ResponseDiaryAnswerDTO
import com.example.mungnyang.Fragment.Walking.SearchResponseWalkingDTO
import com.example.mungnyang.Fragment.Walking.WalkingAnswer.SearchResponseWalkingAnswerDTO
import com.example.mungnyang.Fragment.Walking.WalkingAnswer.WalkingAnswer
import com.example.mungnyang.Fragment.Walking.WalkingAnswer.WalkingAnswerAdapter
import com.example.mungnyang.Fragment.Walking.WalkingAnswer.WalkingAnswerFriendAdapter
import com.example.mungnyang.Fragment.Walking.WalkingAnswer.WalkingBoardAnswerDTO
import com.example.mungnyang.Pet.PetDTO.ResponsePetDTO
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityWalkingBoardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WalkingBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalkingBoardBinding
    private var sendText : String = ""
    private var imageURL : String = ""
    private var accountNickName : String = ""
    private var userEmail : String = ""
    private var boardEmail : String = ""
    private var startTime : String = ""
    private var endTime : String = ""
    private var elapsedTime : String = ""
    private var distance : String = ""
    private var petKcalAmount : String = ""
    private var myKcalAmount : String = ""
    private var mapImageURL : String = ""

    private var findUserEmail : String = ""
    private var findUserURL : String = ""
    private var findUserNickName : String = ""
    private var findPetName : String = ""
    private var findSelectedDay : String = ""
    private var findAnswerText : String = ""
    private var findTime : String = ""
    private var findBoardEmail : String = ""
    private var findAnswerID : Int = 0
    private var findWalkingBoardID : Int = 0
    private var findWalkingID : Int = 0
    private var finMapURL : String = ""

    private var petID : String = ""
    private var petName : String = ""
    private var selectedDay : String = ""

    private var walkingAnswerList : MutableList<WalkingAnswer> = mutableListOf()

    private val retrofit = RetrofitManager.instance
    val TAG = "DiaryBoardActivity"
    val TAG2 = "DiaryBoardActivity22222"

    private lateinit var walkingAnswerAdapter: WalkingAnswerAdapter
    private lateinit var walkingAnswerFriendAdapter: WalkingAnswerFriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkingBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userNickName = intent.getStringExtra("userNickName")
        val userImageURL = intent.getStringExtra("userImageURL")

        startTime = intent.getStringExtra("startTime").toString()
        endTime = intent.getStringExtra("endTime").toString()
        distance = intent.getStringExtra("distance").toString()
        petKcalAmount = intent.getStringExtra("petKcalAmount").toString()
        myKcalAmount = intent.getStringExtra("myKcalAmount").toString()
        mapImageURL = intent.getStringExtra("mapImageURL").toString()
        elapsedTime = intent.getStringExtra("elapsedTime").toString()

        userEmail = intent.getStringExtra("userEmail").toString()
        boardEmail = intent.getStringExtra("boardEmail").toString()

        petID = intent.getStringExtra("petID").toString()
        selectedDay = intent.getStringExtra("selectedDay").toString()

        accountNickName = intent.getStringExtra("accountNickName").toString()
        imageURL = intent.getStringExtra("imageURL").toString()

        Log.d("게시판 작성자 이메일", boardEmail)
        Log.d("댓글 작성자 이메일", userEmail)

        Log.d("펫 아이디", petID)


        val sendWalkingSearch = retrofit.apiService.shareWalkingDiary(boardEmail)
        sendWalkingSearch.enqueue(object : Callback<SearchResponseWalkingDTO> {
            override fun onResponse(call: Call<SearchResponseWalkingDTO>, response: Response<SearchResponseWalkingDTO>) {
                val responseDto = response.body()
                Log.d("테스트", responseDto.toString())

                if (responseDto != null) {
                    val walkingList = responseDto.walkingDiaryList

                    if (walkingList.isNotEmpty()) {
                        for (walkingDiary in walkingList) {
                            val dbPetID = walkingDiary.petID.toString()
                            val dbSelectedDay = walkingDiary.selectedDay.toString()
                            val dbMapImageURL = walkingDiary.mapImageURL.toString()
                            if(dbMapImageURL == mapImageURL){
                                findWalkingBoardID = walkingDiary.walkingID!!.toInt()
                            }
                        }
                        searchByBoardEmail(boardEmail, findWalkingBoardID)
                    } else {
                        Log.d(ContentValues.TAG, "No diary found for the provided email")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<SearchResponseWalkingDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })


        // PetID를 이용해 해당하는 펫의 정보를 불러옴
        val sendSearch = retrofit.apiService.searchPetID(petID.toInt())
        sendSearch.enqueue(object : Callback<ResponsePetDTO> {
            override fun onResponse(call: Call<ResponsePetDTO>, response: Response<ResponsePetDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val petList = responseDto.petList

                    if (petList.isNotEmpty()) {
                        for (pet in petList) {
                            val dbPetId = pet.petID.toString()

                            if(petID == dbPetId){
                                petName = pet.petName.toString()
                                binding.walkingDiaryInfoText.text = "${petName}의 산책 기록"
                            }
                        }
                    } else {
                        Log.d(ContentValues.TAG, "No pets found for the provided email")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponsePetDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })

        val defaultImage = R.drawable.defaultuserimage;

        Glide.with(this@WalkingBoardActivity)
            .load(userImageURL)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(defaultImage)
            .error(defaultImage)
            .fallback(defaultImage)
            .into(binding.userImage)

        binding.userNickname.text = userNickName
        binding.walkingDiaryStartTime.text = startTime
        binding.walkingDiaryEndTime.text = endTime
        binding.walkingDiaryElapsed.text = elapsedTime
        binding.walkingDiaryDistance.text = distance
        binding.walkingDiaryMyKcal.text = myKcalAmount
        binding.walkingDiaryPetKcal.text = petKcalAmount
        binding.walkingDiarySelectedDay.text = selectedDay

        Glide.with(this@WalkingBoardActivity)
            .load(mapImageURL)
            .placeholder(defaultImage)
            .error(defaultImage)
            .fallback(defaultImage)
            .into(binding.mapImage)


        //binding.walkingDiaryDistance.text =

        Glide.with(this@WalkingBoardActivity)
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
                val builder = AlertDialog.Builder(this@WalkingBoardActivity)
                builder.setTitle("삭제")
                builder.setMessage("게시물을 삭제 하시겠습니까?")

                // 예 버튼 처리
                builder.setPositiveButton("예") { dialog, which ->
                    // 예 버튼을 클릭하면 로그아웃 처리를 수행
                    val sendDiaryDelete = retrofit.apiService.deleteWalking(selectedDay, findWalkingBoardID)
                    sendDiaryDelete.enqueue(object : Callback<ResponseDiaryAnswerDTO> {

                        override fun onResponse(
                            call: Call<ResponseDiaryAnswerDTO>,
                            response: Response<ResponseDiaryAnswerDTO>
                        ) {

                            val responseDto = response.body()
                            Log.d(ContentValues.TAG, "Response received: $responseDto");
                            if (responseDto != null) {
                                Log.d(ContentValues.TAG, "Response received: $responseDto");

                                if (responseDto.response) {

                                    Toast.makeText(this@WalkingBoardActivity, "삭제 완료!", Toast.LENGTH_SHORT).show()

                                    deleteAllAnswer(boardEmail, findWalkingBoardID)
                                } else {
                                    Toast.makeText(this@WalkingBoardActivity, "삭제 불가능!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@WalkingBoardActivity, "응답이 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseDiaryAnswerDTO>, t: Throwable) {
                            Toast.makeText(
                                this@WalkingBoardActivity,
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

            val addAnswerDTO = WalkingBoardAnswerDTO(
                findWalkingBoardID,
                userEmail,
                boardEmail,
                accountNickName,
                petName,
                selectedDay,
                sendText,
                formattedTime,
                imageURL )

            Log.d("API BEFORE","hi $addAnswerDTO")

            val sendAdd = retrofit.apiService.addWalkingAnswer(addAnswerDTO);
            sendAdd.enqueue(object : Callback<ResponseAddWalkingDTO> {

                override fun onResponse(call: Call<ResponseAddWalkingDTO>, response: Response<ResponseAddWalkingDTO>) {

                    val responseDto = response.body()
                    Log.d(ContentValues.TAG, "Response received: $responseDto");
                    if (responseDto != null) {

                        Log.d(ContentValues.TAG, "Response received: $responseDto");

                        if (responseDto.response) {
                            Toast.makeText(this@WalkingBoardActivity, "작성 완료!", Toast.LENGTH_SHORT).show()

                            walkingAnswerList.clear() // 기존 목록을 지우고 새로 불러오기

                            binding.commentEditText.text = null

                            if( boardEmail == userEmail){
                                searchByBoardEmail(boardEmail, findWalkingBoardID)
                                walkingAnswerAdapter.notifyDataSetChanged()
                            }
                            else{
                                searchByBoardEmail(boardEmail, findWalkingBoardID)
                                walkingAnswerFriendAdapter.notifyDataSetChanged()
                            }

                        } else {
                            Toast.makeText(this@WalkingBoardActivity, "작성 불가능!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this@WalkingBoardActivity, "응답이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseAddWalkingDTO>, t: Throwable) {
                    Toast.makeText(this@WalkingBoardActivity, "작성 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                        .show();
                }
            })
        }

        initializeViews()
    }

    private fun deleteAllAnswer(boardEmail: String, walkingID: Int){
        val sendAnswerDelete = retrofit.apiService.deleteAllWalkingAnswer(boardEmail, walkingID)
        sendAnswerDelete.enqueue(object : Callback<ResponseDiaryAnswerDTO> {

            override fun onResponse(call: Call<ResponseDiaryAnswerDTO>, response: Response<ResponseDiaryAnswerDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "댓글 삭제 Response received: $responseDto");
                if (responseDto != null) {

                    if (responseDto.response) {
                        finish()
                    } else {
                        Toast.makeText(this@WalkingBoardActivity, "댓글 삭제 불가능!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@WalkingBoardActivity, "댓글 삭제 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDiaryAnswerDTO>, t: Throwable) {
                Toast.makeText(this@WalkingBoardActivity, "댓글 삭제 등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌 ( Linear는 세로로 나열)
        binding.recyclerWalkingAnswerList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        if(userEmail == boardEmail){
            walkingAnswerAdapter = WalkingAnswerAdapter(this, walkingAnswerList, boardEmail)
            binding.recyclerWalkingAnswerList.adapter = walkingAnswerAdapter
        }
        else{
            walkingAnswerFriendAdapter = WalkingAnswerFriendAdapter(this, walkingAnswerList, userEmail)
            binding.recyclerWalkingAnswerList.adapter = walkingAnswerFriendAdapter
        }
        //communityDiaryAdapter.notifyDataSetChanged()

    }

    private fun searchByBoardEmail(boardEmail1: String, walkingBoardID: Int){

        val sendWalkingAnswerSearch = retrofit.apiService.searchByWalkingBoardEmail(boardEmail1)
        sendWalkingAnswerSearch.enqueue(object : Callback<SearchResponseWalkingAnswerDTO> {
            override fun onResponse(
                call: Call<SearchResponseWalkingAnswerDTO>,
                response: Response<SearchResponseWalkingAnswerDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {

                    val answerList = responseDto.answerList

                    if (answerList.isNotEmpty()) {

                        for (answerList in answerList) {

                            findWalkingID = answerList.walkingID!!.toInt()
                            findUserEmail = answerList.userEmail.toString()
                            findUserNickName = answerList.userNickName.toString()
                            findAnswerText = answerList.answerText.toString()
                            findPetName = answerList.petName.toString()
                            findTime = answerList.time.toString()
                            findSelectedDay = answerList.selectedDay.toString()
                            findUserURL = answerList.userImageURL.toString()
                            findBoardEmail = answerList.boardEmail.toString()
                            findAnswerID = answerList.walkingAnswerID!!.toInt()

                            Log.d("워킹 리스트", answerList.toString())

                            if (walkingBoardID == findWalkingID){

                                val walkingAnswer = WalkingAnswer(findAnswerID, walkingBoardID, findUserEmail, findBoardEmail, findUserNickName, findPetName, findSelectedDay, findAnswerText, findTime, findUserURL)

                                Log.d("워킹 댓글", walkingAnswer.toString())

                                walkingAnswerList.add(walkingAnswer)
                                if(userEmail == boardEmail){
                                    walkingAnswerAdapter.notifyDataSetChanged()
                                }
                                else{
                                    walkingAnswerFriendAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    } else {
                        Log.d(TAG2, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG2, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<SearchResponseWalkingAnswerDTO>, t: Throwable) {
                Log.e(TAG2, "Search Request Failed: ${t.message}", t)
            }
        })



    }
}