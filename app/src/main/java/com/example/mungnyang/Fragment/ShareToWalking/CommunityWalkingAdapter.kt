package com.example.mungnyang.Fragment.ShareToWalking

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.DiaryPage.Diary.DiaryPageActivity
import com.example.mungnyang.Fragment.ShareToDiary.CommunityDiary
import com.example.mungnyang.Fragment.ShareToDiary.DiaryBoardActivity
import com.example.mungnyang.R
import com.example.mungnyang.databinding.ItemShareDiaryBinding
import com.example.mungnyang.databinding.ItemShareWalkingBinding


class CommunityWalkingAdapter(private val context: Context?, private val accountNickName: String, private val imageURL: String,
                            private val communityWalkingList: List<CommunityWalking>) :
    RecyclerView.Adapter<CommunityWalkingAdapter.CommunityWalkingViewHolder>() {
    // 호출되는 횟수가 정해졌음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityWalkingViewHolder {
        return CommunityWalkingViewHolder(
            ItemShareWalkingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    //내가 그려야 할 항목의 개수를 알려주는 일을 함 (return에서 한줄로 바꿈)
    override fun getItemCount(): Int = communityWalkingList.size

    // 스크롤을 많이하면 할수록 많이 호출됨
    override fun onBindViewHolder(holder: CommunityWalkingViewHolder, position: Int) {
        val communityWalking  = communityWalkingList[position]
        context?.let { holder.bind(it, accountNickName, imageURL, communityWalking ) }
    }

    // ViewHolder를 만든다는거랑 View를 만든다는거랑 똑같은 말임
    class CommunityWalkingViewHolder(private val binding: ItemShareWalkingBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(
            context: Context,
            accountNickName: String,
            imageURL: String,
            communityWalking: CommunityWalking
        ){
            binding.userNickNameText.text = communityWalking.userNickName.toString()
            binding.selectedDay.text = communityWalking.selectedDay.toString()
            binding.time.text = "${communityWalking.startTime}~${communityWalking.endTime}"
            binding.elapseTime.text = communityWalking.elapsedTime.toString()

            val defaultImage = R.drawable.defaultuserimage;

            Log.d("테스트 유저 이미지 확인 ", imageURL)
            Glide.with(context)
                .load(communityWalking.userImageURL)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(defaultImage)
                .error(defaultImage)
                .fallback(defaultImage)
                .into(binding.userImage)

            val nullImage = R.drawable.nullimage;

            Glide.with(context)
                .load(communityWalking.mapImageURL.toString())
                .placeholder(nullImage)
                .error(nullImage)
                .fallback(nullImage)
                .into(binding.mapImage)

            binding.walkingBoardInfo.setOnClickListener {
                val boardWalkingIntent = Intent(context, WalkingBoardActivity::class.java)
                boardWalkingIntent.putExtra("petID", communityWalking.petID.toString())
                boardWalkingIntent.putExtra("selectedDay", communityWalking.selectedDay.toString())
                boardWalkingIntent.putExtra("userNickName", communityWalking.userNickName.toString())
                boardWalkingIntent.putExtra("userEmail", communityWalking.userEmail.toString())
                boardWalkingIntent.putExtra("boardEmail", communityWalking.boardEmail.toString())

                boardWalkingIntent.putExtra("elapsedTime", communityWalking.elapsedTime.toString())
                boardWalkingIntent.putExtra("startTime", communityWalking.startTime.toString())
                boardWalkingIntent.putExtra("endTime", communityWalking.endTime.toString())
                boardWalkingIntent.putExtra("distance", communityWalking.distance.toString())
                boardWalkingIntent.putExtra("petKcalAmount", communityWalking.petKcalAmount.toString())
                boardWalkingIntent.putExtra("myKcalAmount", communityWalking.myKcalAmount.toString())
                boardWalkingIntent.putExtra("mapImageURL", communityWalking.mapImageURL.toString())

                boardWalkingIntent.putExtra("userImageURL", communityWalking.userImageURL.toString())
                boardWalkingIntent.putExtra("accountNickName", accountNickName)
                boardWalkingIntent.putExtra("imageURL", imageURL)

                context.startActivity(boardWalkingIntent)
            }

        }

    }
}