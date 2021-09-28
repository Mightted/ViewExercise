package com.hxs.expandedcalendarview.data

import java.util.*

data class CalendarGroup(var preDate: Calendar, var curDate: Calendar, var nextDate: Calendar) {
    fun next() {
        preDate.add(Calendar.MONTH, 1)
        curDate.add(Calendar.MONTH, 1)
        nextDate.add(Calendar.MONTH, 1)
    }

    fun last() {
        preDate.add(Calendar.MONTH, -1)
        curDate.add(Calendar.MONTH, -1)
        nextDate.add(Calendar.MONTH, -1)

    }
}
