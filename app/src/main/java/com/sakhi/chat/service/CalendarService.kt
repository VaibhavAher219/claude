package com.sakhi.chat.service

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import java.util.*

class CalendarService(private val context: Context) {

    fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getTodaysEvents(): List<CalendarEvent> {
        if (!hasCalendarPermission()) return emptyList()

        val events = mutableListOf<CalendarEvent>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION
        )

        val selection = "(( ${CalendarContract.Events.DTSTART} >= ? ) AND " +
                "( ${CalendarContract.Events.DTSTART} <= ? ))"
        val selectionArgs = arrayOf(startOfDay.toString(), endOfDay.toString())

        try {
            context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${CalendarContract.Events.DTSTART} ASC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(0)
                    val title = cursor.getString(1) ?: ""
                    val startTime = cursor.getLong(2)
                    val endTime = cursor.getLong(3)
                    val description = cursor.getString(4) ?: ""
                    val location = cursor.getString(5) ?: ""

                    events.add(
                        CalendarEvent(
                            id = id,
                            title = title,
                            startTime = startTime,
                            endTime = endTime,
                            description = description,
                            location = location
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        return events
    }

    fun addEvent(event: CalendarEvent): Long {
        if (!hasCalendarPermission()) return -1

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, getDefaultCalendarId())
            put(CalendarContract.Events.TITLE, event.title)
            put(CalendarContract.Events.DESCRIPTION, event.description)
            put(CalendarContract.Events.EVENT_LOCATION, event.location)
            put(CalendarContract.Events.DTSTART, event.startTime)
            put(CalendarContract.Events.DTEND, event.endTime)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        return try {
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri?.lastPathSegment?.toLongOrNull() ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    private fun getDefaultCalendarId(): Long {
        val projection = arrayOf(CalendarContract.Calendars._ID)

        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getLong(0)
            }
        }
        return 1 // Default to 1 if no calendar found
    }
}

data class CalendarEvent(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val location: String = "",
    val startTime: Long,
    val endTime: Long
)
