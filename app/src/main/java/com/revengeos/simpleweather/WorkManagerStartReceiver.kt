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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager


class WorkManagerStartReceiver : BroadcastReceiver() {
    private val tag = "WorkManagerStartReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(tag, "onReceive: " + intent!!.action)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val isEnabled = sharedPreferences.getBoolean("weather_enabled", false)

        if (isEnabled) {
            val alarmScheduler = context?.let { AlarmScheduler(it) }
            alarmScheduler!!.scheduleAlarm()
        } else {
            Log.d(tag, "Service disabled not starting")
        }

    }
}