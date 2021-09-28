package com.hxs.expandedcalendarview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hxs.expandedcalendarview.newwidget.STATE_COLLAPSED
import java.util.*

class CalendarModel : ViewModel() {

    val expandState = MutableLiveData(STATE_COLLAPSED)

    val curDate = MutableLiveData(Calendar.getInstance())


//    fun lastCalendar(): Calendar {
//        val cal = curDate.value?.clone() as Calendar? ?: Calendar.getInstance()
//        if (expandState.value == STATE_EXPANDED) {
//            cal.add(Calendar.MONTH, -1)
//        } else {
//            cal.add(Calendar.MONTH, 1)
//        }
//        return cal
//    }

//    fun nextCalendar(): Calendar {
//        val cal = curDate.value?.clone() as Calendar? ?: Calendar.getInstance()
//        if (expandState.value == STATE_EXPANDED) {
//            cal.add(Calendar.MONTH, 1)
//        } else {
//            cal.add(Calendar.MONTH, 1)
//        }
//        return cal.get(Calendar.WEE)
//    }
}