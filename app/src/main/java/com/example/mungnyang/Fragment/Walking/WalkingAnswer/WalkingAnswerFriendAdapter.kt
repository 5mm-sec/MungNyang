package com.example.mungnyang.Fragment.Walking.WalkingAnswer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ItemDiaryAnswerFriendBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalkingAnswerFriendAdapter(private val context: Context?, private val walkingAnswerList: List<WalkingAnswer>) : RecyclerView.Adapter<WalkingAnswerFriendAdapter.AnswerWalkingFriendViewHolder>() {

    private val retrofit = RetrofitManager.instance



    // 호출되는 횟수가 정해졌음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerWalkingFriendViewHolder {
        return AnswerWalkingFriendViewHolder(
            ItemDiaryAnswerFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    //내가 그려야 할 항목의 개수를 알려주는 일을 함 (return에서 한줄로 바꿈)
    override fun getItemCount(): Int = walkingAnswerList.size

    // 스크롤을 많이하면 할수록 많이 호출됨
    override fun onBindViewHolder(holder: AnswerWalkingFriendViewHolder, position: Int) {
        val diaryAnswerList = walkingAnswerList[position]
        context?.let { holder.bind(it, diaryAnswerList) }
    }

    // ViewHolder를 만든다는거랑 View를 만든다는거랑 똑같은 말임
    class AnswerWalkingFriendViewHolder(private val binding: ItemDiaryAnswerFriendBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(
            context: Context,
            walkingAnswerList: WalkingAnswer
        ){
            binding.userNickNameText.text = walkingAnswerList.userNickName.toString()
            binding.answerText.text = walkingAnswerList.answerText.toString()
            binding.timeText.text = walkingAnswerList.time.toString()

            val defaultImage = R.drawable.defaultuserimage;

            Glide.with(context)
                .load(walkingAnswerList.userImageURL)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(defaultImage)
                .error(defaultImage)
                .fallback(defaultImage)
                .into(binding.answerUserImage)

        }

    }

}