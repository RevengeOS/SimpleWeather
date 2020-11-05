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

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import android.view.View
import android.widget.RemoteViews
import com.revengeos.weathericons.WeatherIconsHelper
import java.util.*


/**
 * Implementation of App Widget functionality.
 */
class WeatherWidget : AppWidgetProvider() {

    private val update = "org.revengeos.simpleweather.UPDATE"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent!!.action.equals(update)) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context!!, WeatherWidget::class.java)
            val appWidgetIds: IntArray = appWidgetManager.getAppWidgetIds(thisWidget)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    private fun isEvening(): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 20)
        return System.currentTimeMillis() > calendar.timeInMillis
    }

    private fun isMorning(): Boolean {
        val startTime: Calendar = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 4)
        val endTime: Calendar = Calendar.getInstance()
        endTime.set(Calendar.HOUR_OF_DAY, 10)
        return System.currentTimeMillis() > startTime.timeInMillis && System.currentTimeMillis() < endTime.timeInMillis
    }

    private fun getGreeting(context: Context): String? {
        val cursor: Cursor? = context.contentResolver
            .query(ContactsContract.Profile.CONTENT_URI, null, null, null, null)
        if (cursor != null && cursor.count > 0 && cursor.moveToFirst()) {
            val fullName: String = cursor.getString(cursor.getColumnIndex("display_name"))
            cursor.close()
            val name = fullName.split(" ")[0]
            if (isMorning()) {
                return "Good morning, $name"
            }
            if (isEvening()) {
                return "Good night, $name"
            }
        }
        return null
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.weather_widget)

        val utils = WeatherUtils(context)

        val iconResource = WeatherIconsHelper.getDrawable(utils.getIcon(), context)

        if ((isEvening() || isMorning())) {
            views.setViewVisibility(R.id.condition_line, View.GONE)
            views.setViewVisibility(R.id.greeting_line, View.VISIBLE)
            views.setTextViewText(R.id.greeting_current_text, utils.getTemperature())
            iconResource?.let { views.setImageViewResource(R.id.greeting_current_image, it) }
            views.setTextViewText(R.id.greeting_text, getGreeting(context))
        } else {
            views.setViewVisibility(R.id.condition_line, View.VISIBLE)
            views.setViewVisibility(R.id.greeting_line, View.GONE)
            views.setTextViewText(R.id.current_text, utils.getTemperature())
            iconResource?.let { views.setImageViewResource(R.id.current_image, it) }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}