package com.example.mungnyang
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mungnyang.Fragment.AfterHomeFragment
import com.example.mungnyang.Friend.AddFriend.AddFriendActivity
import com.example.mungnyang.Friend.FriendListActivity
import com.example.mungnyang.Hospital.AddressInfo
import com.example.mungnyang.MainPage.MainActivity
import com.example.mungnyang.R
import com.example.mungnyang.User.Login.LoginActivity
import com.example.mungnyang.User.MyPageActivity
import com.example.mungnyang.User.UserRetrofit.KAKAO_LOCAL_API_BASE_URL
import com.example.mungnyang.User.UserRetrofit.kakaoLocalApiService
import com.example.mungnyang.databinding.FragmentHospitalBinding
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class hospitalFragment : Fragment() { // 클래스 이름 변경 (소문자 시작)
    private var param1: String? = null
    private var param2: String? = null
    private var kakaoMap: KakaoMap? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var labelLayer: LabelLayer? = null
    private var hospitallabelLayer: LabelLayer? = null

    private var userName: String = ""
    private var accountEmail = ""

    // 병원 라벨 정보를 저장할 맵
    val hospitalInfoMap = mutableMapOf<Label, AddressInfo>()

    // Retrofit 생성
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(KAKAO_LOCAL_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var _binding: FragmentHospitalBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 초기화



    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHospitalBinding.inflate(inflater, container, false) // 바인딩 초기화

        val view = binding.root // 뷰 가져오기

        accountEmail = arguments?.getString("accountEmail") ?: ""
        userName = arguments?.getString("userName") ?: ""

        val mapView = view.findViewById<MapView>(R.id.mapView)
        if (mapView != null) {
            mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    // 지도 API가 정상적으로 종료될 때 호출됨
                }

                override fun onMapError(error: Exception) {
                    // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                }


            }, object : KakaoMapReadyCallback(), KakaoMap.OnLabelClickListener {
                override fun onMapReady(kakaoMap: KakaoMap) {
                    this@hospitalFragment.kakaoMap = kakaoMap
                    // 인증 후 API가 정상적으로 실행될 때 호출됨
                    // 여기에서 주변 동물병원을 검색하고 결과를 출력
                    fetchAndDisplayCurrentLocation()
                    searchAnimalHospitals()
                    kakaoMap.setOnLabelClickListener(this)
                }

                override fun onLabelClicked(
                    kakaoMap: KakaoMap?,
                    layer: LabelLayer?,
                    label: Label?
                ) {
                    // 클릭된 Label을 사용하여 해당 동물병원 정보를 가져옵니다.
                    val hospitalInfo = hospitalInfoMap[label]

                    // 가져온 정보를 TextView에 출력
                    hospitalInfo?.let {
                        binding.hospitalPlaceTextview.text = it.placeName
                        binding.hospitalRoadaddressTextview.text = it.addressName
                        binding.hospitalCategoryTextview.text = it.categoryName
                        binding.hospitalPhonenumberTextview.text = it.phone
                        binding.hospitalInfopageurlTextview.text = it.placeURL
                    }
                }

            })

        }
        binding.switchButton.setOnCheckedChangeListener{_,isChecked->
            val currentLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if(isChecked){
                binding.hospitalSwitchtextview.text = "24시 동물병원"
                currentLocation?.let{
                    val latitude = it.latitude.toString()
                    val longitude = it.longitude.toString()
                    clearHospitalLabels()
                    searchAddress("KakaoAK e4896473155197581a61edef57dc073b", "24시 동물병원", longitude, latitude)
                }
            }else{
                currentLocation?.let {
                    binding.hospitalSwitchtextview.text = "모든 동물병원"
                    val latitude = it.latitude.toString()
                    val longitude = it.longitude.toString()
                    clearHospitalLabels()
                    searchAddress("KakaoAK e4896473155197581a61edef57dc073b", "동물병원", longitude, latitude)
                }
            }
        }

        binding.inViewDrawer.userEmail.text = accountEmail
        binding.inViewDrawer.userName.text = userName
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

            val afterHomeFragment = AfterHomeFragment()
            // accountEmail 값을 Bundle에 추가하여 인자로 전달
            val args = Bundle()
            args.putString("accountEmail", accountEmail)
            args.putString("userName", userName)
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
            intent.putExtra("userName", userName)
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


        return view
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    //병원 라벨을 삭제하는 함수
    private fun clearHospitalLabels() {
        // hospitallabelLayer에서 라벨을 개별적으로 삭제
        for (label in hospitalInfoMap.keys) {
            hospitallabelLayer?.remove(label)
        }

        // hospitalInfoMap을 비움
        hospitalInfoMap.clear()
    }


    private fun fetchAndDisplayCurrentLocation() {
        // 위치 권한 확인 및 요청
        checkLocationPermission()

        // 위치 관리자 초기화
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 위치 업데이트 리스너 초기화
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // 위치가 업데이트될 때 호출되는 콜백
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("Location", "Latitude: $latitude, Longitude: $longitude")

                // 현재 위치 정보를 사용하여 라벨 생성
                createLabelForCurrentLocation(location)

                // 위치 업데이트 리스너를 더 이상 사용하지 않으므로 제거
                locationManager?.removeUpdates(this)
            }

        }

        // 위치 업데이트 요청
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener!!
        )
    }


    override fun onResume() {
        super.onResume()
        // Check and request location permissions
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    private fun createLabelForCurrentLocation(location: Location) {
        // 라벨 스타일 생성
        val styles = LabelStyles.from(LabelStyle.from(R.drawable.mepoint))

        // 현재 위치 가져오기
        val currentLocation = LatLng.from(location.latitude, location.longitude)

        // 라벨 옵션 생성 및 스타일 설정
        val options = LabelOptions.from(currentLocation).setStyles(styles).setClickable(true)

        // LabelLayer 가져오기 (또는 커스텀 Layer 생성)
        labelLayer = kakaoMap?.getLabelManager()?.getLayer()

        // LabelLayer 에 LabelOptions 을 넣어 Label 생성
        labelLayer?.addLabel(options)

        // 현재 위치를 기준으로 지도를 이동하고 설정
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLocation)
        CameraUpdateFactory.tiltTo(Math.toRadians(30.0))
        CameraUpdateFactory.rotateTo(Math.toRadians(30.0))
        CameraUpdateFactory.zoomTo(11) // 원하는 줌 레벨로 설정
        kakaoMap?.moveCamera(cameraUpdate)
    }



    private fun searchAnimalHospitals() {
        // 위치 권한 확인 및 요청
        checkLocationPermission()

        // 위치 관리자 초기화
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 위치 업데이트 리스너 초기화
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // 위치가 업데이트될 때 호출되는 콜백
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("Location", "Latitude: $latitude, Longitude: $longitude")

                // 주변 동물병원 검색 및 결과 출력
                searchAddress("KakaoAK e4896473155197581a61edef57dc073b", "동물병원", longitude.toString(), latitude.toString())

                // 위치 업데이트 리스너를 더 이상 사용하지 않으므로 제거
                locationManager?.removeUpdates(this)
            }
        }

        // 위치 업데이트 요청
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener!!
        )
    }


    private fun searchAddress(apikey: String, keyword: String, longitude: String, latitude: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response =
                    kakaoLocalApiService.searchAddress(apikey, keyword, longitude, latitude, 20000)
                if (response.isSuccessful) {
                    val addressList = response.body()?.documents
                    addressList?.take(10)?.forEach { address ->
                        val lat = address.y
                        val lon = address.x

                        val hospitalInfo = AddressInfo(
                            addressName = address.address_name,
                            placeName = address.place_name,
                            categoryName = address.category_name, // 카테고리 정보를 여기에 추가
                            phone = address.phone, // 전화번호 정보를 여기에 추가
                            placeURL = address.place_url, // 웹페이지 URL 정보를 여기에 추가
                            latitude = address.y,
                            longitude = address.x
                        )

                        // 동물병원의 위치를 기반으로 라벨 생성 및 지도에 추가
                        createLabelForAnimalHospital(LatLng.from(lat, lon), hospitalInfo)
                    }

                    // 모든 병원 표시 후 지도의 중심 위치를 변경하여 모든 병원이 보이도록 함
                    adjustMapZoomAndCenter()
                } else {
                    Log.e("ApiError", "API call failed with code ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ApiError", "API call failed: ${e.message}")
            }
        }
    }

    private fun adjustMapZoomAndCenter() {
        val cameraUpdate = CameraUpdateFactory.zoomTo(11) // 원하는 줌 레벨로 설정
        kakaoMap?.moveCamera(cameraUpdate)
    }

    private fun createLabelForAnimalHospital(location: LatLng, hospitalInfo: AddressInfo) {
        // 라벨 스타일 생성
        val styles = LabelStyles.from(LabelStyle.from(R.drawable.hospitalicon))

        // 라벨 옵션 생성 및 스타일 설정
        val options = LabelOptions.from(location)
            .setStyles(styles)

        // LabelLayer 가져오기 (또는 커스텀 Layer 생성)
        hospitallabelLayer = kakaoMap?.getLabelManager()?.getLayer()

        // LabelLayer 에 LabelOptions 을 넣어 Label 생성
        val hospitallabel = hospitallabelLayer?.addLabel(options)

        // 동물병원 정보와 Label을 매핑하여 저장
        hospitallabel?.let {
            hospitalInfoMap[it] = hospitalInfo
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            hospitalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}

