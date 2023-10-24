package com.example.mungnyang.Fragment.ShareToDiary

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mungnyang.DiaryPage.Diary.SearchResponseDiaryDTO
import com.example.mungnyang.Fragment.FriendInfo
import com.example.mungnyang.User.UserRetrofit.RetrofitManager

import com.example.mungnyang.databinding.FragmentShareToDiaryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class shareToDiaryFragment : Fragment() {

    private var _binding: FragmentShareToDiaryBinding? = null
    private val binding get() = _binding!!
    private var communityDiaryList : MutableList<CommunityDiary> = mutableListOf()

    private var friendInfoList : MutableList<FriendInfo> = mutableListOf()

    private var accountEmail = ""
    private var compareEmail = ""

    private var diaryTitle = ""
    private var diaryContent = ""
    private var userNickName = ""
    private var imageURL = ""
    private var petID = ""
    private var selectedDay = ""


    private lateinit var communityDiaryAdapter: CommunityDiaryAdapter

    val retrofit = RetrofitManager.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountEmail = arguments?.getString("accountEmail") ?: ""
        userNickName = arguments?.getString("userNickName") ?: ""
        imageURL = arguments?.getString("imageURL") ?: ""

        friendInfoList = arguments?.getParcelableArrayList<FriendInfo>("friendInfoList")!!
        Log.d("friendInfoList", friendInfoList.toString())

        communityDiaryList = mutableListOf() // 데이터 초기화

        // 데이터를 추가할 함수 호출
        searchDiary(accountEmail, userNickName, imageURL)
        searchFriendDiary(friendInfoList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShareToDiaryBinding.inflate(inflater, container, false)
        val view = binding.root
        initializeViews()
        return view
    }


    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌
        binding.recyclerShareDiary.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        communityDiaryAdapter = CommunityDiaryAdapter(context, userNickName, imageURL, communityDiaryList)
        binding.recyclerShareDiary.adapter = communityDiaryAdapter
    }

    private fun searchDiary(userEmail: String, nickName: String, imageUrl: String) {

        val sendDiarySearch = retrofit.apiService.shareDiary(userEmail)
        sendDiarySearch.enqueue(object : Callback<SearchResponseDiaryDTO> {
            override fun onResponse(call: Call<SearchResponseDiaryDTO>, response: Response<SearchResponseDiaryDTO>) {
                val responseDto = response.body()
                Log.d("테스트", responseDto.toString())

                if (responseDto != null) {
                    val diaryList = responseDto.diaryList

                    if (diaryList.isNotEmpty()) {
                        for (diary in diaryList) {
                            compareEmail = diary.userEmail.toString()

                            // 선택한 펫과 선택한 날짜와 맞는 일정만 추가
                            if (compareEmail == accountEmail) {
                                diaryTitle = diary.diaryTitle.toString()
                                diaryContent = diary.diaryDetail.toString()
                                selectedDay = diary.selectedDay.toString()
                                petID = diary.petID.toString()

                                val communityDiary = CommunityDiary(petID, selectedDay, nickName, accountEmail, compareEmail, diaryTitle, diaryContent, imageUrl)
                                communityDiaryList.add(communityDiary)
                                Log.d("ㅋㄴㄴㄴㄴㄴㄴ", communityDiary.toString())
                            }
                        }
                        // 데이터를 가져온 후에 어댑터에게 변경을 알림
                        communityDiaryAdapter.notifyDataSetChanged()
                    } else {
                        Log.d(ContentValues.TAG, "No diary found for the provided email")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<SearchResponseDiaryDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }

    private fun searchFriendDiary(friendInfoList: List<FriendInfo>) {
        val uniqueDiarySet = HashSet<CommunityDiary>()

        for (friendInfo in friendInfoList) {
            val boardEmail = friendInfo.friendEmail
            val nickName = friendInfo.friendNickname
            val imageUrl = friendInfo.friendImageUrl

            val sendUserSearch = retrofit.apiService.shareDiary(boardEmail)
            sendUserSearch.enqueue(object : Callback<SearchResponseDiaryDTO> {
                override fun onResponse(
                    call: Call<SearchResponseDiaryDTO>,
                    response: Response<SearchResponseDiaryDTO>
                ) {
                    val responseDto = response.body()

                    if (responseDto != null) {
                        val friendDiaryList = responseDto.diaryList

                        if (friendDiaryList.isNotEmpty()) {
                            for (diary in friendDiaryList) {
                                val diaryFriendTitle = diary.diaryTitle.toString()
                                val diaryFriendContent = diary.diaryDetail.toString()
                                val diaryFriendPetID = diary.petID.toString()
                                val diaryFriendselectedDay = diary.selectedDay.toString()

                                val communityDiary = CommunityDiary(
                                    diaryFriendPetID,
                                    diaryFriendselectedDay,
                                    nickName,
                                    accountEmail,
                                    boardEmail,
                                    diaryFriendTitle,
                                    diaryFriendContent,
                                    imageUrl
                                )
                                if (!uniqueDiarySet.contains(communityDiary)) {
                                    uniqueDiarySet.add(communityDiary)
                                }
                            }
                            communityDiaryList.addAll(uniqueDiarySet)
                            communityDiaryAdapter.notifyDataSetChanged()
                        } else {
                            Log.d(TAG, "친구 게시물 띄우기 - No users found for the provided email")
                        }
                    } else {
                        Log.d(TAG, "친구 게시물 띄우기 - Search Response is null")
                    }
                }

                override fun onFailure(call: Call<SearchResponseDiaryDTO>, t: Throwable) {
                    Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
                }
            })
        }
    }

    companion object {
    }
}