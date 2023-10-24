package com.example.mungnyang.User

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.mungnyang.Pet.AddPetActivity
import com.example.mungnyang.User.UserDTO.AccountDTO
import com.example.mungnyang.User.UserDTO.ResponseDTO
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private var isEmailValid = false
    private var isDuplicateEmail = false

    private val PERMISSION_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 201
    private var currentPhotoPath: String? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private var URL = ""
    private lateinit var userImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userImage = binding.userImage

        val emailEditText = binding.emailedittext
        val pwEditText = binding.signupPwedittext

        val retrofit = RetrofitManager.instance
        // 이메일 입력 값의 유효성 검사
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()
                enableNextStepButton()
            }
        })

        binding.backImagebutton2.setOnClickListener {
            finish()
        }

        binding.cameraButton.setOnClickListener {
            checkCameraPermission()
        }

        // 중복 확인 버튼 클릭 시 이벤트 처리
        binding.signupEmailcheck.setOnClickListener {

            val emailedittext = binding.emailedittext.text.toString();
            Log.d("EMAIL", "$emailedittext")
            val sendValidCheck = retrofit.apiService.emailValidCheck(emailedittext);

            sendValidCheck.enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    val responseDto = response.body();
                    Log.d("ㅇ", responseDto.toString())

                    if (responseDto != null) {
                        if (responseDto.response) {
                            Toast.makeText(this@SignupActivity, "사용 가능한 이메일 입니다!", Toast.LENGTH_SHORT).show()
                            isEmailValid = true
                        } else {
                            Toast.makeText(this@SignupActivity, "사용 불가능한 이메일 입니다!", Toast.LENGTH_SHORT).show()
                            isEmailValid = false
                        }
                    } else {
                        Toast.makeText(this@SignupActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                        isEmailValid = false
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("API TEST", "ERROR  = ${t.message}")
                }

            })

        }

        // 다음 단계 버튼 클릭 시 이벤트 처리
        binding.rectangle37.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = pwEditText.text.toString()
            val userName = binding.signupUsernameedittext.text.toString()
            val nickName = binding.signupNicknameedittext.text.toString()
            val phoneNumber = binding.signupPhoneedittext.text.toString()

            // 필수 항목 체크
            if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || nickName.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this@SignupActivity, "필수 항목을 모두 작성 해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 중복 체크 여부 확인


            if (!isEmailValid) {
                Toast.makeText(this@SignupActivity, "중복 체크를 해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerDTO = AccountDTO(email, password, nickName, phoneNumber, userName, URL)
            Log.d("API BEFORE", "hi $registerDTO")
            val sendRegister = retrofit.apiService.register(registerDTO);
            sendRegister.enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    val responseDto = response.body();
                    if (responseDto != null) {
                        if (responseDto.response) {
                            Toast.makeText(this@SignupActivity, "회원가입 되었습니다!", Toast.LENGTH_SHORT).show();
                            val intent = Intent(this@SignupActivity, AddPetActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@SignupActivity, "회원가입 불가능!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignupActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("API TEST", "ERROR  = ${t.message}")
                    Toast.makeText(this@SignupActivity, "회원가입불가능 네트워크 에러!", Toast.LENGTH_SHORT).show();
                }
            })
                //그게 아니면 여기서 회원정보 post 로 전송
                //signupWithEmailAndPassword(email, password)
        }
    }

    // 다음 단계 버튼 활성화/비활성화
    private fun enableNextStepButton() {
        val nextStepButton = binding.nextsteptextview
        nextStepButton.isEnabled = isEmailValid && isDuplicateEmail
    }

    private fun checkCameraPermission() {
        // 카메라 권한이 있는지 확인하고 없는 경우 권한 요청
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        // 카메라 앱을 열어 사진 촬영
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile: File = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.mungnyang.fileprovider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    private fun createImageFile(): File {
        // 이미지 파일을 생성하여 저장할 경로와 파일 이름 생성

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",

            storageDir
        )
        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // 사진 촬영이 완료되면 Firebase Storage에 업로드
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // Firebase Storage에 이미지 업로드

        val file = Uri.fromFile(File(currentPhotoPath))
        val imagesRef: StorageReference = storageRef.child("userImages/${file.lastPathSegment}")
        val uploadTask = imagesRef.putFile(file)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // 업로드된 이미지의 다운로드 URL 사용 가능
                // downloadUri.toString()를 활용하여 필요한 곳에서 이미지 URL을 사용할 수 있습니다.
                Log.d("Firebase Storage", "Image Upload Success. Download URL: ${downloadUri.toString()}")
                URL = downloadUri.toString()
                // 이미지를 pet_image ImageView에 표시

                Glide.with(this)
                    .load(file)
                    .override(120, 120) // 크기를 120dp로 지정
                    .centerCrop() // 이미지를 중앙에 맞추고 잘라냄
                    .circleCrop() // 이미지를 원형으로 변환
                    .into(userImage)

            } else {
                // 이미지 업로드 실패 처리
                Log.e("Firebase Storage", "Image Upload Failed")
            }
        }
    }
}