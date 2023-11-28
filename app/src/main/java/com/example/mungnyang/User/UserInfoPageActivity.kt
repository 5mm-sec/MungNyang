package com.example.mungnyang.User

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mungnyang.R
import com.example.mungnyang.User.UserDTO.ResponseAccountDTO
import com.example.mungnyang.User.UserDTO.ResponseUpdateAccountDTO
import com.example.mungnyang.User.UserDTO.UpdateAccountDTO
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.ActivityUserinfopageBinding
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

class UserInfoPageActivity : AppCompatActivity(){


    private lateinit var binding: ActivityUserinfopageBinding


    private val PERMISSION_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 201
    private var currentPhotoPath: String? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private var URL = ""
    private lateinit var petImage: ImageView

    private var email : String =""
    private var imageUrl : String = ""
    private var userName : String = ""
    private var userNickname : String = ""
    private var userPhone : String = ""
    private var userPW : String = ""

    private var updateEmail : String =""
    private var updateImageUrl : String = ""
    private var updateUserName : String = ""
    private var updateUserNickname : String = ""
    private var updateUserPhone : String = ""
    private var updateUserPW : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserinfopageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        petImage = binding.userImage

        val userEmail = intent.getStringExtra("userEmail")
        findUserData(userEmail)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )

        binding.backImagebutton.setOnClickListener {
            finish()
        }

        binding.cameraButton.setOnClickListener {
            Log.d("카메라 버튼 눌림","카메라 버튼 눌림")
            checkCameraPermission()
        }

        binding.savetextview.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("수정")
            builder.setMessage("수정 하시겠습니까?")

            // 예 버튼 처리
            builder.setPositiveButton("예") { dialog, which ->
                updateUserData(userEmail)
            }

            // 아니오 버튼 처리
            builder.setNegativeButton("아니오") { dialog, which ->
                dialog.dismiss()
            }

            // 다이얼로그를 표시
            val dialog = builder.create()
            dialog.show()

        }

    }

    private fun updateUserData(userEmail: String?) {

        updateEmail = email
        updateUserPW = userPW
        updateUserName = binding.usernameedittext.text.toString()
        updateUserPhone = binding.userphoneedit.text.toString()
        updateUserNickname = binding.nicknameedittext.text.toString()

        updateImageUrl = if (URL.isNullOrEmpty()){
            imageUrl
        } else{
            URL
        }

        val retrofit = RetrofitManager.instance

        val updateAccountDTO = UpdateAccountDTO(userEmail,
            updateUserPW,
            updateUserName,
            updateUserPhone,
            updateUserNickname,
            updateImageUrl
           )

        Log.d("API BEFORE","hi $updateAccountDTO")

        val sendUpdate = retrofit.apiService.updateUser(updateAccountDTO);
        sendUpdate.enqueue(object : Callback<ResponseUpdateAccountDTO> {

            override fun onResponse(call: Call<ResponseUpdateAccountDTO>, response: Response<ResponseUpdateAccountDTO>) {

                val responseDto = response.body()
                Log.d(ContentValues.TAG, "Response received: $responseDto");
                if (responseDto != null) {
                    Log.d(ContentValues.TAG, "Response received: $responseDto");

                    if (responseDto.response) {
                        Toast.makeText(this@UserInfoPageActivity, "수정 완료!", Toast.LENGTH_SHORT).show()
                        //findUserData(userEmail)
                        val data = Intent()
                        setResult(RESULT_OK, data)
                        finish()
                    } else {
                        Toast.makeText(this@UserInfoPageActivity, "수정 불가능!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this@UserInfoPageActivity, "응답이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseUpdateAccountDTO>, t: Throwable) {
                Toast.makeText(this@UserInfoPageActivity, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                    .show();
            }
        })

    }

    private fun findUserData(userEmail: String?) {

        val defaultImage = R.drawable.defaultuserimage

        if (userEmail != null) {
            val retrofit = RetrofitManager.instance

            val sendUserSearch = retrofit.apiService.findUser(userEmail)
            sendUserSearch.enqueue(object : Callback<ResponseAccountDTO> {
                override fun onResponse(
                    call: Call<ResponseAccountDTO>,
                    response: Response<ResponseAccountDTO>
                ) {
                    val responseDto = response.body()
                    if (responseDto != null) {
                        val userList = responseDto.userList

                        if (userList.isNotEmpty()) {
                            for (user in userList) {

                                email = user.email.toString()
                                userName = user.userName.toString()
                                userNickname = user.nickName.toString()
                                userPhone = user.phone.toString()
                                imageUrl = user.imageUrl.toString()
                                userPW = user.password.toString()

                                Log.d(ContentValues.TAG, "User : $user")

                                if(userEmail == email){

                                    Glide.with(this@UserInfoPageActivity)
                                        .load(imageUrl)
                                        .apply(RequestOptions.circleCropTransform())
                                        .placeholder(defaultImage)
                                        .error(defaultImage)
                                        .fallback(defaultImage)
                                        .into(petImage)

                                    binding.useremailtext.text = email
                                    binding.usernameedittext.setText(userName)
                                    binding.userphoneedit.setText(userPhone)
                                    binding.nicknameedittext.setText(userNickname)
                                }
                            }
                        } else {
                            Log.d(ContentValues.TAG, "No users found for the provided email")
                        }
                    } else {
                        Log.d(ContentValues.TAG, "Search Response is null")
                    }
                }

                override fun onFailure(call: Call<ResponseAccountDTO>, t: Throwable) {
                    Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
                }
            })
        }
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
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // 사진 촬영이 완료되면 Firebase Storage에 업로드
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // Firebase Storage에 이미지 업로드

        val file = Uri.fromFile(File(currentPhotoPath))
        val imagesRef: StorageReference = storageRef.child("userImage/${file.lastPathSegment}")
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
                    .override(110, 110)
                    .centerCrop() // 이미지를 중앙에 맞추고 잘라냄
                    .circleCrop() // 이미지를 원형으로 변환
                    .into(petImage)

            } else {
                // 이미지 업로드 실패 처리
                Log.e("Firebase Storage", "Image Upload Failed")
            }
        }
    }

}