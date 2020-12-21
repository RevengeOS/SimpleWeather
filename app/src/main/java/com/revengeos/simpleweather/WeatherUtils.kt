/*
 * Copyright (C) 2020 RevengeOS
 * Copyright (C) 2020 Ethan Halsall
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.revengeos.simpleweather

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.revengeos.simpleweather.data.WeatherData
import com.revengeos.weathericons.WeatherIconsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File


class WeatherUtils(private val context: Context) {

    private val apiKey = "a9a5a8c0a12e5b11ae2fc673c8edf0c2"
    private val utils = JsonUtils()
    private val file = File(context.cacheDir, "").toString() + "cacheFile.srl"
    var done = false
    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val units = sharedPreferences.getString("unit_preference", "")

    interface getWeather {
        @GET("weather?")
        fun getCurrentWeatherData(
            @Query("lat") lat: String?,
            @Query("lon") lon: String?,
            @Query("units") units: String?,
            @Query("appid") api: String?
        ): Call<JsonObject?>
    }

    fun fetchWeather(latitude: Double, longitude: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service =
            retrofit.create(
                getWeather::class.java
            )
        val call: Call<JsonObject?>? = service.getCurrentWeatherData(
            latitude.toString(),
            longitude.toString(),
            (if (units == "0") "metric" else "imperial"),
            apiKey
        )


        call!!.enqueue(object : Callback<JsonObject?> {
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                return
            }

            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                if (response.code() == 200) {
                    var json = Gson().fromJson(response.body(), WeatherData::class.java)
                    utils.save(file, json)
                    done = true
                }
            }
        })
    }

    private fun getWeatherFromCache(): WeatherData? {
        return utils.load(file)
    }

    fun getAddress(): String {
        val data = getWeatherFromCache()
        return data!!.name
    }

    fun getTemperature(): String {
        val data = getWeatherFromCache()
        return data!!.main.temp.toInt().toString() + (if (units == "0") " °C" else " °F")
    }

    fun getIcon(): Int {
        val data = getWeatherFromCache()
        val id = data!!.weather[0].id
        val sunrise = data.sys.sunrise
        val sunset = data.sys.sunset
        return WeatherIconsHelper.mapConditionIconToCode(id, sunrise, sunset)
    }
}