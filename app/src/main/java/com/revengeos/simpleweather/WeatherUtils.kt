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

    fun getWeatherFromCache(): WeatherData? {
        return utils.load(file)
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
        return mapConditionIconToCode(id, sunrise, sunset)
    }

    private fun mapConditionIconToCode(conditionId: Int, sunrise: Long, sunset: Long): Int {
        val ut2 = System.currentTimeMillis() / 1000L
        if (ut2 in (sunrise + 1) until sunset) {
            // First, use condition ID for specific cases
            when (conditionId) {
                202, 232, 211 -> return 4
                212 -> return 3
                221, 231, 201 -> return 38
                230, 200, 210 -> return 37
                300, 301, 302, 310, 311, 312, 313, 314, 321 -> return 9
                500, 501, 520, 521, 531 -> return 11
                502, 503, 504, 522 -> return 12
                511 -> return 10
                600, 620 -> return 14 // light snow
                601, 621 -> return 16 // snow
                602, 622 -> return 41 // heavy snow
                611, 612 -> return 18 // sleet
                615, 616 -> return 5 // rain and snow
                741 -> return 20
                711, 762 -> return 22
                701, 721 -> return 21
                731, 751, 761 -> return 19
                771 -> return 23
                781 -> return 0
                800 -> return 32
                801 -> return 34
                802 -> return 28
                803, 804 -> return 30
                900 -> return 0 // tornado
                901 -> return 1 // tropical storm
                902 -> return 2 // hurricane
                903 -> return 25 // cold
                904 -> return 36 // hot
                905 -> return 24 // windy
                906 -> return 17 // hail
            }
        } else {
            // First, use condition ID for specific cases
            when (conditionId) {
                202, 232, 211 -> return 4
                212 -> return 3
                221, 231, 201 -> return 47
                230, 200, 210 -> return 45
                300, 301, 302, 310, 311, 312, 313, 314, 321 -> return 9
                500, 501, 520, 521, 531 -> return 11
                502, 503, 504, 522 -> return 12
                511 -> return 10
                600, 620 -> return 14 // light snow
                601, 621 -> return 16 // snow
                602, 622 -> return 41 // heavy snow
                611, 612 -> return 18 // sleet
                615, 616 -> return 5 // rain and snow
                741 -> return 20
                711, 762 -> return 22
                701, 721 -> return 21
                731, 751, 761 -> return 19
                771 -> return 23
                781 -> return 0
                800 -> return 31
                801 -> return 33
                802 -> return 27
                803, 804 -> return 29
                900 -> return 0 // tornado
                901 -> return 1 // tropical storm
                902 -> return 2 // hurricane
                903 -> return 25 // cold
                904 -> return 36 // hot
                905 -> return 24 // windy
                906 -> return 17 // hail
            }
        }
        return -1
    }
}