package com.example.mungnyang.Fragment

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mungnyang.Friend.AcceptFriend.ResponseWaitFriendDTO
import com.example.mungnyang.MainPage.MainActivity
import com.example.mungnyang.MainPage.MoreAddPetActivity
import com.example.mungnyang.MainPage.WeatherData
import com.example.mungnyang.Profile.Interface
import com.example.mungnyang.Profile.ProfileAdapter
import com.example.mungnyang.Profile.ProfileManagementOBJ
import com.example.mungnyang.R
import com.example.mungnyang.Friend.AddFriend.AddFriendActivity
import com.example.mungnyang.Friend.FriendListActivity
import com.example.mungnyang.Friend.ResponseFriendCountDTO
import com.example.mungnyang.User.Login.LoginActivity
import com.example.mungnyang.User.MyPageActivity
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.FragmentAfterHomeBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AfterHomeFragment : Fragment(), Interface {

    companion object {
        const val API_KEY: String = "92a02e00202d099f2ee4d662dae8b222"
        const val WEATHER_URL: String = "https://api.openweathermap.org/data/2.5/weather"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
        var userEmailTemp : String = ""
    }

    private val REQUEST_ADD_PET = 1

    private var _binding: FragmentAfterHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherIcon: ImageView

    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

    private var accountEmail = ""
    private var waitStatus = ""
    private var waitAccountEmail = ""

    private var petName = ""
    private var petImage = ""


    // 인터페이스 구현 (adapter에서 데이터 받아오기)
    private val pet_information: Interface = object : Interface {

        override fun cal(petName: String) {

            // 클릭한 펫 이름을 이용하여 필요한 동작 수행
            // 예: 해당 펫의 정보 가져오기, 데이터베이스에서 조회 등

            var tempString = "";
            for(i in ProfileManagementOBJ.List){
                if(i.isClicked){
                    tempString += i.petName+", "
                    break;
                }
            }
            var temp = 0;
            for(i in ProfileManagementOBJ.List){
                if(i.isClicked){
                    temp ++;
                }
            }
            temp --;
            if(temp >0){
                tempString =  tempString.substring(0,tempString.length-2);
                tempString +="외에 " +temp +"명의 친구  "
            }

            if(tempString != ""){
                tempString = tempString.substring(0,tempString.length-2);
                binding.text1.text = "${tempString}는 오늘 무슨일이 있었나요 ?"
            }
            else{
                binding.text1.text = tempString
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAfterHomeBinding.inflate(inflater, container, false)

        temperature  = binding.temperatureTv
        weatherState = binding.weatherTv
        weatherIcon = binding.weatherIc

        val view = binding.root

        initializeViews() // 초기화 함수 호출 (리사이클러뷰를 초기화)

        accountEmail = arguments?.getString("accountEmail") ?: ""
//        petName = arguments?.getString("petName") ?: ""
//        petImage = arguments?.getString("petImage") ?: ""
        //ProfileManagementOBJ.refreshFrom(accountEmail)

        // 프로필 리스트가 비어 있으면 갱신
        if(ProfileManagementOBJ.List.isEmpty()){
            ProfileManagementOBJ.refreshFrom(accountEmail)
        }
        findWaitFriend(accountEmail)

        Log.d("afterFramgment email : ", accountEmail)
//        Log.d("afterFramgment 펫 이름 : ", petName)
//        Log.d("afterFramgment 펫 이미지 : ", petImage)

        // 펫 추가 버튼 클릭 시 이벤트 처리 (Intent)
        binding.viewtest.setOnClickListener{
            val intent = Intent(requireContext(), MoreAddPetActivity::class.java)

            intent.putExtra("email", accountEmail)
            startActivityForResult(intent, REQUEST_ADD_PET)
        }

        binding.inViewDrawer.userEmail.text = accountEmail
        binding.inViewDrawer.userName.text
        // drawer 잠금
        binding.dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // 열기 버튼
        binding.menubtn.setOnClickListener {
            binding.dlMain.open()
//            mBinding.dlMain.openDrawer(GravityCompat.START)
//            mBinding.dlMain.openDrawer(Gravity.LEFT)
        }

        // 닫기 버튼
        binding.inViewDrawer.ivDrawerClose.setOnClickListener {
            binding.dlMain.close()
//            mBinding.dlMain.closeDrawer(GravityCompat.START)
//            mBinding.dlMain.closeDrawer(Gravity.LEFT)
        }

        val mainActivity = activity as MainActivity

        val bnv_main = mainActivity.bnv_main

        binding.inViewDrawer.homeSetting.setOnClickListener {
            Toast.makeText(activity, "홈 클릭", Toast.LENGTH_SHORT).show()

            val afterHomeFragment = AfterHomeFragment()
            // accountEmail 값을 Bundle에 추가하여 인자로 전달
            val args = Bundle()
            args.putString("accountEmail", accountEmail)
            afterHomeFragment.arguments = args

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.containers, afterHomeFragment)
            transaction.addToBackStack(null)
            transaction.commit()

            bnv_main.menu.findItem(R.id.home).isChecked = true
            bnv_main.itemIconTintList = ContextCompat.getColorStateList(requireContext(), R.color.click_home)
            bnv_main.itemTextColor = ContextCompat.getColorStateList(requireContext(), R.color.click_home)
        }

        binding.inViewDrawer.adduserSetting.setOnClickListener {

            val intent = Intent(requireContext(), AddFriendActivity::class.java)
            intent.putExtra("userEmail", accountEmail)
            startActivity(intent)
        }

        binding.inViewDrawer.userSetting.setOnClickListener {

            val intent = Intent(requireContext(), MyPageActivity::class.java)
            intent.putExtra("userEmail", accountEmail)
            startActivity(intent)

        }


        binding.inViewDrawer.logoutSetting.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("로그아웃")
            builder.setMessage("로그아웃 하시겠습니까?")

            // 예 버튼 처리
            builder.setPositiveButton("예") { dialog, which ->
                // 예 버튼을 클릭하면 로그아웃 처리를 수행
                navigateToLoginActivity()
            }

            // 아니오 버튼 처리
            builder.setNegativeButton("아니오") { dialog, which ->
                // 아니오 버튼을 클릭하면 다이얼로그를 닫음 (아무 동작 없음)
                dialog.dismiss()
            }

            // 다이얼로그를 표시
            val dialog = builder.create()
            dialog.show()

        }

        binding.inViewDrawer.friendListSetting.setOnClickListener {
            val friendIntent  = Intent(requireContext(), FriendListActivity::class.java)
            friendIntent.putExtra("userEmail", accountEmail)
            startActivity(friendIntent)

        }

        binding.userimage.setOnClickListener {

            val myPageIntent  = Intent(requireContext(), MyPageActivity::class.java)
            myPageIntent.putExtra("userEmail", accountEmail)
            startActivity(myPageIntent)

        }


        binding.waitFriendGroup.setOnClickListener {
            val friendIntent  = Intent(requireContext(), FriendListActivity::class.java)
            friendIntent.putExtra("userEmail", accountEmail)
            startActivity(friendIntent)
        }


        return view
    }

    private fun findWaitFriend(accountEmail1: String){

        val retrofit = RetrofitManager.instance

        val sendWaitFriendSearch = retrofit.apiService.searchWaitFriend(accountEmail1)
        sendWaitFriendSearch.enqueue(object : Callback<ResponseWaitFriendDTO> {
            override fun onResponse(
                call: Call<ResponseWaitFriendDTO>,
                response: Response<ResponseWaitFriendDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val waitFriendList = responseDto.waitFriendList

                    if (waitFriendList.isNotEmpty()) {

                        var waitCount = 0
                        for (friendList in waitFriendList) {
                            waitStatus = friendList.status.toString()
                            waitAccountEmail = friendList.userEmail.toString()

                            if (waitStatus == "대기") {
                                waitCount++
                            }
                        }

                        if (waitCount > 0) {
                            binding.friendCount.text = "${waitCount}명"
                        } else {
                            binding.friendCount.text = "0명"
                        }
                    } else {
                        binding.friendCount.text = "0명" // 대기 친구가 없는 경우
                        Log.d(TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseWaitFriendDTO>, t: Throwable) {
                Log.e(TAG, "Search Request Failed: ${t.message}", t)
            }
        })

    }


    // 로그인 액티비티로 이동
    private fun navigateToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌 ( Linear는 세로로 나열)
        binding.recyclerTest.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        
        // 실제로 넣어주는 역할
        binding.recyclerTest.adapter = context?.let { ProfileAdapter(it, ProfileManagementOBJ.List, pet_information) }

        // 어댑터 설정
        ProfileManagementOBJ.mainAdapter = binding.recyclerTest.adapter as ProfileAdapter?;
        
        // 날씨 정보 호출
        getWeatherInCurrentLocation()
    }

    private fun getWeatherInCurrentLocation(){

        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mLocationListener = LocationListener { p0 ->
            val params: RequestParams = RequestParams()
            params.put("lat", p0.latitude)
            params.put("lon", p0.longitude)
            params.put("appid", API_KEY)
            doNetworking(params)

        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), WEATHER_REQUEST)
            return
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
    }

    private fun doNetworking(params: RequestParams) {

        var client = AsyncHttpClient()

        client.get(WEATHER_URL, params, object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                if (!isAdded) {
                    // Fragment가 활성화되지 않았으므로 아무 작업도 하지 않고 리턴
                    return
                }

                val weatherData = WeatherData().fromJson(response)
                if (weatherData != null) {
                    updateWeather(weatherData)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                if (!isAdded) {
                    // Fragment가 활성화되지 않았으므로 아무 작업도 하지 않고 리턴
                    return
                }
            }
        })

        }

    private fun updateWeather(weather: WeatherData) {

        if (!isAdded) {
            // Fragment가 활성화되지 않았으므로 아무 작업도 하지 않고 리턴
            return
        }

        val colorString = "#615F5F" // Replace with your color value
        val colorString2 = "#A6A5A5"
        temperature.setText(weather.tempString+" ℃")
        temperature.setTextColor(Color.parseColor(colorString))

        weatherState.setText(weather.weatherType)
        weatherState.setTextColor(Color.parseColor(colorString2))

        Log.d("날씨", "온도 : ${weather.tempString}, ${weather.weatherType}")

        val resourceID = resources.getIdentifier(weather.icon, "drawable", activity?.packageName)
        weatherIcon.setImageResource(resourceID)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun cal(petName: String) {
    }
}