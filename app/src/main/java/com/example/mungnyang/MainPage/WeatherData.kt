package com.example.mungnyang.MainPage

import org.json.JSONException
import org.json.JSONObject

class WeatherData{

    lateinit var tempString: String
    lateinit var icon: String
    lateinit var weatherType: String
    private var weatherId: Int = 0
    private var tempInt: Int =0

    fun fromJson(jsonObject: JSONObject?): WeatherData? {
        try{
            var weatherData = WeatherData()
            weatherData.weatherId = jsonObject?.getJSONArray("weather")?.getJSONObject(0)?.getInt("id")!!
            weatherData.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
            weatherData.icon = updateWeatherIcon(weatherData.weatherId)
            val roundedTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp")-273.15).toInt()
            weatherData.tempString = roundedTemp.toString()
            weatherData.tempInt = roundedTemp
            return weatherData
        }catch (e: JSONException){
            e.printStackTrace()
            return null
        }
    }

    private fun updateWeatherIcon(condition: Int): String {
        if (condition in 200..299) {
            return "kjh_thunderstorm"
        } else if (condition in 300..499) {
            return "kjh_drizzle"
        } else if (condition in 500..599) {
            return "kjh_rain"
        } else if (condition in 600..700) {
            return "kjh_snowy"
        } else if (condition in 701..771) {
            return "kjh_fog"
        } else if (condition in 772..799) {
            return "kjh_overcast"
        } else if (condition == 800) {
            return "kjh_clear"
        } else if (condition in 801..804) {
            return "kjh_cloudy"
        } else if (condition in 900..902) {
            return "kjh_thunderstorm"
        }
        if (condition == 903) {
            return "kjh_snowy"
        }
        if (condition == 904) {
            return "kjh_clear"
        }
        return if (condition in 905..1000) {
            "kjh_thunderstorm"
        } else "kjh_close"

    }

}

