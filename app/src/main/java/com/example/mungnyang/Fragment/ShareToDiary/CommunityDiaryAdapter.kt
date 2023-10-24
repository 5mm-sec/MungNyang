package com.example.mungnyang.Fragment.ShareToDiary

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.DiaryPage.Diary.DiaryPageActivity
import com.example.mungnyang.R
import com.example.mungnyang.databinding.ItemShareDiaryBinding


class CommunityDiaryAdapter(private val context: Context?, private val accountNickName: String, private val imageURL: String,
                            private val communityDiaryList: List<CommunityDiary>) :
    RecyclerView.Adapter<CommunityDiaryAdapter.CommunityDiaryViewHolder>() {
    // 호출되는 횟수가 정해졌음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityDiaryViewHolder {
        return CommunityDiaryViewHolder(
            ItemShareDiaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    //내가 그려야 할 항목의 개수를 알려주는 일을 함 (return에서 한줄로 바꿈)
    override fun getItemCount(): Int = communityDiaryList.size

    // 스크롤을 많이하면 할수록 많이 호출됨
    override fun onBindViewHolder(holder: CommunityDiaryViewHolder, position: Int) {
        val communityDiary  = communityDiaryList[position]
        context?.let { holder.bind(it, accountNickName, imageURL, communityDiary ) }
    }

    // ViewHolder를 만든다는거랑 View를 만든다는거랑 똑같은 말임
    class CommunityDiaryViewHolder(private val binding: ItemShareDiaryBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(
            context: Context,
            accountNickName: String,
            imageURL: String,
            communityDiary: CommunityDiary
        ){
            binding.userNickNameText.text = communityDiary.userNickName.toString()
            binding.contentText.text = communityDiary.diaryTitle.toString()

            val defaultImage = R.drawable.defaultuserimage;

            Glide.with(context)
                .load(communityDiary.userImageURL)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(defaultImage)
                .error(defaultImage)
                .fallback(defaultImage)
                .into(binding.userImage)

            binding.shareDiaryInfo.setOnClickListener {
                val boardDiaryIntent = Intent(context, DiaryBoardActivity::class.java)
                boardDiaryIntent.putExtra("userNickName", communityDiary.userNickName.toString())
                boardDiaryIntent.putExtra("petID", communityDiary.petID.toString())
                boardDiaryIntent.putExtra("selectedDay", communityDiary.selectedDay.toString())
                boardDiaryIntent.putExtra("diaryTitle", communityDiary.diaryTitle.toString())
                boardDiaryIntent.putExtra("diaryContent", communityDiary.diaryContent.toString())
                boardDiaryIntent.putExtra("userImageURL", communityDiary.userImageURL.toString())
                boardDiaryIntent.putExtra("accountNickName", accountNickName)
                boardDiaryIntent.putExtra("imageURL", imageURL)
                boardDiaryIntent.putExtra("userEmail", communityDiary.userEmail.toString())
                boardDiaryIntent.putExtra("boardEmail", communityDiary.boardEmail.toString())
                context.startActivity(boardDiaryIntent)
            }

        }

    }
}