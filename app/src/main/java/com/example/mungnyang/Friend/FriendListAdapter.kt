package com.example.mungnyang.Friend

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.Friend.AcceptFriend.AcceptFriendDTO
import com.example.mungnyang.Friend.AcceptFriend.Friend
import com.example.mungnyang.Friend.AcceptFriend.ResponseAcceptFriendDTO
import com.example.mungnyang.Friend.AddFriend.AddFriendDTO
import com.example.mungnyang.Friend.AddFriend.ResponseAddFriendDTO
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ItemWaitFriendListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendListAdapter(private val context: Context, private val friendList: MutableList<Friend>) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    var userEmail: String = ""
    var waitUserEmail: String = ""

    //var status: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemWaitFriendListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendList = friendList[position]
        holder.bind(context, friendList)

    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    inner class ViewHolder(private val binding: ItemWaitFriendListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context,
                 friend: Friend
        ) {

            binding.friendNameText.text = friend.name
            binding.friendEmailText.text = friend.friendEmail
            waitUserEmail = friend.friendEmail

            val defaultImage = R.drawable.defaultuserimage;

            Glide.with(context)
                .load(friend.friendImageURL)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(defaultImage)
                .error(defaultImage)
                .fallback(defaultImage)
                .into(binding.friendImage)

            if (friend.status == "수락"){
                binding.addFriend.visibility = View.GONE
            }
            else{
                binding.addFriend.setOnClickListener {
                    acceptFriend(userEmail)
                }
            }

            binding.deleteFriendBtn.setOnClickListener {
                val clickEmail = friend.friendEmail
                deleteFriend(userEmail, clickEmail)
            }

        }

        private fun deleteFriend(userEmail: String, clickEmail: String){

            val retrofit = RetrofitManager.instance

            val sendUserDelete = retrofit.apiService.deleteFriend(userEmail, clickEmail)
            sendUserDelete.enqueue(object : Callback<ResponseAddFriendDTO> {

                override fun onResponse(call: Call<ResponseAddFriendDTO>, response: Response<ResponseAddFriendDTO>) {

                    val responseDto = response.body()
                    Log.d(ContentValues.TAG, "Response received: $responseDto");
                    if (responseDto != null) {
                        Log.d(ContentValues.TAG, "Response received: $responseDto");

                        if (responseDto.response) {

                            Toast.makeText(context, "삭제 완료!", Toast.LENGTH_SHORT)
                                .show()
                            val position = friendList.indexOfFirst { it.friendEmail == clickEmail }
                            if (position != -1) {
                                friendList.removeAt(position)
                                // 어댑터에게 항목 삭제를 알림
                                notifyItemRemoved(position)
                            }

                        } else {
                            Toast.makeText(context, "삭제 불가능!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "응답이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseAddFriendDTO>, t: Throwable) {
                    Toast.makeText(context, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                        .show()
                }
            })


        }

        private fun acceptFriend(userEmail1: String?) {
            
            val acceptFriendDTO = AcceptFriendDTO(
                waitUserEmail,
                userEmail1.toString(),
                "수락"
            )
            if (userEmail1 != null) {
                val retrofit = RetrofitManager.instance

                val sendAddFriend = retrofit.apiService.acceptFriend(acceptFriendDTO)
                sendAddFriend.enqueue(object : Callback<ResponseAcceptFriendDTO> {
                    override fun onResponse(
                        call: Call<ResponseAcceptFriendDTO>,
                        response: Response<ResponseAcceptFriendDTO>
                    ) {

                        val responseDto = response.body()
                        Log.d(ContentValues.TAG, "Response received: $responseDto");
                        if (responseDto != null) {
                            Log.d(ContentValues.TAG, "Response received: $responseDto");

                            if (responseDto.response) {

                                binding.addFriend.visibility = View.GONE
                                Toast.makeText(context, "요청 완료", Toast.LENGTH_SHORT)
                                    .show()
                                addFriend(userEmail)

                            } else {
                                Toast.makeText(context, "추가 불가능!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(context, "응답이 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseAcceptFriendDTO>, t: Throwable) {
                        Toast.makeText(context, "추가 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        }


        private fun addFriend(userEmail1: String?) {

            val addFriendDTO = AddFriendDTO(
                userEmail1.toString(),
                waitUserEmail,
                "수락"
            )
            if (userEmail1 != null) {
                val retrofit = RetrofitManager.instance

                val sendAddFriend = retrofit.apiService.addFriend(addFriendDTO)
                sendAddFriend.enqueue(object : Callback<ResponseAddFriendDTO> {
                    override fun onResponse(
                        call: Call<ResponseAddFriendDTO>,
                        response: Response<ResponseAddFriendDTO>
                    ) {

                        val responseDto = response.body()
                        Log.d(ContentValues.TAG, "Response received: $responseDto");
                        if (responseDto != null) {
                            Log.d(ContentValues.TAG, "Response received: $responseDto");

                            if (responseDto.response) {
                                Toast.makeText(context, "요청 완료", Toast.LENGTH_SHORT)
                                    .show()

                            } else {
                                Toast.makeText(context, "추가 불가능!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(context, "응답이 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseAddFriendDTO>, t: Throwable) {
                        Toast.makeText(context, "추가 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        }

    }
}