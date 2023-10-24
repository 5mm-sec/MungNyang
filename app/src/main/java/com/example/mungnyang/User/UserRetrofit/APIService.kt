package com.example.mungnyang.User.UserRetrofit

import com.example.mungnyang.DiaryPage.Diary.DiaryDTO
import com.example.mungnyang.DiaryPage.Diary.ResponseDiaryCountDTO
import com.example.mungnyang.DiaryPage.Diary.ResponseDiaryDTO
import com.example.mungnyang.DiaryPage.Diary.SearchResponseDiaryDTO
import com.example.mungnyang.DiaryPage.Numerical.NumericalDTO
import com.example.mungnyang.DiaryPage.Numerical.ResponseNumericalDTO
import com.example.mungnyang.DiaryPage.Numerical.SearchResponseNumericalDTO
import com.example.mungnyang.DiaryPage.Schedule.ResponseScheduleDTO
import com.example.mungnyang.DiaryPage.Schedule.ScheduleDTO
import com.example.mungnyang.DiaryPage.Schedule.SearchResponseScheduleDTO
import com.example.mungnyang.DiaryPage.Symptom.ResponseSymptomDTO
import com.example.mungnyang.DiaryPage.Symptom.SearchResponseSymptomDTO
import com.example.mungnyang.DiaryPage.Symptom.SymptomDTO
import com.example.mungnyang.Fragment.ShareToDiary.Answer.DiaryBoardAnswerDTO
import com.example.mungnyang.Fragment.ShareToDiary.Answer.ResponseDiaryAnswerCountDTO
import com.example.mungnyang.Fragment.ShareToDiary.Answer.ResponseDiaryAnswerDTO
import com.example.mungnyang.Fragment.ShareToDiary.Answer.SearchResponseDiaryAnswerDTO
import com.example.mungnyang.Fragment.ShareToWalking.ResponseAddWalkingDTO
import com.example.mungnyang.Fragment.Walking.ResponseWalkingAnswerCountDTO
import com.example.mungnyang.Fragment.Walking.ResponseWalkingCountDTO
import com.example.mungnyang.Fragment.Walking.SearchResponseWalkingDTO
import com.example.mungnyang.Fragment.Walking.WalkingAnswer.SearchResponseWalkingAnswerDTO
import com.example.mungnyang.Fragment.Walking.WalkingAnswer.WalkingBoardAnswerDTO
import com.example.mungnyang.Fragment.Walking.WalkingDTO
import com.example.mungnyang.Friend.AcceptFriend.AcceptFriendDTO
import com.example.mungnyang.Friend.AddFriend.AddFriendDTO
import com.example.mungnyang.Friend.AcceptFriend.ResponseAcceptFriendDTO
import com.example.mungnyang.Friend.AddFriend.ResponseAddFriendDTO
import com.example.mungnyang.Friend.AcceptFriend.ResponseWaitFriendDTO
import com.example.mungnyang.Friend.ResponseFriendCountDTO
import com.example.mungnyang.Pet.PetDTO.PetDTO
import com.example.mungnyang.Pet.PetDTO.ResponsePetDTO
import com.example.mungnyang.Pet.PetDTO.ResponsePetDeleteDTO
import com.example.mungnyang.Pet.PetDTO.ResponseUpdatePetDTO
import com.example.mungnyang.Pet.PetDTO.UpdatePetDTO
import com.example.mungnyang.User.Login.LoginDTO
import com.example.mungnyang.User.Login.LoginResponseDTO
import com.example.mungnyang.User.UserDTO.AccountDTO
import com.example.mungnyang.User.UserDTO.ResponseAccountDTO
import com.example.mungnyang.User.UserDTO.ResponseDTO
import com.example.mungnyang.User.UserDTO.ResponseUpdateAccountDTO
import com.example.mungnyang.User.UserDTO.UpdateAccountDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface APIService {
    // 사용자 --- 회원가입
    @GET("account/emailValid")
    fun emailValidCheck(@Query("email") email:String): Call<ResponseDTO>

    @POST("account/register")
    fun register(@Body body: AccountDTO): Call<ResponseDTO>

    // 사용자 --- 정보 검색
    @GET("account/searchUser")
    fun findUser(@Query("email") email:String): Call<ResponseAccountDTO>

    // 사용자 --- 수정
    @PATCH("account/update")
    fun updateUser(@Body body: UpdateAccountDTO): Call<ResponseUpdateAccountDTO>

    // 사용자 --- 펫 탐색
    @GET("pet")
    fun searchPet(@Query("email") email: String): Call<ResponsePetDTO>

    // 사용자 --- 펫 탐색
    @GET("pet/searchPetID")
    fun searchPetID(@Query("petID") petID: Int): Call<ResponsePetDTO>

    @POST("pet")
    fun addPet(@Body body: PetDTO): Call<ResponsePetDTO>

    // 사용자 --- 펫 수정
    @PATCH("pet/update")
    fun updatePet(@Body body: UpdatePetDTO): Call<ResponseUpdatePetDTO>

    // 사용자 --- 로그인
    @POST("Login")
    fun login(@Body body: LoginDTO) : Call<LoginResponseDTO>

    // 수치 작성
    @POST("numerical")
    fun addNumerical(@Body body: NumericalDTO) : Call<ResponseNumericalDTO>

    @GET("numerical/selectedDay")
    fun searchNumerical(@Query("selectedDay") selectedDay: String): Call<SearchResponseNumericalDTO>

    @GET("numerical/searchDay")
    fun searchDay(@Query("petID") petID: Int): Call<SearchResponseNumericalDTO>

    @DELETE("numerical/deleteDay")
    fun deleteNumerical(@Query("selectedDay") selectedDay: String, @Query("petID") petID: Int) : Call<ResponseNumericalDTO>

    // 이상 증상 작성
    @POST("symptom")
    fun addSymptom(@Body body: SymptomDTO) : Call<ResponseSymptomDTO>

    @GET("symptom/selectedDay")
    fun searchSymptom(@Query("selectedDay") selectedDay: String) : Call<SearchResponseSymptomDTO>

    @GET("symptom/searchSymptomDay")
    fun searchSymptomDay(@Query("petID") petID: Int) : Call<SearchResponseSymptomDTO>

    @DELETE("symptom/deleteDay")
    fun deleteSymptom(@Query("selectedDay") selectedDay: String, @Query("petID") petID: Int) : Call<ResponseSymptomDTO>

    // 일정 작성
    @POST("schedule")
    fun addSchedule(@Body body: ScheduleDTO) : Call<ResponseScheduleDTO>

    @GET("schedule/selectedDay")
    fun searchSchedule(@Query("selectedDay") selectedDay: String) : Call<SearchResponseScheduleDTO>

    @GET("schedule/searchScheduleDay")
    fun searchScheduleDay(@Query("petID") petID: Int) : Call<SearchResponseScheduleDTO>

    @DELETE("schedule/deleteDay")
    fun deleteSchedule(@Query("selectedDay") selectedDay: String, @Query("petID") petID: Int) : Call<ResponseScheduleDTO>

    // 일지 작성
    @POST("diary")
    fun addDiary(@Body body: DiaryDTO) : Call<ResponseDiaryDTO>

    @GET("diary/selectedDay")
    fun searchDiary(@Query("selectedDay") selectedDay: String) : Call<SearchResponseDiaryDTO>

    @GET("diary/searchDiaryDay")
    fun searchDiaryDay(@Query("petID") petID: Int) : Call<SearchResponseDiaryDTO>

    @DELETE("diary/deleteDay")
    fun deleteDiary(@Query("selectedDay") selectedDay: String, @Query("petID") petID: Int) : Call<ResponseDiaryDTO>

    // 모든 데이터 삭제
    @DELETE("pet/deletePet")
    fun deletePet(@Query("petID") petID: Int): Call<ResponsePetDeleteDTO>

    @DELETE("numerical/deletePetID")
    fun deletePetToNumerical(@Query("petID") petID: Int) : Call<ResponseNumericalDTO>

    @DELETE("symptom/deletePetID")
    fun deletePetToSymptom(@Query("petID") petID: Int) : Call<ResponseSymptomDTO>

    @DELETE("schedule/deletePetID")
    fun deletePetToSchedule(@Query("petID") petID: Int) : Call<ResponseScheduleDTO>

    @DELETE("diary/deletePetID")
    fun deletePetToDiary(@Query("petID") petID: Int) : Call<ResponseDiaryDTO>
    
    // 친구 추가
    @POST("friend/add")
    fun addFriend(@Body body: AddFriendDTO) : Call<ResponseAddFriendDTO>

    // 요청 대기 검색
    @GET("friend/searchWait")
    fun searchWaitFriend(@Query("userEmail") userEmail: String) : Call<ResponseWaitFriendDTO>

    // 요청 대기 추가
    @PATCH("friend/accept")
    fun acceptFriend(@Body body: AcceptFriendDTO) : Call<ResponseAcceptFriendDTO>

    // 요청 대기 거절
    @DELETE("friend/reject")
    fun deleteFriend(@Query("userEmail") userEmail: String, @Query("friendEmail") friendEmail: String) : Call<ResponseAddFriendDTO>

    // 친구 개수 전달
    @GET("friend/friendCount")
    fun friendCount(@Query("userEmail") userEmail: String) : Call<ResponseFriendCountDTO>

    // 댓글 개수 전달
    @GET("diaryAnswer/answerCount")
    fun answerCount(@Query("userEmail") userEmail: String) : Call<ResponseDiaryAnswerCountDTO>

    // 일지 개수 전달
    @GET("diary/diaryCount")
    fun diaryCount(@Query("userEmail") userEmail: String) : Call<ResponseDiaryCountDTO>

    // 일지 공유 게시판
    @GET("diary/userEmail")
    fun shareDiary(@Query("userEmail") userEmail: String) : Call<SearchResponseDiaryDTO>

    // 일지 댓글 추가
    @POST("diaryAnswer/addDiaryAnswer")
    fun addDiaryAnswer(@Body body: DiaryBoardAnswerDTO) : Call<ResponseDiaryAnswerDTO>

    // 일지 삭제
    @DELETE("diaryAnswer/deleteAnswer")
    fun deleteAnswer(@Query("answerID") answerID: Int): Call<ResponseDiaryAnswerDTO>

    // 일지 삭제 시 모든 댓글 삭제
    @DELETE("diaryAnswer/allAnswer")
    fun deleteAllAnswer(@Query("boardEmail") boardEmail: String, @Query("boardTitle") boardTitle: String): Call<ResponseDiaryAnswerDTO>

    // 일지 댓글 가져옴
    @GET("diaryAnswer/boardEmail")
    fun searchByBoardEmail(@Query("boardEmail") boardEmail: String) : Call<SearchResponseDiaryAnswerDTO>

    // 일지 댓글 유저 이메일로 가져옴
    @GET("diaryAnswer/userEmail")
    fun searchByUserEmail(@Query("userEmail") userEmail: String) : Call<SearchResponseDiaryAnswerDTO>

    // 산책 데이터 추가
    @POST("walking/addWalking")
    fun addWalking(@Body body: WalkingDTO) : Call<ResponseAddWalkingDTO>

    // 산책 공유 게시판
    @GET("walking/userEmail")
    fun shareWalkingDiary(@Query("userEmail") userEmail: String) : Call<SearchResponseWalkingDTO>

    // 삭책 기록 개수 전달
    @GET("walking/walkingCount")
    fun walkingCount(@Query("userEmail") userEmail: String) : Call<ResponseWalkingCountDTO>

    // 산책 일지 댓글 추가
    @POST("walkingAnswer/addWalkingAnswer")
    fun addWalkingAnswer(@Body body: WalkingBoardAnswerDTO) : Call<ResponseAddWalkingDTO>

    // 산책 일지 댓글 가져옴
    @GET("walkingAnswer/boardEmail")
    fun searchByWalkingBoardEmail(@Query("boardEmail") boardEmail: String) : Call<SearchResponseWalkingAnswerDTO>
    
    // 산책 기록 개수 전달
    @GET("walkingAnswer/answerCount")
    fun walkingAnswerCount(@Query("userEmail") userEmail: String) : Call<ResponseWalkingAnswerCountDTO>

    // 산책 일지 삭제
    @DELETE("walking/deleteDay")
    fun deleteWalking(@Query("selectedDay") selectedDay: String, @Query("walkingID") walkingID: Int): Call<ResponseDiaryAnswerDTO>

    // 일지 삭제
    @DELETE("walkingAnswer/deleteAnswer")
    fun deleteWalkingAnswer(@Query("answerID") answerID: Int): Call<ResponseDiaryAnswerDTO>

    // 일지 삭제
    @DELETE("walkingAnswer/allAnswer")
    fun deleteAllWalkingAnswer(@Query("boardEmail") boardEmail: String, @Query("walkingID") walkingID: Int): Call<ResponseDiaryAnswerDTO>

}