package com.example.mungnyang.Profile

import android.content.ContentValues
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.Pet.PetDTO.ResponsePetDTO
import com.example.mungnyang.Pet.PetDTO.UpdatePetActivity
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityUpdatePetBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ProfileManagementOBJ {
    val List = mutableListOf<Profile>()

    // 함수 하나 만들어서 init 됐을 때 리스트 내용 비우고 새로고침
    var mainAdapter: RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>? = null
    private val retrofit = RetrofitManager.instance

    // 펫 프로필 추가
    fun addProfile(profile: Profile){
        List.add(profile)
        List.sortBy { it.petName.uppercase() }

        for (i in List){
            Log.d("sort!!",i.toString())
        }
        mainAdapter!!.notifyDataSetChanged()
    }

    // 펫 프로필 수정
    fun updateProfile(profile: Profile) {
        val existingProfile = findProfileByPetID(profile.petID)
        if (existingProfile != null) {
            // 기존 프로필이 있는 경우 해당 프로필을 수정
            val index = List.indexOf(existingProfile)
            List[index] = profile
            mainAdapter!!.notifyDataSetChanged()
        } else {
            // 기존 프로필이 없는 경우 새로운 프로필을 추가
            addProfile(profile)
        }
    }

    // 펫 프로필 삭제
    fun deleteProfile(petID: String) {
        val profile = findProfileByPetID(petID)
        if (profile != null) {
            List.remove(profile)
        }
        mainAdapter!!.notifyDataSetChanged()
    }

    // 펫 ID로 프로필 펫 ID 검색
    private fun findProfileByPetID(petID: String): Profile? {
        return List.firstOrNull { it.petID == petID }
    }

    // 펫 ID로 프로필 이미지 검색
    fun findProfileByPetImage(petID: String): String? {
        val profile = List.firstOrNull { it.petID == petID }
        return profile?.petImage
    }

    // 사용자 이메일로 DB에 있는 펫의 정보를 받아옴
    fun refreshFrom(email : String) {
        // 사용자의 펫 정보를 받아옴.
        val sendSearch = retrofit.apiService.searchPet(email)
        sendSearch.enqueue(object : Callback<ResponsePetDTO> {
            override fun onResponse(call: Call<ResponsePetDTO>, response: Response<ResponsePetDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val petList = responseDto.petList

                    if (petList.isNotEmpty()) {
                        for (pet in petList) {
                            val petId = pet.petID.toString()
                            val petName = pet.petName.toString()
                            val petImage = pet.image.toString()
                            val accountEmail = pet.accountEmail.toString()

                            Log.d(ContentValues.TAG, "Pet : $pet")

                            // 이미 리스트에 존재하는 펫 정보인지 확인
                            val existingProfile = List.find { it.petName == petName }

                            if (existingProfile != null) {
                                // 이미 존재하는 경우 업데이트
                                existingProfile.petImage = petImage
                            } else {
                                // 존재하지 않는 경우 새로 추가
                                val profile = Profile("$petId", "$petImage", "$petName", "$accountEmail")
                                addProfile(profile)

                            }
                        }

                    } else {
                        Log.d(ContentValues.TAG, "No pets found for the provided email")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponsePetDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }

    // 펫 추가시 서버로부터 해당하는 펫의 ID를 받아옴
    fun moreAddPetRefreshFrom(email : String, addPetName: String, callback: (String) -> Unit) {
        var petID = ""

        // 사용자의 펫 정보를 받아옴.
        val sendSearch = retrofit.apiService.searchPet(email)
        sendSearch.enqueue(object : Callback<ResponsePetDTO> {
            override fun onResponse(call: Call<ResponsePetDTO>, response: Response<ResponsePetDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val petList = responseDto.petList

                    if (petList.isNotEmpty()) {
                        for (pet in petList) {
                            val petId = pet.petID.toString()
                            val petName = pet.petName.toString()

                            if (addPetName == petName){
                                petID = petId
                            }
                        }
                    } else {
                        Log.d(ContentValues.TAG, "No pets found for the provided email")
                    }
                }
                else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
                // 결과를 콜백 함수를 통해 반환
                callback(petID)
            }

            override fun onFailure(call: Call<ResponsePetDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })

    }

    // petID를 사용해서 추가한 펫의 정보를 검색한 후 UpdatePetActivity에 작성한 데이터를 출력
    fun searchPetID(
        petID: Int,
        activity: UpdatePetActivity,
        binding: ActivityUpdatePetBinding// 이 부분에 실제 액티비티 타입을 넣어야 합니다.
    ) {

        val defaultImage = R.drawable.cool_1

        // PetID를 이용해 해당하는 펫의 정보를 불러옴
        val sendSearch = retrofit.apiService.searchPetID(petID)
        sendSearch.enqueue(object : Callback<ResponsePetDTO> {
            override fun onResponse(call: Call<ResponsePetDTO>, response: Response<ResponsePetDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val petList = responseDto.petList

                    if (petList.isNotEmpty()) {
                        for (pet in petList) {
                            val petId = pet.petID.toString()
                            val imageUrl = pet.image.toString()
                            val petName = pet.petName.toString()

                            val petBirthday = pet.birthday.toString()
                            val petGender = pet.gender.toString()
                            val petType = pet.type.toString()
                            val petSpecies = pet.species.toString()
                            val petWeight = pet.weight.toString()
                            val petNeutered = pet.neutered

                            //val existingProfile = List.find { it.petID == petID.toString() }

                            binding.nameEditText.setText(petName)
                            binding.birthdayTextView.text = petBirthday
                            binding.kindedittext.setText(petSpecies)
                            binding.weightedittext.setText(petWeight)

                            // 펫의 Gender에 따라 배경색 설정
                            if (petGender == "남아") {
                                activity.isButtonClicked2 = true
                                binding.manBackground.setBackgroundResource(R.drawable.marking_background)
                                binding.womanBackground.setBackgroundResource(R.drawable.man_background_shape)
                            } else if (petGender == "여아") {
                                activity.isButtonClicked2 = false
                                binding.manBackground.setBackgroundResource(R.drawable.man_background_shape)
                                binding.womanBackground.setBackgroundResource(R.drawable.marking_background)
                            }

                            // 펫의 Type에 따라 배경색 설정
                            if (petType == "강아지") {
                                activity.isButtonClicked3 = false
                                binding.dogBackground.setBackgroundResource(R.drawable.marking_background)
                                binding.catBackhround.setBackgroundResource(R.drawable.man_background_shape)
                            } else if (petType == "고양이") {
                                activity.isButtonClicked3 = true
                                binding.dogBackground.setBackgroundResource(R.drawable.man_background_shape)
                                binding.catBackhround.setBackgroundResource(R.drawable.marking_background)
                            }

                            // 펫의 Neutered에 따라 체크박스 설정
                            if (petNeutered) {
                                binding.yesCheckBox.isChecked = true
                                binding.noCheckBox.isChecked = false
                            } else {
                                binding.yesCheckBox.isChecked = false
                                binding.noCheckBox.isChecked = true
                            }

                            Glide.with(activity)
                                .load(imageUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .placeholder(defaultImage)
                                .error(defaultImage)
                                .fallback(defaultImage)
                                .into(binding.updatePetImage)
                        }
                    } else {
                        Log.d(ContentValues.TAG, "No pets found for the provided email")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponsePetDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }

//    private fun updatePet(petID: Int?) {
//
//        updateEmail = email
//        updateUserPW = userPW
//        updateUserName = binding.usernameedittext.text.toString()
//        updateUserPhone = binding.userphoneedit.text.toString()
//        updateUserNickname = binding.nicknameedittext.text.toString()
//        updateImageUrl = URL
//
//
//        val updateAccountDTO = UpdateAccountDTO(userEmail,
//            updateUserPW,
//            updateUserName,
//            updateUserPhone,
//            updateUserNickname,
//            updateImageUrl
//        )
//
//        Log.d("API BEFORE","hi $updateAccountDTO")
//
//        val sendUpdate = retrofit.apiService.updateUser(updateAccountDTO);
//        sendUpdate.enqueue(object : Callback<ResponseUpdateAccountDTO> {
//
//            override fun onResponse(call: Call<ResponseUpdateAccountDTO>, response: Response<ResponseUpdateAccountDTO>) {
//
//                val responseDto = response.body()
//                Log.d(ContentValues.TAG, "Response received: $responseDto");
//                if (responseDto != null) {
//                    Log.d(ContentValues.TAG, "Response received: $responseDto");
//
//                    if (responseDto.response) {
//                        Toast.makeText(this@UserInfoPageActivity, "수정 완료!", Toast.LENGTH_SHORT).show()
//                        findUserData(userEmail)
//                    } else {
//                        Toast.makeText(this@UserInfoPageActivity, "수정 불가능!", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                } else {
//                    Toast.makeText(this@UserInfoPageActivity, "응답이 없습니다.", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseUpdateAccountDTO>, t: Throwable) {
//                Toast.makeText(this@UserInfoPageActivity, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
//                    .show();
//            }
//        })

//    }
}
