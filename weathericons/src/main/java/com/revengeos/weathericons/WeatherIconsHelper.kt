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

package com.revengeos.weathericons

import android.content.Context
import android.graphics.drawable.Drawable

class WeatherIconsHelper {
    companion object {
        fun mapConditionIconToCode(conditionId: Int, sunrise: Long, sunset: Long): Int {
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

        fun getDrawable(state : Int, context : Context): Int? {
            val resourceIdentifier: Int = context.resources.getIdentifier(
                "weather_$state",
                "drawable",
                context.packageName
            )
            return resourceIdentifier
        }
    }
}