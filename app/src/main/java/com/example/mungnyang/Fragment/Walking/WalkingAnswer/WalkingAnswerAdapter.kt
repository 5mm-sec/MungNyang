package com.example.mungnyang.Fragment.Walking.WalkingAnswer

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.Fragment.ShareToDiary.Answer.ResponseDiaryAnswerDTO
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ItemDiaryAnswerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalkingAnswerAdapter(private val context: Context?, private val walkingAnswerList: MutableList<WalkingAnswer>) : RecyclerView.Adapter<WalkingAnswerAdapter.AnswerWalkingViewHolder>() {

    // 호출되는 횟수가 정해졌음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerWalkingViewHolder {
        return AnswerWalkingViewHolder(
            ItemDiaryAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }


    fun deleteItem(position: Int) {
        walkingAnswerList.removeAt(position)
        notifyItemRemoved(position)
    }
    //내가 그려야 할 항목의 개수를 알려주는 일을 함 (return에서 한줄로 바꿈)
    override fun getItemCount(): Int = walkingAnswerList.size

    // 스크롤을 많이하면 할수록 많이 호출됨
    override fun onBindViewHolder(holder: AnswerWalkingViewHolder, position: Int) {
        val diaryAnswerList = walkingAnswerList[position]
        context?.let { holder.bind(it, diaryAnswerList, this) }
    }

    // ViewHolder를 만든다는거랑 View를 만든다는거랑 똑같은 말임
    class AnswerWalkingViewHolder(private val binding: ItemDiaryAnswerBinding ) :
        RecyclerView.ViewHolder(binding.root)
    {
        private var boardPetNameToDB : String = ""
        private var answerIDToDB : String = ""
        private var answerTextToDB : String = ""

        private var answerEmail : String = ""
        private var petName : String = ""
        private var answerText : String = ""
        private lateinit var context : Context
        val TAG = "DiaryAnswerAdapter"
        private val retrofit = RetrofitManager.instance

        fun bind(
            context: Context,
            walkingAnswerList: WalkingAnswer,
            walkingAnswerAdapter: WalkingAnswerAdapter
        ){
            this.context = context
            binding.userNickNameText.text = walkingAnswerList.userNickName.toString()
            binding.answerText.text = walkingAnswerList.answerText.toString()
            binding.timeText.text = walkingAnswerList.time.toString()

            Log.d("ㅌㅊㄴㅁ어ㅕㅁ놔ㅓㅣ옴지ㅓㅏ",walkingAnswerList.answerText.toString() )
            Log.d("ㅌㅊdawdwad213123ㅓㅣ옴지ㅓㅏ",walkingAnswerList.time.toString() )

            answerText = walkingAnswerList.answerText.toString()
            petName = walkingAnswerList.petName.toString()
            answerEmail = walkingAnswerList.userEmail.toString()

            val defaultImage = R.drawable.defaultuserimage;

            Glide.with(context)
                .load(walkingAnswerList.userImageURL)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(defaultImage)
                .error(defaultImage)
                .fallback(defaultImage)
                .into(binding.answerUserImage)

            searchByBoardEmail(answerEmail)

            binding.deleteAnswerBtn.setOnClickListener {
                deleteAnswer(answerIDToDB.toInt())
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    deleteAnswer(answerIDToDB.toInt())
                    walkingAnswerAdapter.deleteItem(position) // 리사이클러뷰에서 해당 항목 삭제
                }

            }

        }

        private fun searchByBoardEmail(answerEmail2: String){

            val sendWalkingAnswerSearch = retrofit.apiService.searchByWalkingBoardEmail(answerEmail2)
            sendWalkingAnswerSearch.enqueue(object : Callback<SearchResponseWalkingAnswerDTO> {
                override fun onResponse(
                    call: Call<SearchResponseWalkingAnswerDTO>,
                    response: Response<SearchResponseWalkingAnswerDTO>
                ) {
                    val responseDto = response.body()
                    if (responseDto != null) {
                        val answerList = responseDto.answerList

                        if (answerList.isNotEmpty()) {

                            for (answerList in answerList) {

                                answerTextToDB = answerList.answerText.toString()
                                boardPetNameToDB= answerList.petName.toString()

                                if (answerText == answerTextToDB && answerEmail == answerEmail2 && petName == boardPetNameToDB){
                                    answerIDToDB = answerList.walkingAnswerID.toString()
                                }

                            }
                        } else {
                            Log.d(TAG, "No users found for the provided email")
                        }
                    } else {
                        Log.d(TAG, "Search Response is null")
                    }
                }

                override fun onFailure(call: Call<SearchResponseWalkingAnswerDTO>, t: Throwable) {
                    Log.e(TAG, "Search Request Failed: ${t.message}", t)
                }
            })

        }

        private fun deleteAnswer(answerIDToDB: Int){
            val sendPetDelete = retrofit.apiService.deleteWalkingAnswer(answerIDToDB)
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