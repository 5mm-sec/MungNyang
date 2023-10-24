package com.example.mungnyang.Fragment.ShareToWalking

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mungnyang.DiaryPage.Diary.SearchResponseDiaryDTO
import com.example.mungnyang.Fragment.FriendInfo
import com.example.mungnyang.Fragment.ShareToDiary.CommunityDiary
import com.example.mungnyang.Fragment.ShareToDiary.CommunityDiaryAdapter
import com.example.mungnyang.Fragment.Walking.SearchResponseWalkingDTO
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.FragmentShareToWalkingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar


class shareToWalkingFragment : Fragment() {

    private var _binding: FragmentShareToWalkingBinding? = null
    private val binding get() = _binding!!

    private var communityWalkingList : MutableList<CommunityWalking> = mutableListOf()

    private var friendInfoList : MutableList<FriendInfo> = mutableListOf()

    private var walkingDiaryPetID = ""
    private var selectedDay = ""
    private var userNickName = ""
    private var accountEmail = ""
    private var compareEmail = ""

    private var elapsedTime = ""
    private var startTime = ""
    private var endTime = ""
    private var distance = ""
    private var petKcalAmount = ""
    private var myKcalAmount = ""
    private var imageURL = ""
    private var userImageURL = ""
    private var mapImageURL = ""

    private lateinit var communityWalkingAdapter: CommunityWalkingAdapter
    val retrofit = RetrofitManager.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountEmail = arguments?.getString("accountEmail") ?: ""
        userNickName = arguments?.getString("userNickName") ?: ""
        imageURL = arguments?.getString("imageURL") ?: ""

        friendInfoList = arguments?.getParcelableArrayList<FriendInfo>("friendInfoList")!!

        searchWalkingDiary(accountEmail, userNickName, imageURL)
        searchFriendWalking(friendInfoList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShareToWalkingBinding.inflate(inflater, container, false)
        val view = binding.root
        initializeViews()
        return view
    }

    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌
        binding.recyclerShareWalkingDiary.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        communityWalkingAdapter = CommunityWalkingAdapter(context, userNickName, imageURL, communityWalkingList)
        binding.recyclerShareWalkingDiary.adapter = communityWalkingAdapter
    }

    private fun searchWalkingDiary(userEmail: String, nickName: String, imageUrl: String) {

        val sendWalkingSearch = retrofit.apiService.shareWalkingDiary(userEmail)
        sendWalkingSearch.enqueue(object : Callback<SearchResponseWalkingDTO> {
            override fun onResponse(call: Call<SearchResponseWalkingDTO>, response: Response<SearchResponseWalkingDTO>) {
                val responseDto = response.body()
                Log.d("테스트1212", responseDto.toString())

                if (responseDto != null) {
                    val walkingList = responseDto.walkingDiaryList

                    if (walkingList.isNotEmpty()) {
                        for (walkingDiary in walkingList) {
                            compareEmail = walkingDiary.accountEmail.toString()
                            // 선택한 펫과 선택한 날짜와 맞는 일정만 추가
                            if (compareEmail == accountEmail) {
                                selectedDay = walkingDiary.selectedDay.toString()
                                elapsedTime = walkingDiary.elapsedTime.toString()
                                startTime = walkingDiary.startTime.toString()
                                endTime = walkingDiary.endTime.toString()
                                distance = walkingDiary.distance.toString()
                                petKcalAmount = walkingDiary.petKcalAmount.toString()
                                myKcalAmount = walkingDiary.myKcalAmount.toString()
                                mapImageURL = walkingDiary.mapImageURL.toString()

                                walkingDiaryPetID = walkingDiary.petID.toString()


                                val communityWalking = CommunityWalking(walkingDiaryPetID, selectedDay, nickName, accountEmail, compareEmail, elapsedTime, startTime, endTime, distance, petKcalAmount, myKcalAmount, imageUrl, mapImageURL)
                                communityWalkingList.add(communityWalking)

                            }
                        }
                        // 데이터를 가져온 후에 어댑터에게 변경을 알림
                        communityWalkingAdapter.notifyDataSetChanged()
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
    }

    private fun searchFriendWalking(friendInfoList: List<FriendInfo>) {


        for (friendInfo in friendInfoList) {
            val uniqueDiarySet = HashSet<CommunityWalking>()
            Log.d("친구 리스트 테스트입ㄴㅇㅁㅈㅇㅈㅁ", friendInfo.toString())
            val friendBoardEmail = friendInfo.friendEmail
            val friendNickName = friendInfo.friendNickname
            val friendImageUrl = friendInfo.friendImageUrl

            val sendUserSearch = retrofit.apiService.shareWalkingDiary(friendBoardEmail)
            sendUserSearch.enqueue(object : Callback<SearchResponseWalkingDTO> {
                override fun onResponse(
                    call: Call<SearchResponseWalkingDTO>,
                    response: Response<SearchResponseWalkingDTO>
                ) {
                    val responseDto = response.body()
                    Log.d("테스트22222", responseDto.toString())
                    if (responseDto != null) {
                        val friendWalkingDiaryList = responseDto.walkingDiaryList

                        if (friendWalkingDiaryList.isNotEmpty()) {
                            for (walking in friendWalkingDiaryList) {
                                val diaryFriendElapseTime = walking.elapsedTime.toString()
                                val diaryFriendStartTime = walking.startTime.toString()
                                val diaryFriendEndTime = walking.endTime.toString()
                                val diaryFriendDistance = walking.distance.toString()
                                val diaryFriendPetKcal = walking.petKcalAmount.toString()
                                val diaryFriendMyKcal = walking.myKcalAmount.toString()
                                val diaryFriendMapImage = walking.mapImageURL.toString()
                                val diaryFriendPetID = walking.petID.toString()
                                val diaryFriendSelectedDay = walking.selectedDay.toString()

                                val communityWalking = CommunityWalking(
                                    diaryFriendPetID,
                                    diaryFriendSelectedDay,
                                    friendNickName,
                                    accountEmail,
                                    friendBoardEmail,
                                    diaryFriendElapseTime,
                                    diaryFriendStartTime,
                                    diaryFriendEndTime,
                                    diaryFriendDistance,
                                    diaryFriendPetKcal,
                                    diaryFriendMyKcal,
                                    friendImageUrl,
                                    diaryFriendMapImage)

                                Log.d("테스트3333", communityWalking.toString())
                                if (!uniqueDiarySet.contains(communityWalking)) {
                                    uniqueDiarySet.add(communityWalking)
                                }
                            }
                            communityWalkingList.addAll(uniqueDiarySet)
                            communityWalkingAdapter.notifyDataSetChanged()
                        } else {
                            Log.d(ContentValues.TAG, "친구 게시물 띄우기 - No users found for the provided email")
                        }
                    } else {
                        Log.d(ContentValues.TAG, "친구 게시물 띄우기 - Search Response is null")
                    }
                }

                override fun onFailure(call: Call<SearchResponseWalkingDTO>, t: Throwable) {
                    Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
                }
            })
        }
    }


    companion object {
    }
}