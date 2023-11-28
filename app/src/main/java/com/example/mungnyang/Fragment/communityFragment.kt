package com.example.mungnyang.Fragment

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.mungnyang.DiaryPage.Diary.ResponseDiaryCountDTO
import com.example.mungnyang.Fragment.ShareToDiary.Answer.ResponseDiaryAnswerCountDTO
import com.example.mungnyang.Fragment.ShareToDiary.CommunityPagerAdapter
import com.example.mungnyang.Fragment.Walking.ResponseWalkingAnswerCountDTO
import com.example.mungnyang.Fragment.Walking.ResponseWalkingCountDTO
import com.example.mungnyang.Friend.AcceptFriend.ResponseWaitFriendDTO
import com.example.mungnyang.Friend.AddFriend.AddFriendActivity
import com.example.mungnyang.Friend.FriendListActivity
import com.example.mungnyang.Friend.ResponseFriendCountDTO
import com.example.mungnyang.MainPage.MainActivity
import com.example.mungnyang.R
import com.example.mungnyang.User.Login.LoginActivity
import com.example.mungnyang.User.MyPageActivity
import com.example.mungnyang.User.UserDTO.ResponseAccountDTO
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class communityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private var accountEmail = ""

    val TAG = "communityFragment"

    private var email : String =""
    private var imageUrl : String = ""
    private var userNickName : String = ""
    private var userName : String = ""
    private var waitFriendEmail : String = ""
    private var status : String = ""
    private var waitUserEmail : String = ""


    private var friendInfoList : MutableList<FriendInfo> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)

        val view = binding.root
        accountEmail = arguments?.getString("accountEmail") ?: ""
        userName = arguments?.getString("userName") ?: ""

        findFriend(accountEmail)
        findFriendCount(accountEmail)
        findDiaryAnswerCount(accountEmail)
        findDiaryCount(accountEmail)

        binding.inViewDrawer.userEmail.text = accountEmail
        binding.inViewDrawer.userName.text = userName

        binding.communityFriend.setOnClickListener {
            val friendIntent  = Intent(requireContext(), FriendListActivity::class.java)
            friendIntent.putExtra("userEmail", accountEmail)
            startActivity(friendIntent)
        }


        // 열기 버튼
        binding.communityMenubtn.setOnClickListener {
            binding.dlMain.open()
        }

        // 닫기 버튼
        binding.inViewDrawer.ivDrawerClose.setOnClickListener {
            binding.dlMain.close()
        }

        val mainActivity = activity as MainActivity

        val bnv_main = mainActivity.bnv_main

        binding.inViewDrawer.homeSetting.setOnClickListener {
            Toast.makeText(activity, "홈 클릭", Toast.LENGTH_SHORT).show()

            val afterHomeFragment = AfterHomeFragment()
            // accountEmail 값을 Bundle에 추가하여 인자로 전달
            val args = Bundle()
            args.putString("accountEmail", accountEmail)
            args.putString("userName", userName)
            afterHomeFragment.arguments = args

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.containers, afterHomeFragment)
            transaction.addToBackStack(null)
            transaction.commit()

            bnv_main.menu.findItem(R.id.home).isChecked = true
            bnv_main.itemIconTintList = ContextCompat.getColorStateList(requireContext(), R.color.click_home)
            bnv_main.itemTextColor = ContextCompat.getColorStateList(requireContext(), R.color.click_home)
        }

        binding.inViewDrawer.adduserSetting.setOnClickListener {

            val intent = Intent(requireContext(), AddFriendActivity::class.java)
            intent.putExtra("userEmail", accountEmail)
            startActivity(intent)
        }

        binding.inViewDrawer.userSetting.setOnClickListener {

            val intent = Intent(requireContext(), MyPageActivity::class.java)
            intent.putExtra("userEmail", accountEmail)
            intent.putExtra("userName", userName)
            startActivity(intent)

        }


        binding.inViewDrawer.logoutSetting.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("로그아웃")
            builder.setMessage("로그아웃 하시겠습니까?")

            // 예 버튼 처리
            builder.setPositiveButton("예") { dialog, which ->
                // 예 버튼을 클릭하면 로그아웃 처리를 수행
                navigateToLoginActivity()
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

        binding.inViewDrawer.friendListSetting.setOnClickListener {
            val friendIntent  = Intent(requireContext(), FriendListActivity::class.java)
            friendIntent.putExtra("userEmail", accountEmail)
            startActivity(friendIntent)
        }

        return view
    }

    private fun createCommunityPagerAdapter() {

        // 데이터를 성공적으로 가져왔을 때 CommunityPagerAdapter 생성
        val viewPager = view?.findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = view?.findViewById<TabLayout>(R.id.tabLayout)

        val pagerAdapter = CommunityPagerAdapter(childFragmentManager, accountEmail, userNickName, imageUrl, friendInfoList )
        viewPager?.adapter = pagerAdapter
        tabLayout?.setupWithViewPager(viewPager)
    }

    private fun findFriend(userEmail: String){

        val retrofit = RetrofitManager.instance

        val sendWaitFriendSearch = retrofit.apiService.searchWaitFriend(userEmail)
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

                            if (userEmail == waitFriendEmail && status == "수락"){
                                //findUserData(waitUserEmail)
                                //currentStatus = "수락"
                                //findFriendEmailList.add(waitUserEmail)

                                Log.d("조건만족 친구", waitUserEmail)
                                loadFriendInformation(waitUserEmail)
                            }
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

    private fun loadFriendInformation(friendEmail: String) {
        val retrofit = RetrofitManager.instance

        val sendUserSearch = retrofit.apiService.findUser(friendEmail)
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
                            val nickname = user.nickName.toString()
                            val imageUrl = user.imageUrl.toString()
                            val email = user.email.toString()

                            val friendInfo = FriendInfo(email, nickname, imageUrl)
                            friendInfoList.add(friendInfo)
                        }
                        findUserData(accountEmail)
                    }
                    else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                }
                else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseAccountDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }

    private fun findUserData(userEmail: String?) {

        if (userEmail != null) {
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
                                userName = user.userName.toString()
                                imageUrl = user.imageUrl.toString()

                                binding.communityNametextview.text = userNickName
                                createCommunityPagerAdapter()
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

    private fun findFriendCount(findUserEmail: String){

        val retrofit = RetrofitManager.instance

        val sendWaitFriendSearch = retrofit.apiService.friendCount(findUserEmail)
        sendWaitFriendSearch.enqueue(object : Callback<ResponseFriendCountDTO> {
            override fun onResponse(
                call: Call<ResponseFriendCountDTO>,
                response: Response<ResponseFriendCountDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {

                    val friendCount = responseDto.count
                    if (friendCount != null) {
                        Log.d("친구 수", friendCount.toString())

                        binding.communityFriend.text = "${friendCount}명"

                    } else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseFriendCountDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })

    }

    private fun findDiaryAnswerCount(findUserEmail: String){

        val retrofit = RetrofitManager.instance

        val sendAnswerCountSearch = retrofit.apiService.answerCount(findUserEmail)
        sendAnswerCountSearch.enqueue(object : Callback<ResponseDiaryAnswerCountDTO> {
            override fun onResponse(
                call: Call<ResponseDiaryAnswerCountDTO>,
                response: Response<ResponseDiaryAnswerCountDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {

                    val answerCount = responseDto.answerCount
                    if (answerCount != null) {
                        Log.d("일지 댓글 수", answerCount.toString())
                        findWalkingAnswerCount(findUserEmail, answerCount)
                    } else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseDiaryAnswerCountDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })

    }
    private fun findWalkingAnswerCount(findUserEmail: String, diaryAnswerCount: Int){
        val retrofit = RetrofitManager.instance

        val sendWalkingAnswerCountSearch = retrofit.apiService.walkingAnswerCount(findUserEmail)
        sendWalkingAnswerCountSearch.enqueue(object : Callback<ResponseWalkingAnswerCountDTO> {
            override fun onResponse(
                call: Call<ResponseWalkingAnswerCountDTO>,
                response: Response<ResponseWalkingAnswerCountDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {

                    val walkingAnswerCount = responseDto.walkingAnswerCount
                    if (walkingAnswerCount != null) {

                        Log.d("산책 댓글 수", walkingAnswerCount.toString())
                        binding.communityAnswer.text = "${(diaryAnswerCount + walkingAnswerCount)}개"

                    } else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseWalkingAnswerCountDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }

    private fun findDiaryCount(findUserEmail: String){

        val retrofit = RetrofitManager.instance

        val sendDiaryCountSearch = retrofit.apiService.diaryCount(findUserEmail)
        sendDiaryCountSearch.enqueue(object : Callback<ResponseDiaryCountDTO> {
            override fun onResponse(
                call: Call<ResponseDiaryCountDTO>,
                response: Response<ResponseDiaryCountDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {

                    val diaryCount = responseDto.diaryCount
                    if (diaryCount != null) {
                        Log.d("일지 수", diaryCount.toString())

                        findWalkingCount(findUserEmail, diaryCount)

                    } else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseDiaryCountDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }


    private fun findWalkingCount(findUserEmail: String, diaryCount: Int){
        val retrofit = RetrofitManager.instance

        val sendWalkingCountSearch = retrofit.apiService.walkingCount(findUserEmail)
        sendWalkingCountSearch.enqueue(object : Callback<ResponseWalkingCountDTO> {
            override fun onResponse(
                call: Call<ResponseWalkingCountDTO>,
                response: Response<ResponseWalkingCountDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {

                    val walkingCount = responseDto.walkingCount
                    if (walkingCount != null) {
                        Log.d("산책 수", walkingCount.toString())
                        binding.communityHowmanywriting.text = "${(diaryCount + walkingCount)}개"

                    } else {
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseWalkingCountDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }

    // 로그인 액티비티로 이동
    private fun navigateToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    companion object {
    }
}