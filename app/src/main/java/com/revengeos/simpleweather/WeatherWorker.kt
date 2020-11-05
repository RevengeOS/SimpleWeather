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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class WeatherWorker(private val context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude = 0.0
    private var longitude = 0.0
    private var gotLocation: Boolean = false
    private val intent: Intent = Intent("org.revengeos.simpleweather.UPDATE")
    private val timeOutMillis = 5000

    override fun doWork(): Result {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val utils = WeatherUtils(context)

        getLastKnownLocation()

        var time = 0
        while (!gotLocation && time <= timeOutMillis) {
            Thread.sleep(1)
            time++
            if (time >= timeOutMillis) {
                return Result.retry()
            }

        }

        utils.fetchWeather(latitude, longitude)

        time = 0
        while (!utils.done && time <= timeOutMillis) {
            Thread.sleep(1)
            time++
            if (time >= timeOutMillis) {
                return Result.retry()
            }
        }


        val temp = utils.getTemperature()
        val icon = utils.getIcon()
        intent.putExtra("temp", temp)
            .putExtra("icon", icon)
        context.sendBroadcast(intent)

        return Result.success()

    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    gotLocation = true
                }
            }
    }
}