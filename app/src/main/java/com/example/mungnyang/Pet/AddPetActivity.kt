package com.example.mungnyang.Pet

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.mungnyang.databinding.ActivityAddPetBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.mungnyang.Pet.PetDTO.PetDTO
import com.example.mungnyang.Pet.PetDTO.ResponsePetDTO
import com.example.mungnyang.R
import com.example.mungnyang.User.Login.LoginActivity
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat


class AddPetActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAddPetBinding
    private lateinit var calendar: Calendar
    private var isButtonClicked: Boolean = false
    private var isButtonClicked2: Boolean = false
    private var isButtonClicked3: Boolean = false

    val db = Firebase.firestore
    private val REQUEST_IMAGE_CAPTURE = 1

    private val storageRef = FirebaseStorage.getInstance().reference
    private var currentPhotoPath: String? = null

    private val PERMISSION_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 201

    private lateinit var petImage: ImageView
    private var downloadUri: String? = null
    private var URL = ""

    private var accountEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = RetrofitManager.instance

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )

        calendar = Calendar.getInstance()
        val manBackground = binding.manBackground
        val womanBackground = binding.womanBackground
        val dogBackground = binding.dogBackground
        val catBackground = binding.catBackhround
        val allvaccinebutton = binding.allvaccinebutton
        val kennelcorpbutton = binding.kennelcorpbutton
        val covidbutton = binding.covidbutton
        val influenzabutton = binding.influenzabutton
        val heartwormbutton = binding.heartwormbutton
        val rabiesbutton = binding.rabiesbutton
        val allselectbutton = binding.allselectbutton

        val yesCheckBox = binding.yesCheckBox
        val noCheckBox = binding.noCheckBox

        petImage = binding.petImage

        binding.backImagebutton.setOnClickListener {
            finish()
        }

        binding.selectDateButton.setOnClickListener {
            Log.d("날짜선택 버튼 클릭", "날짜선택 버튼 클릭")
            showDatePickerDialog()
        }

        binding.cameraButton.setOnClickListener {
            Log.d("카메라 버튼 눌림","카메라 버튼 눌림")
            checkCameraPermission()
        }

        // 남아, 여아, 반려견, 반려묘 이벤트 처리
        manBackground.setOnClickListener {
            if (isButtonClicked2) {
                // 버튼이 클릭된 상태일 때
                manBackground.setBackgroundResource(R.drawable.marking_background)
                womanBackground.setBackgroundResource(R.drawable.man_background_shape)
                Log.d("선택여부", "남성 클릭!")
                isButtonClicked2 = false
            } else {
                // 버튼이 클릭되지 않은 상태일 때
                manBackground.setBackgroundResource(R.drawable.man_background_shape)
                isButtonClicked2 = true
            }
        }
        womanBackground.setOnClickListener {
            if (isButtonClicked2) {
                // 버튼이 클릭된 상태일 때
                womanBackground.setBackgroundResource(R.drawable.marking_background)
                manBackground.setBackgroundResource(R.drawable.man_background_shape)

                Log.d("선택여부", "여성 클릭!")
                isButtonClicked2 = false
            } else {
                // 버튼이 클릭되지 않은 상태일 때
                womanBackground.setBackgroundResource(R.drawable.man_background_shape)
                isButtonClicked2 = true
            }
        }
        dogBackground.setOnClickListener {
            if (isButtonClicked3) {
                // 버튼이 클릭된 상태일 때
                dogBackground.setBackgroundResource(R.drawable.marking_background)
                catBackground.setBackgroundResource(R.drawable.man_background_shape)

                Log.d("선택여부", "강아지 클릭!")
                isButtonClicked3 = false
            } else {
                // 버튼이 클릭되지 않은 상태일 때
                dogBackground.setBackgroundResource(R.drawable.man_background_shape)
                isButtonClicked3 = true
            }
        }
        catBackground.setOnClickListener {
            if (isButtonClicked3) {
                // 버튼이 클릭된 상태일 때
                catBackground.setBackgroundResource(R.drawable.marking_background)
                dogBackground.setBackgroundResource(R.drawable.man_background_shape)

                Log.d("선택여부", "고양이 클릭!")
                isButtonClicked3 = false
            } else {
                // 버튼이 클릭되지 않은 상태일 때
                catBackground.setBackgroundResource(R.drawable.man_background_shape)
                isButtonClicked3 = true
            }
        }


        //예방접종 버튼 이벤트 처리
        allvaccinebutton.setOnClickListener {
            if (isButtonClicked) {
                allvaccinebutton.setBackgroundResource(R.drawable.marking_background2)
                isButtonClicked = false
            } else {
                allvaccinebutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                isButtonClicked = true
            }
        }
        kennelcorpbutton.setOnClickListener {
            if (isButtonClicked) {
                kennelcorpbutton.setBackgroundResource(R.drawable.marking_background2)
                isButtonClicked = false
            } else {
                kennelcorpbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                isButtonClicked = true
            }
        }
        covidbutton.setOnClickListener {
            if (isButtonClicked) {
                covidbutton.setBackgroundResource(R.drawable.marking_background2)
                isButtonClicked = false
            } else {
                covidbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                isButtonClicked = true
            }
        }
        influenzabutton.setOnClickListener {
            if (isButtonClicked) {
                influenzabutton.setBackgroundResource(R.drawable.marking_background2)
                isButtonClicked = false
            } else {
                influenzabutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                isButtonClicked = true
            }
        }
        heartwormbutton.setOnClickListener {
            if (isButtonClicked) {
                heartwormbutton.setBackgroundResource(R.drawable.marking_background2)
                isButtonClicked = false
            } else {
                heartwormbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                isButtonClicked = true
            }
        }
        rabiesbutton.setOnClickListener {
            if (isButtonClicked) {
                rabiesbutton.setBackgroundResource(R.drawable.marking_background2)
                isButtonClicked = false
            } else {
                rabiesbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                isButtonClicked = true
            }
        }
        allselectbutton.setOnClickListener {
            if (isButtonClicked) {
                allvaccinebutton.setBackgroundResource(R.drawable.marking_background2)
                kennelcorpbutton.setBackgroundResource(R.drawable.marking_background2)
                covidbutton.setBackgroundResource(R.drawable.marking_background2)
                influenzabutton.setBackgroundResource(R.drawable.marking_background2)
                heartwormbutton.setBackgroundResource(R.drawable.marking_background2)
                rabiesbutton.setBackgroundResource(R.drawable.marking_background2)
                allselectbutton.setBackgroundResource(R.drawable.marking_background2)
                isButtonClicked = false
            } else {
                allvaccinebutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                kennelcorpbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                covidbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                influenzabutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                heartwormbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                rabiesbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                allselectbutton.setBackgroundResource(R.drawable.allvaccinebutton_shape)
                isButtonClicked = true
            }
        }

        //중성화 여부 체크박스 이벤트 처리
        yesCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                noCheckBox.isChecked = false
            }
        }

        noCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                yesCheckBox.isChecked = false
            }
        }

        binding.savetextview.setOnClickListener {

            accountEmail = intent.getStringExtra("email")

            val petName = binding.nameEditText.text.toString()
            val birthday = binding.birthdayTextView.text.toString()
            var gender = ""
            var kind = ""
            val medicalHistory = binding.medicalHistoryEdittext.text.toString()
            val weightEditText = findViewById<EditText>(R.id.weightedittext)
            val weight = weightEditText.text.toString()

            // 예외 처리 추가
            val weightDouble: Double = try {
                weight.toDouble()
            } catch (e: NumberFormatException) {
                0.0 // 무효한 입력의 경우 기본값 설정
            }


            val vaccines = mutableListOf<String>()
            if (binding.allvaccinebutton.background.constantState == resources.getDrawable(R.drawable.marking_background2)?.constantState) {
                vaccines.add("종합백신")
            }
            if (binding.kennelcorpbutton.background.constantState == resources.getDrawable(R.drawable.marking_background2)?.constantState) {
                vaccines.add("켄넬코프")
            }
            if (binding.covidbutton.background.constantState == resources.getDrawable(R.drawable.marking_background2)?.constantState) {
                vaccines.add("코로나 장염")
            }
            if (binding.influenzabutton.background.constantState == resources.getDrawable(R.drawable.marking_background2)?.constantState) {
                vaccines.add("인플루엔자")
            }
            if (binding.heartwormbutton.background.constantState == resources.getDrawable(R.drawable.marking_background2)?.constantState) {
                vaccines.add("심장사상충")
            }
            if (binding.rabiesbutton.background.constantState == resources.getDrawable(R.drawable.marking_background2)?.constantState) {
                vaccines.add("광견병")
            }

            if (binding.dogBackground.background.constantState == resources.getDrawable(R.drawable.marking_background)?.constantState) {
                kind = "강아지"
            }
            if (binding.catBackhround.background.constantState == resources.getDrawable(R.drawable.marking_background)?.constantState) {
                kind = "고양이"
            }
            if (binding.manBackground.background.constantState == resources.getDrawable(R.drawable.marking_background)?.constantState) {
                gender = "남아"
            }
            if (binding.womanBackground.background.constantState == resources.getDrawable(R.drawable.marking_background)?.constantState) {
                gender = "여아"
            }
            val neutered = binding.yesCheckBox.isChecked

            val kindEdit = binding.kindedittext.text.toString()


            Log.d("accountEmail", accountEmail.toString())
            Log.d("petName", petName)
            Log.d("birthday", birthday)
            Log.d("gender", gender)
            Log.d("kind", kind)
            Log.d("kindEdit", kindEdit)
            Log.d("weightDouble", weight)
            Log.d("neutered", neutered.toString())
            Log.d("URL",URL)


            if (petName.isEmpty() || birthday.isEmpty() || gender.isEmpty() || kind.isEmpty() || kindEdit.isEmpty() || weight.isEmpty() || !neutered){
                Toast.makeText(this@AddPetActivity, "필수 항목을 모두 작성 해 주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                val addPetDTO = PetDTO(accountEmail.toString(),
                    petName,
                    birthday,
                    gender,
                    kind,
                    kindEdit,
                    weightDouble,
                    neutered,
                    URL )

                Log.d("API BEFORE","hi $addPetDTO")

                val sendAdd = retrofit.apiService.addPet(addPetDTO);
                sendAdd.enqueue(object : Callback<ResponsePetDTO> {

                    override fun onResponse(call: Call<ResponsePetDTO>, response: Response<ResponsePetDTO>) {

                        val responseDto = response.body()
                        Log.d(TAG, "Response received: $responseDto");
                        if (responseDto != null) {
                            Log.d(TAG, "Response received: $responseDto");

                            if (responseDto.response) {
                                Toast.makeText(this@AddPetActivity, "등록 완료! 로그인을 해주세요.", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@AddPetActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(this@AddPetActivity, "등록 불가능!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(this@AddPetActivity, "응답이 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<ResponsePetDTO>, t: Throwable) {
                        Toast.makeText(this@AddPetActivity, "등록 불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                            .show();
                    }
                })


            }
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
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // 사진 촬영이 완료되면 Firebase Storage에 업로드
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // Firebase Storage에 이미지 업로드

        val file = Uri.fromFile(File(currentPhotoPath))
        val imagesRef: StorageReference = storageRef.child("images/${file.lastPathSegment}")
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
                    .into(petImage)

            } else {
                // 이미지 업로드 실패 처리
                Log.e("Firebase Storage", "Image Upload Failed")
            }
        }
    }

    // 날짜 선택
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // 날짜 선택 시 실행되는 콜백 함수
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val formattedDate = formatDate(selectedDate.time)
                Toast.makeText(this, "선택한 날짜: $formattedDate", Toast.LENGTH_SHORT).show()
                binding.birthdayTextView.text = formattedDate // birthdayTextView에 선택한 날짜 설정
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        )
        datePickerDialog.show()
    }

    private fun formatDate(date: Date): String {
        // 원하는 날짜 형식으로 포맷팅하는 함수
        val simpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return simpleDateFormat.format(date)
    }




}

