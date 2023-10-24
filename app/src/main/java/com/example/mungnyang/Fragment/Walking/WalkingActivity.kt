package com.example.mungnyang.Fragment.Walking

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.mungnyang.Fragment.ShareToWalking.ResponseAddWalkingDTO
import com.example.mungnyang.R
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityWalkingBinding
import com.google.firebase.storage.FirebaseStorage
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.graphics.gl.GLSurfaceView
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

import com.kakao.vectormap.shape.MapPoints
import com.kakao.vectormap.shape.Polyline
import com.kakao.vectormap.shape.PolylineOptions
import com.kakao.vectormap.shape.PolylineStyles
import com.kakao.vectormap.shape.PolylineStylesSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date


class WalkingActivity : AppCompatActivity() {

    private var kakaoMap: KakaoMap? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var mylabelLayer: LabelLayer? = null
    private var myPolylineLayer: Polyline? = null
    private var startlabelLayer: LabelLayer? = null
    private var endlabelLayer: LabelLayer? = null
    private var hospitallabelLayer: LabelLayer? = null
    private lateinit var binding: ActivityWalkingBinding
    private lateinit var petImageView: ImageView
    private lateinit var walkingActivityTime: TextView
    private val handler = Handler()
    private var seconds = 0
    private var isRunning = false
    private var mylabel: Label? = null // 현재 위치를 나타내는 라벨을 저장하는 변수
    private var kcalCounter = 0
    private var totalDistanceInMeters = 0.0 // 총 이동 거리 (미터)
    private var lastLocation: Location? = null // 이전 위치를 저장하기 위한 변수
    private lateinit var distanceTextView: TextView // walking_activity_distance TextView
    private var timerElapsedSeconds = 0
    private var currentPhotoPath: String? = null
    private var petKcalCounter = 0
    private var timerElapsedSeconds2 = 0
    private val otherLocations = mutableListOf<Location>() // 다른 위치들의 목록을 저장할 리스트
    // FirebaseStorage 인스턴스 가져오기
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var startTime: Long = 0 // 액티비티 시작 시간을 저장할 변수
    private var endTime: Long = 0 // 종료시간을 저장할 변수
    private var polyline: Polyline? = null
    private val imageURL: String? = null
    private val polylineOptionsList =
        mutableListOf<PolylineOptions>() // 사용자가 지나간 위치에 대한 PolylineOptions 리스트

    private lateinit var currentDate: String
    private lateinit var formattedStartTime: String
    private lateinit var formattedEndTime: String
    private lateinit var formattedelapsedTimeMillis: String
    private lateinit var petkcal: String
    private lateinit var mykcal: String
    private lateinit var distance: String
    private var FinalelapsedTimeMillis: Long = 0
    private var elapsedTimeMillis: Int = 0

    private var GoalTime: String? = null
    private var GoalDistance: String? = null
    private var GoalKcal: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWalkingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        petImageView = binding.petView
        walkingActivityTime = binding.walkingActivityTime
        // Intent에서 "petname" 정보 추출
        val petname = intent.getStringExtra("petname")
        val petID = intent.getStringExtra("petid")?.toInt()
        val accountEmail = intent.getStringExtra("accountEmail")
        val petimageURL = intent.getStringExtra("petimageURL")

        GoalTime = intent.getStringExtra("GoalTime")
        Log.d("골타임 수신", GoalTime.toString())
        GoalDistance = intent.getStringExtra("GoalDistance")
        GoalKcal = intent.getStringExtra("GoalKcal")


        distanceTextView = findViewById(R.id.walking_activity_distance)
        startStopwatch()

        val retrofit = RetrofitManager.instance

        requestLocationUpdates()
        startTime = System.currentTimeMillis()

        if (petimageURL != null) {
            Log.d("WalkingActivity", petimageURL)
        }

        if (petname != null) {
            Log.d("WalkingActivity", petname)
        }

