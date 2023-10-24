package com.example.mungnyang.Friend.AddFriend

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mungnyang.databinding.ActivityAddFriendBinding

class AddFriendActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendBinding
    val TAG = "AddFriendActivity"
    private lateinit var searchUserAdapter: SearchUserAdapter
    private var userEmail: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backImagebutton.setOnClickListener {
            finish()
        }

        userEmail = intent.getStringExtra("userEmail").toString()

        binding.searchViewUser.setOnQueryTextListener(searchViewTextListener)
        setAdapter()
    }


    private var searchViewTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            // 검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            // 텍스트 입력/수정시에 호출
            override fun onQueryTextChange(s: String): Boolean {
                searchUserAdapter.filter.filter(s)
                Log.d(TAG, "SearchVies Text is changed : $s")
                return false
            }
        }

    fun setAdapter(){
        //리사이클러뷰에 리사이클러뷰 어댑터 부착
        binding.recyclerUserList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        searchUserAdapter = SearchUserAdapter(ArrayList(), this)
        searchUserAdapter.userEmail = userEmail
        binding.recyclerUserList.adapter = searchUserAdapter
    }
}
