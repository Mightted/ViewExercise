package com.hxs.viewexercise.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hxs.viewexercise.R
import kotlinx.android.synthetic.main.activity_calendar.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        last.setOnClickListener {
            calendarView.lastMonth()
        }
        next.setOnClickListener {
            calendarView.nextMonth()
        }

        curTime.setOnClickListener {
            calendarView.show()
        }

        val format = SimpleDateFormat("YYYY-MM")
        val weekFmt = SimpleDateFormat("YYYY-MM-dd")

        calendarView.setOnDateChangedListener(object : WeekCalendarView.DateChangeListener {
            override fun onDateChanged(date: Date?) {
                date?.let { curTime.text = format.format(it) }

            }

            override fun onWeekClicked(startDate: Date?, endDate: Date?) {
                println("${weekFmt.format(startDate)}--${weekFmt.format(endDate)}")
            }
        })

    }


}