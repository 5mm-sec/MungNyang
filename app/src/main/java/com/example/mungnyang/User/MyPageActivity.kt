package com.example.mungnyang.User

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.MainPage.MainActivity
import com.example.mungnyang.MainPage.MoreAddPetActivity
import com.example.mungnyang.Profile.Interface
import com.example.mungnyang.Profile.ProfileAdapter
import com.example.mungnyang.Profile.ProfileManagementOBJ
import com.example.mungnyang.R
import com.example.mungnyang.User.UserDTO.ResponseAccountDTO
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityMypageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private lateinit var petImage: ImageView

    private val REQUEST_UPDATE_USER_INFO = 2
    private var email : String =""
    private var imageUrl : String = ""
    private var userNickName : String = ""
    private val REQUEST_ADD_PET = 1
    private var userEmail : String = ""
    private var userName : String = ""

    private var petURL : String = ""

    // 인터페이스 구현 (adapter에서 데이터 받아오기)
    private val pet_information: Interface = object : Interface {
        override fun cal(petName: String) {} }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        petImage = binding.userImage

        userEmail = intent.getStringExtra("userEmail").toString()
        userName = intent.getStringExtra("userName").toString()

        findUserData(userEmail)

        for(i in ProfileManagementOBJ.List){
            if (i.isClicked){
                petURL = i.petImage
                Log.d("저장 url", petURL)
                Log.d("test 확인", i.test())
            }

        }

        binding.userpageBackImagebuttonEk1.setOnClickListener {
            finish()
        }

        binding.addPet.setOnClickListener {
            val intent = Intent(this, MoreAddPetActivity::class.java)

            intent.putExtra("email", userEmail)
            startActivityForResult(intent, REQUEST_ADD_PET)
        }


        binding.userpageHomeText.setOnClickListener {
            Toast.makeText(this, "홈 클릭", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java) // 호스팅 액티비티로 변경
            intent.putExtra("accountEmail", userEmail)
            intent.putExtra("userName", userName)
            startActivity(intent)
            finish()
        }

        binding.userpageUserInfoText.setOnClickListener {
            val intent = Intent(this, UserInfoPageActivity::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivityForResult(intent, REQUEST_UPDATE_USER_INFO)
        }

        initializeViews()
    }

    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌 ( Linear는 세로로 나열)
        binding.recyclerTest.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 실제로 넣어주는 역할
        binding.recyclerTest.adapter = this?.let { ProfileAdapter(it, ProfileManagementOBJ.List, pet_information ) }

        // 어댑터 설정
        ProfileManagementOBJ.mainAdapter = binding.recyclerTest.adapter as ProfileAdapter?

    }

    // 수정 성공 후 수정한 데이터로 갱신
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UPDATE_USER_INFO && resultCode == RESULT_OK) {
            findUserData(userEmail)
        }
    }

    private fun findUserData(userEmail: String?) {

        val defaultImage = R.drawable.defaultuserimage

        if(userEmail != null){
            val retrofit = RetrofitManager.instance

            val sendUserSearch = retrofit.apiService.findUser(userEmail)
            sendUserSearch.enqueue(object : Callback<ResponseAccountDTO> {
                override fun onResponse(
                    call: Call<ResponseAccountDTO>,
                    response: Response<ResponseAccountDTO>
                ) {
                    val responseDto = response.body()
                    if (responseDto != null) {
                        val userList = responseDto.userList

                        if (userList.isNotEmpty()) {
                            for (user in userList) {

                                email = user.email.toString()
                                userNickName = user.nickName.toString()
                                imageUrl = user.imageUrl.toString()


                                Log.d(ContentValues.TAG, "User : $user")

                                // Load and display the user's image
                                Glide.with(this@MyPageActivity)
                                    .load(imageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .placeholder(defaultImage)
                                    .error(defaultImage)
                                    .fallback(defaultImage)
                                    .into(petImage)

                                binding.userNickname.text = userNickName
                            }


                        } else {
                            Log.d(ContentValues.TAG, "No users found for the provided email")
                        }
                    } else {
                        Log.d(ContentValues.TAG, "Search Response is null")
                    }
                }

                override fun onFailure(call: Call<ResponseAccountDTO>, t: Throwable) {
                    Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
                }
            })
        }

    }
}