package com.example.mungnyang.Friend

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mungnyang.Friend.AcceptFriend.Friend
import com.example.mungnyang.Friend.AcceptFriend.ResponseWaitFriendDTO
import com.example.mungnyang.User.UserDTO.ResponseAccountDTO
import com.example.mungnyang.User.UserRetrofit.RetrofitManager

import com.example.mungnyang.databinding.ActivityFriendListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding

    private lateinit var friendListAdapter: FriendListAdapter

    val TAG = "AddFriendActivity"
    private var userEmail: String = ""
    private var friendList : MutableList<Friend> = mutableListOf()

    private var waitFriendEmail: String = ""
    private var waitUserEmail: String = ""

    private var status: String = ""

    private var findUserName: String = ""
    private var findUserEmail: String = ""
    private var findUserImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backImagebutton.setOnClickListener {
            finish()
        }

        userEmail = intent.getStringExtra("userEmail").toString()
        Log.d("친구 목록 유저", userEmail)

        friendListAdapter = FriendListAdapter(this@FriendListActivity, friendList)

        findWaitFriend(userEmail)

        initializeViews()

    }


    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌 ( Linear는 세로로 나열)
        binding.friendList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        friendListAdapter.userEmail = userEmail
        //friendListAdapter.status = status
        //friendListAdapter.waitUserEmail = waitUserEmail
        // 실제로 넣어주는 역할
        binding.friendList.adapter = friendListAdapter

    }

    private fun findWaitFriend(friendEmail: String){

        val retrofit = RetrofitManager.instance

        val sendWaitFriendSearch = retrofit.apiService.searchWaitFriend(friendEmail)
        sendWaitFriendSearch.enqueue(object : Callback<ResponseWaitFriendDTO> {
            override fun onResponse(
                call: Call<ResponseWaitFriendDTO>,
                response: Response<ResponseWaitFriendDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val waitFriendList = responseDto.waitFriendList

                    if (waitFriendList.isNotEmpty()) {

                        for (friendList in waitFriendList) {

                            waitFriendEmail = friendList.friendEmail.toString()
                            status = friendList.status.toString()
                            waitUserEmail = friendList.userEmail.toString()
                            Log.d("텥ㅌ", friendList.toString())

                            if (userEmail == waitFriendEmail && status == "대기"){
                                //currentStatus = "대기"
                                waitUserData(waitUserEmail)
                            }
                            else if (userEmail == waitFriendEmail && status == "수락"){
                                //currentStatus = "수락"
                                acceptUserData(waitUserEmail)
                            }
//                            if (userEmail == waitUserEmail && status == "수락"){
//                                findUserData2(waitUserEmail)
//                            }
                        }
                    } else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseWaitFriendDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })

    }

    private fun waitUserData(userEmail1: String){

        val retrofit = RetrofitManager.instance

        val sendUserSearch = retrofit.apiService.findUser(userEmail1)
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
                            findUserEmail = user.email.toString()
                            findUserName = user.userName.toString()
                            findUserImage = user.imageUrl.toString()

                            val friend = Friend(findUserName, findUserImage, findUserEmail, "대기")
                            friendList.add(friend)
                            friendListAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseAccountDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })

    }

    private fun acceptUserData(userEmail1: String){

        val retrofit = RetrofitManager.instance

        val sendUserSearch = retrofit.apiService.findUser(userEmail1)
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
                            findUserEmail = user.email.toString()
                            findUserName = user.userName.toString()
                            findUserImage = user.imageUrl.toString()

                            val friend = Friend( findUserName, findUserImage, findUserEmail, "수락")
                            friendList.add(friend)
                            friendListAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseAccountDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })

    }

}
