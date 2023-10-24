package com.example.mungnyang.User.Login

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mungnyang.Fragment.AfterHomeFragment
import com.example.mungnyang.MainPage.MainActivity
import com.example.mungnyang.Pet.PetDTO.ResponsePetDTO
import com.example.mungnyang.User.SignupActivity
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleApiClient: GoogleApiClient
    private val GOOGLE_SIGN_IN_REQUEST_CODE = 3674
    // SharedPreferences 키 값 상수 정의
    private val SHARED_PREFS_NAME = "MyPrefs"
    private val TOKEN_KEY = "token"


    private var accountEmail = ""
    private var petName = ""
    private var petImage = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val googleLoginButtonTextView = binding.googleloginbuttontextview
        val kakaoLoginButtonTextView = binding.kakaologinbuttontextview
        val loginButton = binding.loginbutton
        val idEditText = binding.emailedittext
        val pwEditText = binding.passwordedittext
        val signupButton = binding.signupbutton
        //binding.emailedittext.setText("test1234@daum.net")
        //binding.passwordedittext.setText("1q2w3e4r!")

        val retrofit = RetrofitManager.instance;

        // Firebase Storage 참조
        val storage = FirebaseStorage.getInstance()
        val storageReference: StorageReference = storage.reference

        // Kakao SDK 초기화
        //KakaoSdk.init(this, appKey = "2ab3e1d57402481098dc858b5a9c7beb")

        // Google 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // GoogleApiClient 생성
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        googleLoginButtonTextView.setOnClickListener {
            // 구글 로그인 버튼 클릭 이벤트 처리
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
        }

//        kakaoLoginButtonTextView.setOnClickListener {
//            // 카카오 로그인 버튼 클릭 이벤트 처리
//            UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
//                if (error != null) {
//                    // 카카오계정으로 로그인 실패
//                } else if (token != null) {
//                    // 카카오계정으로 로그인 성공
//                    handleKakaoLogin(token)
//                }
//            }
//        }

        // 로그인 버튼
        loginButton.setOnClickListener{
            val email = idEditText.text.toString()
            val password = pwEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val loginDTO = LoginDTO(email, password)

            val sendLogin = retrofit.apiService.login(loginDTO)
            sendLogin.enqueue(object : Callback<LoginResponseDTO> {

                override fun onResponse(call: Call<LoginResponseDTO>, response: Response<LoginResponseDTO>) {
                    val responseLoginDto = response.body()

                    if (responseLoginDto != null) {
                        Log.d("responseLoginDto", responseLoginDto.toString())
                        Log.d("response", responseLoginDto.success.toString())
                        // 토큰 파싱 및 로그인 성공 처리
                        if (responseLoginDto.success) {
                            // 토큰 파싱
                            val token = response.headers()["Authorization"]?.replace("Bearer ", "")
                            if (token != null) {
                                // 토큰을 SharedPreferences에 저장
                                val sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                                sharedPreferences.edit().putString(TOKEN_KEY, token).apply()

                                // 토큰을 SharedPreferences에서 가져오기
                                val savedToken = sharedPreferences.getString(TOKEN_KEY, null)

                                // 토큰이 존재한다면 로그인 성공 처리
                                if (savedToken != null) {
                                    Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                                    // MainActivity로 이동
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)

                                    intent.putExtra("accountEmail", email)

                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(this@LoginActivity, "토큰 저장 실패!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                    Log.e("API TEST", "ERROR  = ${t.message}")
                    Toast.makeText(this@LoginActivity, "로그인 불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
                }
            })



        }


        signupButton.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


    }

//    private fun handleKakaoLogin(token: OAuthToken) {
//        // 카카오 로그인 성공 처리
//        val accessToken = token.accessToken
//        // accessToken을 활용하여 로그인 처리 또는 다른 작업 수행
//    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
    }

    override fun onStop() {
        super.onStop()
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (result != null) {
                if (result.isSuccess) {
                    // 구글 로그인 성공
                    val account = result.signInAccount
                    // account 정보를 활용하여 구글 로그인 처리 또는 다른 작업 수행
                } else {
                    // 구글 로그인 실패
                    // 실패 처리 로직 작성
                }
            }
        }
    }
}