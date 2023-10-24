package com.example.mungnyang.Profile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.Pet.PetDTO.UpdatePetActivity
import com.example.mungnyang.R
import com.example.mungnyang.databinding.ItemPetprofileBinding


class ProfileAdapter(private val context: Context, private val Profile: List<Profile>, private val pet_information: Interface) :
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    private val clickedPetNames: MutableList<String> = mutableListOf()  // 클릭한 이미지의 펫 이름을 저장하는 배열 변수

    // 호출되는 횟수가 정해졌음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(
            ItemPetprofileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    //내가 그려야 할 항목의 개수를 알려주는 일을 함 (return에서 한줄로 바꿈)
    override fun getItemCount(): Int = Profile.size

    // 스크롤을 많이하면 할수록 많이 호출됨
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = Profile[position]
        holder.bind(context, profile, pet_information, clickedPetNames)
    }

    // ViewHolder를 만든다는거랑 View를 만든다는거랑 똑같은 말임
    class ProfileViewHolder(private val binding: ItemPetprofileBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
            fun bind(context: Context,
                     profile: Profile,
                     pet_information: Interface,
                     clickedPetNames: MutableList<String>
            ){
                binding.profilePetName.text = profile.petName
                
                // 리사이클러뷰 이미지 클릭 이벤트
                binding.profilePetImage.setOnClickListener {

                    profile.isClicked = !profile.isClicked

                    Log.d("클릭", profile.isClicked.toString())
                    Log.d("클릭개수", clickedPetNames.size.toString())

                    if (clickedPetNames.size == 1 && profile.isClicked) {
                        return@setOnClickListener
                    }
                    if (profile.isClicked) {
                        binding.profilePetName.setTextColor(Color.BLUE)
                        clickedPetNames.add(profile.petName)
                    } else {
                        binding.profilePetName.setTextColor(Color.BLACK)
                        clickedPetNames.remove(profile.petName)
                    }
                    //pet_information.cal(clickedPetNames.joinToString(", "))

                    if (clickedPetNames.isNotEmpty()) {
                        pet_information.cal(clickedPetNames.joinToString(", "))
                    } else {
                        Log.d("빈 값","클릭한 이미지 없음" )
                    }

                    //pet_information.cal("${profile.petName}") // 클릭한 이미지의 펫 이름을 콤마로 구분하여 전달
                    //Toast.makeText(context,"${profile.petName}",Toast.LENGTH_SHORT).show();
                }

                // 프로필 수정 버튼 이벤트
                binding.profileModifyImage.setOnClickListener {
                    val intent = Intent(context, UpdatePetActivity::class.java)
                    intent.putExtra("petID", profile.petID)
                    intent.putExtra("accountEmail", profile.accountEmail)
                    Log.d("펫 아이디", profile.petID)
                    Log.d("이메일", profile.accountEmail)

                    context.startActivity(intent)
                }

                val defaultImage = R.drawable.cool_1;


                if (profile.isClicked) {
                    binding.profilePetName.setTextColor(Color.BLUE)
                    clickedPetNames.add(profile.petName)
                } else {
                    binding.profilePetName.setTextColor(Color.BLACK)
                    clickedPetNames.remove(profile.petName)
                }

                if (clickedPetNames.isNotEmpty()) {
                    pet_information.cal(clickedPetNames.joinToString(", "))
                }

                Glide.with(context)
                    .load(profile.petImage)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .fallback(defaultImage)
                    .into(binding.profilePetImage)

            }
    }
}