        if (petimageURL != null) {
            Glide.with(this)
                .load(petimageURL)
                .override(110, 110) // 이미지 크기를 120dp x 100dp로 조절
                .circleCrop()
                .into(petImageView)
        }


        val mapView = binding.walkingMapView
        if (mapView != null) {
            mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                }

                override fun onMapError(error: Exception?) {
                }
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {

                    this@WalkingActivity.kakaoMap = kakaoMap



                    // 위치 권한을 확인하고 위치 업데이트를 요청
                    if (checkLocationPermission()) {
                        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                        val location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            createLabelForCurrentLocationStartPoint(location)
                            createLabelForCurrentLocation(location)

                        }
                        if (location != null) {

                        }
                        requestLocationUpdates()
                    }
                }
            })
        }

        val walkingActivityStopButton = binding.walkingActivityStopButton

        walkingActivityStopButton.setOnClickListener {
            pauseStopwatchAndKcalCounters()
            endTime = System.currentTimeMillis()
            elapsedTimeMillis = (endTime - startTime).toInt()

            FinalelapsedTimeMillis = elapsedTimeMillis.toLong()

            formattedStartTime = formatTime(startTime)
            formattedEndTime = formatTime(endTime)

            formattedelapsedTimeMillis = formatTime(FinalelapsedTimeMillis)


            mykcal = binding.walkingActivityMykcal.text.toString()
            petkcal = binding.walkingActivityPetkcal.text.toString()
            distance = binding.walkingActivityDistance.text.toString()

            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                createLabelForCurrentLocationEndPoint(location)

            }

            if (mapView == null) {
                Toast.makeText(applicationContext, "지도가 준비되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // MapCapture.capture를 사용하여 지도 캡처를 수행합니다.
            MapCapture.capture(
                this,
                mapView.surfaceView as GLSurfaceView,
                object : MapCapture.OnCaptureListener {
                    override fun onCaptured(
                        isSucceed: Boolean,
                        fileName: String,
                        imageUrl: String
                    ) {
                        if (isSucceed) {
                            // 캡쳐가 완료되면 이미지를 업로드하고 Firebase Storage에 저장합니다.
                            val bitmap =
                                BitmapFactory.decodeFile(fileName) // 캡처된 이미지 파일을 Bitmap으로 읽음
                            currentPhotoPath = imageUrl
                            Log.d("파이어베이스에 업로드한 이미지url", currentPhotoPath!!)
                        } else {
                            Toast.makeText(applicationContext, "캡쳐에 실패하였습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
        }


        val saveButton = binding.walkingActivitySavebutton
        saveButton.setOnClickListener {

            // 현재 날짜를 가져오고 "yyyy-MM-dd" 형식으로 포맷
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = dateFormat.format(Date())
            val FinalwalkingActivityTime = walkingActivityTime.text
            Log.d("산책시작시간", formattedStartTime.toString())
            Log.d("산책종료시간",formattedEndTime.toString())
            Log.d("산책 날짜", currentDate)


            val addWalkingDTO = WalkingDTO(
                petID,
                accountEmail,
                currentDate,
                formattedStartTime,
                formattedEndTime,
                FinalwalkingActivityTime,
                petkcal,
                mykcal,
                distance,
                currentPhotoPath)

            Log.d("API BEFORE","hi $addWalkingDTO")
            val sendAdd = retrofit.apiService.addWalking(addWalkingDTO)

            sendAdd.enqueue(object : Callback<ResponseAddWalkingDTO> {

                override fun onResponse(call: Call<ResponseAddWalkingDTO>, response: Response<ResponseAddWalkingDTO>) {

                    val responseDto = response.body()
                    Log.d(ContentValues.TAG, "Response received: $responseDto");
                    if (responseDto != null) {
                        Log.d(ContentValues.TAG, "Response received: $responseDto");

                        if (responseDto.response) {
                            Toast.makeText(this@WalkingActivity, "등록 완료!", Toast.LENGTH_SHORT).show()


                        } else {
                            Toast.makeText(this@WalkingActivity, "등록 불가능!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this@WalkingActivity, "응답이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseAddWalkingDTO>, t: Throwable) {
                    Toast.makeText(this@WalkingActivity, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                        .show();
                }
            })

        }
        binding.homeButton.setOnClickListener{

        }



    }

    // walkingActivityTime을 파싱하는 함수
    private fun parseWalkingTimeToSeconds(walkingTime: String): Int {
        val timeParts = walkingTime.split(":")
        val minutes = timeParts[0].toIntOrNull() ?: 0
        val seconds = timeParts[1].toIntOrNull() ?: 0
        return minutes * 60 + seconds
    }

    private fun pauseStopwatchAndKcalCounters() {
        isRunning = false
        handler.removeCallbacks(stopwatchRunnable) // 타이머 중지

        // kcalCounter 증가 중지
        handler.removeCallbacksAndMessages(null)

        // petKcalCounter 증가 중지
        handler.removeCallbacksAndMessages(null)
    }

    private val stopwatchRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                seconds++
                val minutes = seconds / 60
                val secondsDisplay = seconds % 60
                val timeText = String.format("%02d:%02d", minutes, secondsDisplay)
                walkingActivityTime.text = timeText

                // GoalTime 문자열을 Int로 변환

                val goalTimeInMinutes = GoalTime?.toIntOrNull() ?: 0

                // walkingActivityTime을 파싱하여 초로 변환
                val walkingTimeInSeconds = parseWalkingTimeToSeconds(timeText)

                if (goalTimeInMinutes != null) {
                    if (walkingTimeInSeconds == goalTimeInMinutes * 60) {
                        // AlertDialog를 생성하여 표시
                        val alertDialog = AlertDialog.Builder(this@WalkingActivity)
                        alertDialog.setTitle("목표 시간 달성")
                        alertDialog.setMessage("목표 시간을 달성했습니다!")
                        alertDialog.setPositiveButton("확인") { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                    }
                }
            }
            handler.postDelayed(this, 1000) // 1초마다 실행
        }
    }


    // 타이머 시작 함수 수정
    private fun startStopwatch() {
        isRunning = true
        handler.postDelayed(stopwatchRunnable, 1000) // 1초마다 실행

        // 18초마다 kcalCounter를 증가시키고 walking_activity_mykcal TextView에 업데이트
        val kcalUpdateInterval = 18 // 18초마다 업데이트

        handler.postDelayed(object : Runnable {
            override fun run() {
                timerElapsedSeconds += 18
                kcalCounter++
                val kcalText = kcalCounter.toString()
                findViewById<TextView>(R.id.walking_activity_mykcal).text = kcalText

                handler.postDelayed(this, kcalUpdateInterval * 1000.toLong())
            }
        }, kcalUpdateInterval * 1000.toLong())

        // 12.5초마다 petKcalCounter를 증가시키고 walking_activity_petkcal TextView에 업데이트
        val petKcalUpdateInterval = 12.5f // 12.5초마다 업데이트

        handler.postDelayed(object : Runnable {
            override fun run() {
                timerElapsedSeconds2 += 12.5.toInt()
                petKcalCounter++
                val petKcalText = petKcalCounter.toString()
                findViewById<TextView>(R.id.walking_activity_petkcal).text = petKcalText

                if (petKcalText == GoalKcal.toString()) {
                    // kcalText와 GoalKcal 값이 일치하면 AlertDialog 표시
                    val alertDialogBuilder = AlertDialog.Builder(this@WalkingActivity)
                    alertDialogBuilder.setTitle("목표 멍냥 칼로리 달성")
                    alertDialogBuilder.setMessage("목표 멍냥 칼로리를 달성했습니다!")
                    alertDialogBuilder.setPositiveButton("확인") { dialog, which ->
                        // 사용자가 확인 버튼을 클릭한 경우 수행할 동작 추가
                    }
                    alertDialogBuilder.show()
                }

                handler.postDelayed(this, (petKcalUpdateInterval * 1000).toLong())
            }
        }, (petKcalUpdateInterval * 1000).toLong())
    }

    // 타이머 일시 정지 함수 수정
    private fun pauseStopwatch() {
        isRunning = false
        handler.removeCallbacks(stopwatchRunnable) // 타이머 중지
    }

    // 타이머 재개 함수 수정
    private fun resumeStopwatch() {
        isRunning = true
        handler.postDelayed(stopwatchRunnable, 1000) // 1초마다 실행
    }


    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = PackageManager.PERMISSION_GRANTED
        return ActivityCompat.checkSelfPermission(this, permission) == granted
    }

    private fun requestLocationUpdates() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        Log.d("requestLocationUpdates", "requestLocationUpdates 실행")
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // 현재 위치를 다른 위치 목록에 추가
                otherLocations.add(location)
                // 위치가 변경될 때 호출되는 메서드
                createLabelForCurrentLocation(location)
                // 이전 위치가 null이 아니면 거리 계산
                if (lastLocation != null) {
                    val distanceInMeters = location.distanceTo(lastLocation!!) // 이전 위치와의 거리 (미터)
                    totalDistanceInMeters += distanceInMeters
                }

                // 현재 위치를 이전 위치로 설정
                lastLocation = location

                // 이동 거리를 TextView에 업데이트
                updateDistanceTextView()
                addPolylineForCurrentLocation(location,otherLocations)


            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // 위치 제공자의 상태가 변경될 때 호출되는 메서드
            }

            override fun onProviderEnabled(provider: String) {
                // 위치 제공자가 활성화될 때 호출되는 메서드
            }

            override fun onProviderDisabled(provider: String) {
                // 위치 제공자가 비활성화될 때 호출되는 메서드
            }
        }

        // 위치 업데이트 요청
        if (checkLocationPermission()) {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000, // 위치 업데이트 간격 (1초)
                10f, // 위치 업데이트 거리 (10미터)
                locationListener as LocationListener
            )
        }
    }


    private fun createLabelForCurrentLocation(location: Location) {
        // 라벨 스타일 생성
        val styles = LabelStyles.from(LabelStyle.from(R.drawable.mepoint))

        // 현재 위치 가져오기
        val currentLocation = LatLng.from(location.latitude, location.longitude)
        Log.d("createLabelForCurrentLocation", location.latitude.toString())
        Log.d("createLabelForCurrentLocation", location.longitude.toString())
        addPolylineForCurrentLocation(location,otherLocations)
        // 이전 mylabel이 있을 경우 삭제
        mylabel?.let { mylabelLayer?.remove(it) }

        // 라벨 옵션 생성 및 스타일 설정
        val options = LabelOptions.from(currentLocation).setStyles(styles).setClickable(true)

        // LabelLayer 가져오기 (또는 커스텀 Layer 생성)
        mylabelLayer = kakaoMap?.labelManager?.layer

        // LabelLayer 에 LabelOptions 을 넣어 Label 생성
        mylabel = mylabelLayer?.addLabel(options)

        // 현재 위치를 기준으로 지도를 이동하고 설정
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLocation)
        CameraUpdateFactory.tiltTo(Math.toRadians(30.0))
        CameraUpdateFactory.rotateTo(Math.toRadians(30.0))
        CameraUpdateFactory.zoomTo(11) // 원하는 줌 레벨로 설정
        kakaoMap?.moveCamera(cameraUpdate)


    }


    private fun createLabelForCurrentLocationStartPoint(location: Location) {
        // Start Point 라벨 스타일 생성
        val styles = LabelStyles.from(LabelStyle.from(R.drawable.startpoint))

        // 현재 위치 가져오기
        val currentLocation = LatLng.from(location.latitude, location.longitude)

        // 라벨 옵션 생성 및 스타일 설정
        val options = LabelOptions.from(currentLocation).setStyles(styles).setClickable(true)

        // LabelLayer 가져오기 (또는 커스텀 Layer 생성)
        startlabelLayer = kakaoMap?.labelManager?.layer

        // LabelLayer 에 LabelOptions 을 넣어 Label 생성
        startlabelLayer?.addLabel(options)
    }

    private fun createLabelForCurrentLocationEndPoint(location: Location) {
        // Start Point 라벨 스타일 생성
        val styles = LabelStyles.from(LabelStyle.from(R.drawable.endpoint))

        // 현재 위치 가져오기
        val currentLocation = LatLng.from(location.latitude, location.longitude)

        // 라벨 옵션 생성 및 스타일 설정
        val options = LabelOptions.from(currentLocation).setStyles(styles).setClickable(true)

        // LabelLayer 가져오기 (또는 커스텀 Layer 생성)
        endlabelLayer = kakaoMap?.labelManager?.layer

        // LabelLayer 에 LabelOptions 을 넣어 Label 생성
        endlabelLayer?.addLabel(options)
    }


    private fun updateDistanceTextView() {
        val distanceInKilometers = totalDistanceInMeters / 1000 // 미터를 킬로미터로 변환
        val kilometers = distanceInKilometers.toInt() // 킬로미터 부분
        val meters = (totalDistanceInMeters % 1000).toInt() // 미터 부분

        val distanceText = String.format("%02d.%02d", kilometers, meters)
        distanceTextView.text = distanceText

        // 로그로 이동 거리 확인
        Log.d("WalkingActivity", "이동 거리: $kilometers.$meters")
        if (kilometers == GoalDistance?.toInt()) {
            // kcalText와 GoalKcal 값이 일치하면 AlertDialog 표시
            val alertDialogBuilder = AlertDialog.Builder(this@WalkingActivity)
            alertDialogBuilder.setTitle("목표 거리 달성")
            alertDialogBuilder.setMessage("목표 거리를 달성했습니다!")
            alertDialogBuilder.setPositiveButton("확인") { dialog, which ->
                // 사용자가 확인 버튼을 클릭한 경우 수행할 동작 추가
            }
            alertDialogBuilder.show()
        }
        // Toast 메시지로 이동 거리 표시
        val toastText = "이동 거리: $kilometers km $meters m"
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
    }


    private fun addPolylineForCurrentLocation(location: Location, otherLocations: List<Location>) {
        val currentLatLng = LatLng.from(location.latitude, location.longitude)

        // LatLng 배열을 생성합니다. 현재 위치와 다른 위치들을 포함합니다.
        val latLngArray = mutableListOf(currentLatLng)
        otherLocations.forEach { otherLocation ->
            val otherLatLng = LatLng.from(otherLocation.latitude, otherLocation.longitude)
            latLngArray.add(otherLatLng)
        }

        // MapPoints를 생성합니다.
        val currentMapPoints = MapPoints.fromLatLng(*latLngArray.toTypedArray())

        // PolylineOptions를 생성합니다.
        val polylineOptions = PolylineOptions.from(currentMapPoints)

        // Polyline 스타일 설정 (선 두께, 색상 등)
        polylineOptions.setStylesSet(
            PolylineStylesSet.from(
                PolylineStyles.from(
                    5f, // 폴리라인 두께
                    Color.BLUE // 폴리라인 색상 (파란색 예시)
                )
            )
        )

        val shapeManager = kakaoMap?.shapeManager

        // Polyline을 추가하고 리스트에 저장
        this@WalkingActivity.polyline = shapeManager?.getLayer()?.addPolyline(polylineOptions)
        polyline?.let {
            polylineOptionsList.add(polylineOptions)
        }
    }

    private fun formatTime(timeInMillis: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val date = Date(timeInMillis)
        return simpleDateFormat.format(date)
    }

}




