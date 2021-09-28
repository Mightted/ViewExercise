package com.hxs.viewexercise.calendar

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hxs.expandedcalendarview.data.Day
import com.hxs.expandedcalendarview.data.Event
import com.hxs.expandedcalendarview.widget.CollapsibleCalendar
import com.hxs.viewexercise.R
import kotlinx.android.synthetic.main.activity_calendar.*
import java.util.*

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        last.setOnClickListener {
//            calendarView.last()
        }
        next.setOnClickListener {
//            calendarView.next()
        }

        curTime.setOnClickListener {
//            calendarView.show()
        }

        calendarView.initModel(this)
        calendarView.initPager(this)

        calendarView.setCalendarListener(object : CollapsibleCalendar.CalendarListener {

            override fun onItemClick(day: Day) {
                println("onItemClick:${day.year}:${day.month}:${day.day}")
            }

            override fun onMonthChange(calendar: Calendar) {
                println("onMonthChange:${calendar.get(Calendar.MONTH)}")
                val list = mutableListOf<Event>()
                list.add(Event(2021, 7, 15))
                list.add(Event(2021, 7, 17))

                list.add(Event(2021, 7, 23))
                calendarView.updateEvents(list)
//                return list
            }

        })

//        val format = SimpleDateFormat("YYYY-MM")
//        val weekFmt = SimpleDateFormat("YYYY-MM-dd")

//        calendarView.setOnDateChangedListener(object : DateChangeListener {
//            override fun onDateChanged(date: Date?) {
//                date?.let { curTime.text = format.format(it) }
//
//            }
//
//            override fun onWeekClicked(startDate: Date?, endDate: Date?) {
//                println("${weekFmt.format(startDate)}--${weekFmt.format(endDate)}")
//            }
//        })

    }


}