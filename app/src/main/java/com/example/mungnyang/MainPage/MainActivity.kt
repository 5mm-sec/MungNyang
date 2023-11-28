package com.example.mungnyang.MainPage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mungnyang.Fragment.AfterHomeFragment
import com.example.mungnyang.Fragment.diaryFragment
import com.example.mungnyang.Fragment.communityFragment
import com.example.mungnyang.Fragment.Walking.walkingFragment
import com.example.mungnyang.R
import com.example.mungnyang.User.UserDTO.ResponseAccountDTO
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityMainBinding
import com.example.mungnyang.hospitalFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var bnv_main: BottomNavigationView // bnv_main을 선언

    private var accountEmail = ""
    private var userName = ""
    private var petImage = ""


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountEmail = intent.getStringExtra("accountEmail").toString()
        userName = intent.getStringExtra("userName").toString()
//        petName = intent.getStringExtra("petName").toString()
//        petImage = intent.getStringExtra("petImage").toString()

        //findUserData(accountEmail)


        Log.d("메인에서 email", accountEmail)
        Log.d("메인에서 펫 이름",  userName)
//        Log.d("메인에서 펫 이미지", petImage)

        // 카카오 Api 사용시 Sha 값 받아오는 코드
        try {
            val information =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = information.signingInfo.apkContentsSigners
            val md = MessageDigest.getInstance("SHA")
            for (signature in signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                var hashcode = String(Base64.encode(md.digest(), 0))
                Log.d("hashcode", "" + hashcode)
            }
        } catch (e: Exception) {
            Log.d("hashcode", "에러::" + e.toString())

        }

//        binding.root.setOnClickListener{view ->
//            Snackbar.make(view, "rersre", Snackbar.LENGTH_LONG).setAction("Action", null).show()
//
//        }


        bnv_main = findViewById(R.id.bottom_navigationview)

        bnv_main.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.home -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,
                            R.color.click_home
                        )
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this,
                            R.color.click_home
                        )
                        val fragment = AfterHomeFragment()

                        val bundle = Bundle()
                        bundle.putString("accountEmail", accountEmail)
                        bundle.putString("userName", userName)
//                        bundle.putString("petImage", petImage)

                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction().replace(R.id.containers, fragment).commit()

                        //homeFragment()
                        fragment
                        // Respond to navigation item 1 click
                    }
                    R.id.diary -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,
                            R.color.click_diary
                        )
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this,
                            R.color.click_diary
                        )

                        val fragment = diaryFragment()

                        val bundle = Bundle()
                        bundle.putString("accountEmail", accountEmail)
                        bundle.putString("userName", userName)
//                        bundle.putString("petName", petName)
//                        bundle.putString("petImage", petImage)

                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction().replace(R.id.containers, fragment).commit()

                        fragment
                        // Respond to navigation item 2 click
                    }
                    R.id.hospital -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,
                            R.color.click_hospital
                        )
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this,
                            R.color.click_hospital
                        )

                        val fragment = hospitalFragment()

                        val bundle = Bundle()
                        bundle.putString("accountEmail", accountEmail)
                        bundle.putString("userName", userName)
//                        bundle.putString("petName", petName)
//                        bundle.putString("petImage", petImage)

                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction().replace(R.id.containers, fragment).commit()

                        fragment

                        // Respond to navigation item 3 click
                    }
                    R.id.walking -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,
                            R.color.click_walking
                        )
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this,
                            R.color.click_walking
                        )

                        val fragment = walkingFragment()

                        val bundle = Bundle()
                        bundle.putString("accountEmail", accountEmail)
                        bundle.putString("userName", userName)
//                        bundle.putString("petName", petName)
//                        bundle.putString("petImage", petImage)

                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction().replace(R.id.containers, fragment).commit()

                        fragment

                        // Respond to navigation item 3 click
                    }
                    else -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,
                            R.color.click_notify
                        )
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this,
                            R.color.click_notify
                        )

                        val fragment = communityFragment()

                        val bundle = Bundle()
                        bundle.putString("accountEmail", accountEmail)
                        bundle.putString("userName", userName)
//                        bundle.putString("petName", petName)
//                        bundle.putString("petImage", petImage)

                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction().replace(R.id.containers, fragment).commit()

                        fragment
                    }
                }
            )
            true
        }
        bnv_main.selectedItemId = R.id.home


        //binding.button.setOnClickListener{startActivity(intent)}
    }

//    private fun findUserData(userEmail: String) {
//        val retrofit = RetrofitManager.instance
//
//        val sendUserSearch = retrofit.apiService.findUser(accountEmail)
//        sendUserSearch.enqueue(object : Callback<ResponseAccountDTO> {
//            override fun onResponse(
//                call: Call<ResponseAccountDTO>,
//                response: Response<ResponseAccountDTO>
//            ) {
//                val responseDto = response.body()
//                if (responseDto != null) {
//                    val userList = responseDto.userList
//
//                    if (userList.isNotEmpty()) {
//                        for (user in userList) {
//                            userName = user.userName.toString()
//                        }
//                    } else {
//                        Log.d(ContentValues.TAG, "No users found for the provided email")
//                    }
//                } else {
//                    Log.d(ContentValues.TAG, "Search Response is null")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseAccountDTO>, t: Throwable) {
//                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
//            }
//        })
//    }


    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containers, fragment)
            .commit()
    }
}