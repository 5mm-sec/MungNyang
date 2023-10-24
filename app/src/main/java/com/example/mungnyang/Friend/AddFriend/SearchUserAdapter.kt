package com.example.mungnyang.Friend.AddFriend

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.R
import com.example.mungnyang.User.UserDTO.ResponseAccountDTO
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ItemUserlistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchUserAdapter(var persons: ArrayList<Person>, var con: Context) :
    RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>(), Filterable {
    var TAG = "SearchUserAdapter"

    var filteredPersons = ArrayList<Person>()
    var itemFilter = ItemFilter()

    private var findUserName: String = ""
    private var findUserEmail: String = ""
    private var findUserImage: String = ""
    var userEmail: String = ""
    var filterString: String = ""

    inner class SearchUserViewHolder(private var binding: ItemUserlistBinding): RecyclerView.ViewHolder(binding.root) {
        var userImage: ImageView = binding.userImage
        var userNameText: TextView = binding.userNameText
        var userEmailText: TextView = binding.userEmailText

        init {

            binding.userImage.setOnClickListener {
                val position = adapterPosition
                val person = filteredPersons[position]
                val defaultImage = R.drawable.defaultuserimage

                val dialogView = LayoutInflater.from(con).inflate(R.layout.item_user_image, null)
                val dialogImageView = dialogView.findViewById<ImageView>(R.id.dialogUserImage)

                // 이미지 크기 조정
                val layoutParams = ViewGroup.LayoutParams(
                    con.resources.getDimensionPixelSize(R.dimen.image_width), // 300dp
                    con.resources.getDimensionPixelSize(R.dimen.image_height) // 300dp
                )

                dialogImageView.layoutParams = layoutParams

                Glide.with(con)
                    .load(person.userImageURL)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .fallback(defaultImage)
                    .into(dialogImageView)

                AlertDialog.Builder(con).apply {
                    setView(dialogView)
//                    setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
//                        Toast.makeText(con, "OK Button Click", Toast.LENGTH_SHORT).show()
//                    })
                    show()

                }
            }

            binding.userInfo.setOnClickListener {
                AlertDialog.Builder(con).apply {
                    var position = adapterPosition
                    var person = filteredPersons[position]
                    setTitle(person.name)
                    setMessage(person.userEmail)
                    setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    })
                    show()
                }
            }

            binding.addFriendBtn.setOnClickListener {
//                Log.d("입력한 이메일", filterString)
//                Log.d("유저 이메일", userEmail)
                addFriend(userEmail)
            }


        }

        fun bind(person: Person) {
            val defaultImage = R.drawable.defaultuserimage

            userNameText.text = person.name
            userEmailText.text = person.userEmail

            Glide.with(con)
                .load(person.userImageURL)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(defaultImage)
                .error(defaultImage)
                .fallback(defaultImage)
                .into(userImage)

            // 이미지 작업 (Glide 등을 사용하여 서버 이미지 불러오기)
            // Glide.with(con).load(person.imageUrl).into(userImage)
        }


    }

    init {
        filteredPersons.addAll(persons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        val binding =
            ItemUserlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val person: Person = filteredPersons[position]
        holder.bind(person)
    }

    override fun getItemCount(): Int {
        return filteredPersons.size
    }

    //-- filter
    override fun getFilter(): Filter {
        return itemFilter
    }
    
    private fun addFriend(userEmail1: String?) {

        val addFriendDTO = AddFriendDTO(
            userEmail1.toString(),
            filterString,
            "대기"
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
                            Toast.makeText(con, "요청 완료", Toast.LENGTH_SHORT)
                                .show()

                        } else {
                            Toast.makeText(con, "추가 불가능!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(con, "응답이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseAddFriendDTO>, t: Throwable) {
                    Toast.makeText(con, "추가 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    inner class ItemFilter : Filter() {
        // 사용자가 입력한 검색어인 charSequence를 받아오는 메서드
        override fun performFiltering(charSequence: CharSequence): FilterResults {

            filterString = charSequence.toString()  // 입력된 검색어를 문자열로 변환
            val results = FilterResults() // FilterResults 객체로, 필터링 결과를 저장하기 위한 변수
            Log.d(TAG, "charSequence : $charSequence")

            // 검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList: ArrayList<Person> = ArrayList<Person>()
            // 공백 제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = persons
                results.count = persons.size

                return results
                // 공백 제외 2글자 이하인 경우 -> 이름으로만 검색
            } else if (filterString.trim { it <= ' ' }.length <= 2) {
                for (person in persons) {
                    if (person.name.contains(filterString)) {
                        filteredList.add(person)
                    }
                }
                // 그 외의 경우(공백 제외 2글자 초과) -> 이름/전화번호로 검색
            } else {
                for (person in persons) {
                    if (person.name.contains(filterString) || person.userEmail.contains(filterString)) {
                        filteredList.add(person)
                    }
                }
            }
            results.values = filteredList
            results.count = filteredList.size
            
            // 검색어를 기반으로 필터링된 데이터로 사용자를 검색
            findUserData(filterString)

            return results
        }

        private fun findUserData(filterString1: String) {
            val tempPersons = ArrayList<Person>()

            if (filterString1 != userEmail) {
                val retrofit = RetrofitManager.instance

                val sendUserSearch = retrofit.apiService.findUser(filterString1)
                sendUserSearch.enqueue(object : Callback<ResponseAccountDTO> {
                    override fun onResponse(
                        call: Call<ResponseAccountDTO>,
                        response: Response<ResponseAccountDTO>
                    ) {
                        val responseDto = response.body()
                        if (responseDto != null) {
                            val userList = responseDto.userList

                            if (userList.isNotEmpty()) {
                                var idCounter = 1 // 첫 번째 매개변수용 카운터

                                for (user in userList) {
                                    findUserEmail = user.email.toString()
                                    findUserName = user.userName.toString()
                                    findUserImage = user.imageUrl.toString()

                                    tempPersons.add(Person(idCounter++, findUserName, findUserEmail, findUserImage))
                                }
                                // 데이터를 가져온 후 어댑터에 설정
                                setSearchResults(tempPersons)
                            } else {
                                Log.d(TAG, "No users found for the provided email")
                            }
                        } else {
                            Log.d(TAG, "Search Response is null")
                        }
                    }

                    override fun onFailure(call: Call<ResponseAccountDTO>, t: Throwable) {
                        Log.e(TAG, "Search Request Failed: ${t.message}", t)
                    }
                })
            }
        }

        // 이 함수를 사용하여 검색 결과를 어댑터에 설정
        fun setSearchResults(results: List<Person>) {
            filteredPersons.clear()
            filteredPersons.addAll(results)
            notifyDataSetChanged()
        }


        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            filteredPersons.clear()
            filteredPersons.addAll(filterResults.values as ArrayList<Person>)
            notifyDataSetChanged()
        }
    }


}