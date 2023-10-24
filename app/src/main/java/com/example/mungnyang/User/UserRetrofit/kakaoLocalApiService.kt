package com.example.mungnyang.User.UserRetrofit

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// 카카오 로컬 API 기본 URL
const val KAKAO_LOCAL_API_BASE_URL = "https://dapi.kakao.com/"

// API 호출을 위한 Retrofit 인터페이스 정의
interface KakaoLocalApiService {
    // 키워드 기반 주소 검색 API
    @GET("/v2/local/search/keyword.json")
    suspend fun searchAddress(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int,
    ): Response<AddressSearchResponse>
}
data class AddressSearchResponse(
    val documents: List<AddressDocument>
)

data class AddressDocument(
    val address_name: String,
    val place_name: String,
    val category_name: String,
    val phone: String,
    val place_url: String,
    val x: Double,
    val y: Double
)

// Retrofit 생성
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(KAKAO_LOCAL_API_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val kakaoLocalApiService: KakaoLocalApiService = retrofit.create(KakaoLocalApiService::class.java)

