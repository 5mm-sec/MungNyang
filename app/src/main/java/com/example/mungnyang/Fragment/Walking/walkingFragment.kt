package com.example.mungnyang.Fragment.Walking

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mungnyang.Fragment.AfterHomeFragment
import com.example.mungnyang.Fragment.ShareToWalking.ResponseAddWalkingDTO
import com.example.mungnyang.Friend.AddFriend.AddFriendActivity
import com.example.mungnyang.Friend.FriendListActivity
import com.example.mungnyang.MainPage.MainActivity
import com.example.mungnyang.Profile.Interface
import com.example.mungnyang.Profile.ProfileAdapter
import com.example.mungnyang.Profile.ProfileManagementOBJ
import com.example.mungnyang.R
import com.example.mungnyang.User.Login.LoginActivity
import com.example.mungnyang.User.MyPageActivity
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.example.mungnyang.databinding.FragmentWalkingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Date

class walkingFragment : Fragment() ,Interface{

    private var _binding: FragmentWalkingBinding? = null
    private val binding get() = _binding!!
    private var GoalTime: String? = null
    private var GoalDistance: String? = null
    private var GoalKcal: String? = null
    private var userName: String = ""
    private var param2: String? = null
    private var accountEmail = ""

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
                binding.petname.text = "${tempString}와 함께 산책 시작 !"
            }
            else{
                binding.petname.text = tempString
            }

        }
    }



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalkingBinding.inflate(inflater, container, false)
        val view = binding.root
        initializeViews() // 초기화 함수 호출 (리사이클러뷰를 초기화)

        // 프로필 리스트가 비어 있으면 갱신
        accountEmail = arguments?.getString("accountEmail") ?: ""
        userName = arguments?.getString("userName") ?: ""

        // 사용자 이메일 로그 출력
        Log.d("AfterHomeFragment", "사용자 이메일: $accountEmail")
        //val adapter = context?.let { ProfileAdapter(it, ProfileManagementOBJ.List, pet_information, clickedPetNames) }
        if(ProfileManagementOBJ.List.isEmpty()){
            ProfileManagementOBJ.refreshFrom(accountEmail)
        }

        binding.inViewDrawer.userEmail.text = accountEmail
        binding.inViewDrawer.userName.text = userName
        // drawer 잠금
        binding.dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // 열기 버튼
        binding.walkingMenubutton.setOnClickListener {
            binding.dlMain.open()
//            mBinding.dlMain.openDrawer(GravityCompat.START)
//            mBinding.dlMain.openDrawer(Gravity.LEFT)
        }

        // 닫기 버튼
        binding.inViewDrawer.ivDrawerClose.setOnClickListener {
            binding.dlMain.close()
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

        binding.walkingStartImagebutton.setOnClickListener{
            val intent = Intent(activity, WalkingActivity::class.java)

            for(i in ProfileManagementOBJ.List){
                if(i.isClicked){
                    var petImageURL = i.test()
                    intent.putExtra("petimageURL",petImageURL)
                }
            }

            for(i in ProfileManagementOBJ.List){
                if(i.isClicked){
                    var petId = i.WalkingGetPetID()
                    intent.putExtra("petid",petId)
                }
            }

            // "petname" 정보를 Intent에 추가
            val petname = binding.petname.text.toString()
            intent.putExtra("petname", petname)
            // "AfterHomeFragment" 정보를 Intent에 추가 (선택적으로)
            intent.putExtra("fragment_info", "AfterHomeFragment")
            intent.putExtra("accountEmail",accountEmail)
            intent.putExtra("GoalTime",GoalTime)
            Log.d("골타임 전달", GoalTime.toString())
            intent.putExtra("GoalDistance",GoalDistance)
            intent.putExtra("GoalKcal",GoalKcal)
            // Intent를 사용하여 "WalkingActivity"로 이동
            startActivity(intent)
        }
        binding.switchButton.setOnCheckedChangeListener{_,isChecked->
            if(isChecked){
                binding.walkingGoalTimer.setImageResource(R.drawable.walkingtimericon)
                binding.walkingGoalDistance.setImageResource(R.drawable.walkingdistanceicon)
                binding.walkingGoalKcal.setImageResource(R.drawable.walkingkcalicon)

                // 요소들을 클릭 가능하게 설정
                binding.walkingGoalTimer.isClickable = true
                binding.walkingGoalDistance.isClickable = true
                binding.walkingGoalKcal.isClickable = true
            }
            else{
                binding.walkingGoalTimer.setImageResource(R.drawable.walkingtimerdisable)
                binding.walkingGoalDistance.setImageResource(R.drawable.walkingdistancedisable)
                binding.walkingGoalKcal.setImageResource(R.drawable.walkingkcaldisable)

                // 요소들을 클릭 불가능하게 설정
                binding.walkingGoalTimer.isClickable = false
                binding.walkingGoalDistance.isClickable = false
                binding.walkingGoalKcal.isClickable = false
            }
        }

        binding.walkingGoalTimer.setOnClickListener {
            // LayoutInflater를 사용하여 XML 레이아웃 파일을 불러옴
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.walking_timer_alertdialog, null)

            // AlertDialog.Builder 초기화
            val alertDialogBuilder = AlertDialog.Builder(context)
            // AlertDialog에 커스텀 뷰 설정
            alertDialogBuilder.setView(view)
            // AlertDialog 생성 및 표시
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            val saveButton = view.findViewById<TextView>(R.id.walking_goal_timer_addtextview)
            val numberPicker = view.findViewById<NumberPicker>(R.id.timerNumberPicker)
            numberPicker.minValue = 0
            numberPicker.maxValue = 500
            numberPicker.value = 60
            // 버튼 클릭 시 NumberPicker의 값을 변수에 저장
            saveButton.setOnClickListener {
                GoalTime = numberPicker.value.toString()
                Log.d("목표시간", GoalTime.toString())
                // 이제 "selectedValue" 변수에 NumberPicker의 값이 저장됩니다.
                alertDialog.dismiss() // AlertDialog 닫기
            }

            // Find the back_imagebutton
            val backImageButton = view.findViewById<ImageView>(R.id.back_imagebutton)

            // Set an OnClickListener for the back_imagebutton
            backImageButton.setOnClickListener {
                alertDialog.dismiss() // Dismiss the AlertDialog when the back_imagebutton is clicked
            }

        }

        binding.walkingGoalDistance.setOnClickListener{
            // LayoutInflater를 사용하여 XML 레이아웃 파일을 불러옴
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.walking_distance_dialog, null)

            // AlertDialog.Builder 초기화
            val alertDialogBuilder = AlertDialog.Builder(context)
            // AlertDialog에 커스텀 뷰 설정
            alertDialogBuilder.setView(view)
            // AlertDialog 생성 및 표시
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            val saveButton = view.findViewById<TextView>(R.id.walking_goal_distance_addtextview)
            val numberPicker = view.findViewById<NumberPicker>(R.id.distanceNumberPicker)
            numberPicker.minValue = 0
            numberPicker.maxValue = 100
            numberPicker.value = 3
            // 버튼 클릭 시 NumberPicker의 값을 변수에 저장
            saveButton.setOnClickListener {
                GoalDistance = numberPicker.value.toString()
                Log.d("목표거리", GoalDistance.toString())
                // 이제 "GoalDistance" 변수에 NumberPicker의 값이 저장됩니다.
                alertDialog.dismiss() // AlertDialog 닫기
            }
            // Find the back_imagebutton
            val backImageButton = view.findViewById<ImageView>(R.id.back_imagebutton)

            // Set an OnClickListener for the back_imagebutton
            backImageButton.setOnClickListener {
                alertDialog.dismiss() // Dismiss the AlertDialog when the back_imagebutton is clicked
            }
        }
        binding.walkingGoalKcal.setOnClickListener{
            // LayoutInflater를 사용하여 XML 레이아웃 파일을 불러옴
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.walking_kcal_dialog, null)

            // AlertDialog.Builder 초기화
            val alertDialogBuilder = AlertDialog.Builder(context)
            // AlertDialog에 커스텀 뷰 설정
            alertDialogBuilder.setView(view)
            // AlertDialog 생성 및 표시
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            val saveButton = view.findViewById<TextView>(R.id.walking_goal_kcal_addtextview)
            val numberPicker = view.findViewById<NumberPicker>(R.id.kcalNumberPicker)
            // 사용자 정의 NumberPicker 설정
            numberPicker.minValue = 0
            numberPicker.maxValue = 2000
            numberPicker.value = 100

            // 버튼 클릭 시 NumberPicker의 값을 변수에 저장
            saveButton.setOnClickListener {
                GoalKcal = numberPicker.value.toString()
                Log.d("목표칼로리", GoalKcal.toString())
                // 이제 "GoalKcal" 변수에 NumberPicker의 값이 저장됩니다.
                alertDialog.dismiss() // AlertDialog 닫기
            }
            // Find the back_imagebutton
            val backImageButton = view.findViewById<ImageView>(R.id.back_imagebutton)

            // Set an OnClickListener for the back_imagebutton
            backImageButton.setOnClickListener {
                alertDialog.dismiss() // Dismiss the AlertDialog when the back_imagebutton is clicked
            }
        }




        // Inflate the layout for this fragment
        return view
    }

    // 로그인 액티비티로 이동
    private fun navigateToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌 ( Linear는 세로로 나열)
        binding.walkingRecyclerviewArea.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        // 실제로 넣어주는 역할
        binding.walkingRecyclerviewArea.adapter = context?.let { ProfileAdapter(it, ProfileManagementOBJ.List, pet_information) }

        // 어댑터 설정
        ProfileManagementOBJ.mainAdapter = binding.walkingRecyclerviewArea.adapter as ProfileAdapter?;

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
    }

    override fun cal(petName: String) {
        TODO("Not yet implemented")
    }
}