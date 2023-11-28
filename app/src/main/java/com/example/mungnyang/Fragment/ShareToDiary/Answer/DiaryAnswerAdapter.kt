package com.example.mungnyang.Fragment.ShareToDiary.Answer

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
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ItemDiaryAnswerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryAnswerAdapter(private val context: Context?, private val diaryAnswerList: MutableList<DiaryAnswer>, private val boardEmail: String) : RecyclerView.Adapter<DiaryAnswerAdapter.AnswerDiaryViewHolder>() {

    // 호출되는 횟수가 정해졌음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerDiaryViewHolder {
        return AnswerDiaryViewHolder(
            ItemDiaryAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }


    fun deleteItem(position: Int) {
        diaryAnswerList.removeAt(position)
        notifyItemRemoved(position)
    }
    //내가 그려야 할 항목의 개수를 알려주는 일을 함 (return에서 한줄로 바꿈)
    override fun getItemCount(): Int = diaryAnswerList.size

    // 스크롤을 많이하면 할수록 많이 호출됨
    override fun onBindViewHolder(holder: AnswerDiaryViewHolder, position: Int) {
        val diaryAnswerList = diaryAnswerList[position]
        context?.let { holder.bind(it, diaryAnswerList, this, boardEmail) }
    }

    // ViewHolder를 만든다는거랑 View를 만든다는거랑 똑같은 말임
    class AnswerDiaryViewHolder(private val binding: ItemDiaryAnswerBinding ) :
        RecyclerView.ViewHolder(binding.root)
    {
        private var boardTitleToDB : String = ""
        private var answerIDToDB : String = ""
        private var answerTextToDB : String = ""

        private var answerEmail : String = ""
        private var boardTitle : String = ""
        private var answerText : String = ""
        private lateinit var context : Context
        val TAG = "DiaryAnswerAdapter"
        private val retrofit = RetrofitManager.instance

        fun bind(
            context: Context,
            diaryAnswerList: DiaryAnswer,
            diaryAnswerAdapter: DiaryAnswerAdapter,
            boardEmail1: String
        ){
            this.context = context
            binding.userNickNameText.text = diaryAnswerList.userNickName.toString()
            binding.answerText.text = diaryAnswerList.answerText.toString()
            binding.timeText.text = diaryAnswerList.time.toString()

            answerText = diaryAnswerList.answerText.toString()
            boardTitle = diaryAnswerList.boardTitle.toString()
            answerEmail = diaryAnswerList.userEmail.toString()

            val defaultImage = R.drawable.defaultuserimage;

            Glide.with(context)
                .load(diaryAnswerList.userImageURL)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(defaultImage)
                .error(defaultImage)
                .fallback(defaultImage)
                .into(binding.answerUserImage)

            searchByBoardEmail(answerEmail)

            if(boardEmail1 == answerEmail) {
                binding.deleteAnswerBtn.setOnClickListener {

                    //deleteAnswer(answerIDToDB.toInt())
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        deleteAnswer(answerIDToDB.toInt())
                        diaryAnswerAdapter.deleteItem(position) // 리사이클러뷰에서 해당 항목 삭제
                    }

                }
            }
            else{
                binding.deleteAnswerBtn.alpha = 0.0f
            }

        }

        private fun searchByBoardEmail(answerEmail2: String){

            val sendDiaryAnswerSearch = retrofit.apiService.searchByBoardEmail(answerEmail2)
            sendDiaryAnswerSearch.enqueue(object : Callback<SearchResponseDiaryAnswerDTO> {
                override fun onResponse(
                    call: Call<SearchResponseDiaryAnswerDTO>,
                    response: Response<SearchResponseDiaryAnswerDTO>
                ) {
                    val responseDto = response.body()
                    if (responseDto != null) {
                        val answerList = responseDto.answerList

                        if (answerList.isNotEmpty()) {

                            for (answerList in answerList) {

                                answerTextToDB = answerList.answerText.toString()
                                boardTitleToDB= answerList.boardTitle.toString()

                                if (answerText == answerTextToDB && answerEmail == answerEmail2 && boardTitle == boardTitleToDB){
                                    answerIDToDB = answerList.diaryAnswerID.toString()
                                }

                            }
                        } else {
                            Log.d(TAG, "No users found for the provided email")
                        }
                    } else {
                        Log.d(TAG, "Search Response is null")
                    }
                }

                override fun onFailure(call: Call<SearchResponseDiaryAnswerDTO>, t: Throwable) {
                    Log.e(TAG, "Search Request Failed: ${t.message}", t)
                }
            })

        }

        private fun deleteAnswer(answerIDToDB: Int){
            val sendPetDelete = retrofit.apiService.deleteAnswer(answerIDToDB)
            sendPetDelete.enqueue(object : Callback<ResponseDiaryAnswerDTO> {

                override fun onResponse(call: Call<ResponseDiaryAnswerDTO>, response: Response<ResponseDiaryAnswerDTO>) {

                    val responseDto = response.body()
                    Log.d(ContentValues.TAG, "댓글 삭제 Response received: $responseDto");
                    if (responseDto != null) {

                        if (responseDto.response) {
                            Toast.makeText(context, "삭제 완료!", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(context, "댓글 삭제 불가능!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "댓글 삭제 응답이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseDiaryAnswerDTO>, t: Throwable) {
                    Toast.makeText(context, "댓글 삭제 등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }
}