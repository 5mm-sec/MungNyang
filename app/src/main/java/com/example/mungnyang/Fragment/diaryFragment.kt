package com.example.mungnyang.Fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mungnyang.databinding.FragmentDiaryBinding
import com.example.mungnyang.DiaryPage.Schedule.BeforeScheduleActivity
import com.example.mungnyang.DiaryPage.Diary.DiaryPageActivity
import com.example.mungnyang.DiaryPage.Numerical.NumericalCheck.KGPageActivity
import com.example.mungnyang.DiaryPage.Numerical.NumericalCheck.PetbowlCheckActivity
import com.example.mungnyang.Profile.ProfileManagementOBJ
import com.example.mungnyang.R
import com.example.mungnyang.DiaryPage.Numerical.NumericalCheck.SnackCheckActivity
import com.example.mungnyang.DiaryPage.Symptom.SymptomCheckActivity
import com.example.mungnyang.DiaryPage.Numerical.NumericalCheck.TEMPPageActivity
import com.example.mungnyang.DiaryPage.Numerical.NumericalCheck.WaterCheckActivity
import com.example.mungnyang.DiaryPage.Numerical.NumericalOBJ
import com.example.mungnyang.DiaryPage.Diary.SearchDiaryOBJ
import com.example.mungnyang.DiaryPage.Diary.initSearchDayOBJ
import com.example.mungnyang.MainPage.MainActivity
import com.example.mungnyang.Friend.AddFriend.AddFriendActivity
import com.example.mungnyang.Friend.FriendListActivity
import com.example.mungnyang.User.Login.LoginActivity
import com.example.mungnyang.User.MyPageActivity
import com.example.mungnyang.User.UserRetrofit.RetrofitManager
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar

class diaryFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentDiaryBinding? = null
    val binding get() = _binding!!

    private var selectedDate: String = ""
    private var petID: String = ""

    private var searchPetID = ""
    private var searchSelectedDay = ""

    private var petKg = ""
    private var petTemperature = ""
    private var petBowl = ""
    private var petSnack = ""
    private var petWater = ""
    private var petIDInt = 0
    private var accountEmail = ""

    private lateinit var calendarView: MaterialCalendarView
    private var symptomList: ArrayList<Int>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)

        val view = binding.root

        calendarView = binding.calendarView2 as MaterialCalendarView

        accountEmail = arguments?.getString("accountEmail") ?: ""

        binding.inViewDrawer.userEmail.text = accountEmail

        val retrofit = RetrofitManager.instance


        // 초기 선택 날짜를 현재 날짜로 설정
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
        selectedDate = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
        Log.d("기본 날짜", selectedDate)

        val initCurrentDate = CalendarDay.today()
        binding.calendarView2.selectRange(initCurrentDate, initCurrentDate)

        
        // 선택된 프로필을 출력
        for(i in ProfileManagementOBJ.List){
            if (i.isClicked){
                petID = i.petID
                Log.d("프로필 확인", i.toString())
            }

        }


        // 수치 작성 데이터 material 캘린더로 초기화
        initSearchDayOBJ.initCalendarView(binding.calendarView2 as MaterialCalendarView)
        NumericalOBJ.initCalendarView(binding.calendarView2 as MaterialCalendarView)

        val petIDString = petID // petID 값을 얻는 코드
        petIDInt = petIDString?.toIntOrNull() ?: 0 // 만약 petIDString이 null이면 0으로 초기화

        // 선택한 펫이 존재하고 그 펫에 작성한 일지가 있으면 해당하는 날짜에 빨간색 점으로 찍어줌
        // 이 OBJ는 날짜를 클릭하지 않고 데이터를 띄워주기 위해 작업을 함

        if (petIDInt != null) {
            initSearchDayOBJ.searchAllDay(petIDInt)
        }

        // 캘린더 클릭 이벤트
        binding.calendarView2.setOnDateChangedListener (object : OnDateSelectedListener {
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                val selectedYear = date.year
                val selectedMonth = date.month + 1  // CalendarDay의 월은 0부터 시작하므로 1을 더해줍니다.
                val selectedDayOfMonth = date.day

                selectedDate = "$selectedYear-${String.format("%02d", selectedMonth-1)}-${String.format("%02d", selectedDayOfMonth)}"

                Log.d("선택한 날짜", selectedDate)

                // 날짜를 클릭할 때 데이터가 있으면 화면에 띄워줌
                SearchDiaryOBJ.search(selectedDate, petID, this@diaryFragment, binding )

            }
        })

        // 몸무게 페이지 이벤트
        binding.diarypageScaleImage.setOnClickListener {
            val intent = Intent(activity, KGPageActivity::class.java)
            intent.putExtra("petID", petID)
            intent.putExtra("kg_calendardata", selectedDate)
            startActivityForResult(intent, 2)
        }

        // 체온 페이지 이벤트
        binding.diarypageTemperatureImage.setOnClickListener {
            val intent = Intent(activity, TEMPPageActivity::class.java)
            intent.putExtra("petID", petID)
            intent.putExtra("temp_calendardata", selectedDate)
            startActivityForResult(intent, 2)
        }

        // 식사량 페이지 이벤트
        binding.diarypagePetbowlImage.setOnClickListener {
            val intent = Intent(activity, PetbowlCheckActivity::class.java)
            intent.putExtra("petID", petID)
            intent.putExtra("petbowl_calendardata", selectedDate)
            startActivityForResult(intent, 2)
        }

        // 간식량 페이지 이벤트
        binding.diarypageCannedfoodImage.setOnClickListener {
            val intent = Intent(activity, SnackCheckActivity::class.java)
            intent.putExtra("petID", petID)
            intent.putExtra("snack_calendardata", selectedDate)
            startActivityForResult(intent, 2)
        }

        // 음수량 페이지 이벤트
        binding.diarypageWaterImage.setOnClickListener {
            val intent = Intent(activity, WaterCheckActivity::class.java)
            intent.putExtra("petID", petID)
            intent.putExtra("water_calendardata", selectedDate)
            startActivityForResult(intent, 2)
        }

        // 수치 작성 후 저장 버튼 클릭시 이벤트 처리
        binding.diarypageSaveData.setOnClickListener{
            NumericalOBJ.addNumerical(petID.toInt(), selectedDate, petKg, petTemperature, petBowl, petSnack, petWater, this, binding)
        }

        // 이상 증상 작성 페이지 클릭시 이벤트 처리
        binding.dangerousArrowupbtn.setOnClickListener {
            val intent = Intent(activity, SymptomCheckActivity::class.java)
            intent.putExtra("petID", petID)
            intent.putExtra("symptom_calendardata", selectedDate)
            startActivityForResult(intent, 2)
        }

        // 일정 작성 페이지 클릭시 이벤트 처리
        binding.notificationArrowupbtn.setOnClickListener {
            val intent = Intent(activity, BeforeScheduleActivity::class.java)
            intent.putExtra("petID", petID)
            intent.putExtra("notification_calendardata", selectedDate)
            startActivityForResult(intent, 2)
        }
        
        // 일지 작성 페이지 클릭시 이벤트 처리
        binding.diaryArrowupbtn.setOnClickListener {
            val intent = Intent(activity, DiaryPageActivity::class.java)
            intent.putExtra("petID", petID)
            intent.putExtra("diary_calendardata", selectedDate)
            intent.putExtra("userEmail", accountEmail)

            startActivityForResult(intent, 2)
        }

        binding.numericalDelete.setOnClickListener {
            DeleteDiaryToSelectedDayOBJ.numericalDelete(selectedDate, petIDInt, this@diaryFragment, binding, calendarView)
        }

        binding.symptomDelete.setOnClickListener {
            DeleteDiaryToSelectedDayOBJ.symptomDelete(selectedDate, petIDInt, this@diaryFragment, binding, calendarView)
        }

        binding.scheduleDelete.setOnClickListener {
            DeleteDiaryToSelectedDayOBJ.scheduleDelete(selectedDate, petIDInt, this@diaryFragment, binding, calendarView)
        }

        binding.diaryDelete.setOnClickListener{
            DeleteDiaryToSelectedDayOBJ.diaryDelete(selectedDate, petIDInt, this@diaryFragment, binding, calendarView)
        }

        // drawer 잠금
        binding.dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // 열기 버튼
        binding.diarypageMenubtn.setOnClickListener {
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

        // MainActivity에서 선언한 bnv_main을 참조
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

        binding.inViewDrawer.friendListSetting.setOnClickListener {
            val friendIntent  = Intent(requireContext(), FriendListActivity::class.java)
            friendIntent.putExtra("userEmail", accountEmail)
            startActivity(friendIntent)

        }

        binding.inViewDrawer.userSetting.setOnClickListener {
            val intent = Intent(requireContext(), MyPageActivity::class.java)
            intent.putExtra("userEmail", accountEmail)
            startActivity(intent)

        }

        // 야매 로그아웃
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

        return view
    }

    // 로그인 액티비티로 이동
    private fun navigateToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the result is from the correct request and if the data is not null
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            
            // 수치 작성 페이지에서 받아온 데이터로 이벤트 처리
            
            val kgResult = data.getStringExtra("kg_result") 
            val tempResult = data.getStringExtra("temp_result")
            val petBowlResult = data.getStringExtra("petbowl_result")
            val snackResult = data.getStringExtra("snack_result")
            val waterResult = data.getStringExtra("water_result")


            if (kgResult != null) {
                binding.diarypageKgResult.text = kgResult
                binding.diarypageScaleImage.setImageResource(R.drawable.scale_click)
                petKg = kgResult
                Log.d("DB에 넣을 kg 값 :", petKg)
            }

            if (tempResult != null) {
                binding.diarypageTempResult.text = tempResult
                binding.diarypageTemperatureImage.setImageResource(R.drawable.temperature_click)
                petTemperature = tempResult
                Log.d("DB에 넣을 체온 값 :", petTemperature)

            }

            if  (petBowlResult != null) {
                binding.diarypagePetbowlResult.text = petBowlResult
                binding.diarypagePetbowlImage.setImageResource(R.drawable.petbowl_click)
                petBowl = petBowlResult
                Log.d("DB에 넣을 식사량 값 :", petBowl)
            }

            if (snackResult != null) {
                binding.diarypageCannedfoodResult.text = snackResult
                binding.diarypageCannedfoodImage.setImageResource(R.drawable.snack_click)
                petSnack = snackResult
                Log.d("DB에 넣을 간식량 값 :", petSnack)
            }

            if (waterResult != null) {
                binding.diarypageWaterResult.text = waterResult
                binding.diarypageWaterImage.setImageResource(R.drawable.water_click)
                petWater = waterResult
                Log.d("DB에 넣을 음수량 값 :", petWater)
            }


            if (data != null) {

                symptomList = data.getIntegerArrayListExtra("symptomList")
                val receivedSymptomEventDates = data.getStringArrayListExtra("eventDates")
                val receivedScheduleEventDates = data.getStringArrayListExtra("eventDates")
                val scheduleTitle = data.getStringExtra("scheduleTitle")
                val scheduleTime = data.getStringExtra("scheduleTime")
                val receivedDiaryEventDates = data.getStringArrayListExtra("eventDates")
                val diaryTitle = data.getStringExtra("diaryTitle")
                val diaryDetail= data.getStringExtra("diaryDetail")

                if(symptomList?.isNotEmpty() == true){
                    binding.diarypageSymptomCheckResult.text = symptomList.toString()
                }

                if(scheduleTitle != null && scheduleTime != null){
                    binding.diarypageScheduleResult.text = "일정 제목 : $scheduleTitle\n일정 시간 : $scheduleTime"

                }

                if(diaryTitle != null && diaryTitle != null){
                    binding.diaryResult.text = "일지 제목 : $diaryTitle\n일지 내용 : $diaryDetail"

                }

                // 이상 증상 작성 페이지에서 받아온 데이터로 일지가 작성되어 DB에 저장되면 해당 날짜에 빨간색 점을 찍어줌
                val symptomEventDates: List<CalendarDay> = receivedSymptomEventDates?.mapNotNull { dateString ->
                    try {
                        val date = dateString.substringAfter("CalendarDay{").substringBefore("}").trim()
                        val dateParts = date.split("-")
                        if (dateParts.size == 3) {
                            val year = dateParts[0].toInt()
                            val month = dateParts[1].toInt()
                            val day = dateParts[2].toInt()
                            CalendarDay.from(year, month, day)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                calendarView.addDecorators(EventDecorator(Color.RED, symptomEventDates))

                // 일정 페이지에서 받아온 데이터로 일지가 작성되어 DB에 저장되면 해당 날짜에 빨간색 점을 찍어줌
                val scheduleEventDates: List<CalendarDay> = receivedScheduleEventDates?.mapNotNull { dateString ->
                    try {
                        val date = dateString.substringAfter("CalendarDay{").substringBefore("}").trim()
                        val dateParts = date.split("-")
                        if (dateParts.size == 3) {
                            val year = dateParts[0].toInt()
                            val month = dateParts[1].toInt()
                            val day = dateParts[2].toInt()
                            CalendarDay.from(year, month, day)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                calendarView.addDecorators(EventDecorator(Color.RED, scheduleEventDates))

                // 일지 페이지에서 받아온 데이터로 일지가 작성되어 DB에 저장되면 해당 날짜에 빨간색 점을 찍어줌
                val diaryEventDates: List<CalendarDay> = receivedDiaryEventDates?.mapNotNull { dateString ->
                    try {
                        val date = dateString.substringAfter("CalendarDay{").substringBefore("}").trim()
                        val dateParts = date.split("-")
                        if (dateParts.size == 3) {
                            val year = dateParts[0].toInt()
                            val month = dateParts[1].toInt()
                            val day = dateParts[2].toInt()
                            CalendarDay.from(year, month, day)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                calendarView.addDecorators(EventDecorator(Color.RED, diaryEventDates))

            }else {
                binding.diarypageSymptomCheckResult.text = ""
                binding.diarypageScheduleResult.text = ""
                binding.diaryResult.text = ""
                Log.d("데이터 오류", "데이터 없음")
            }



        }
    }


}