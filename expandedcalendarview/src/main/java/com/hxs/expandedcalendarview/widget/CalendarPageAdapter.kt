package com.hxs.expandedcalendarview.widget


import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hxs.expandedcalendarview.data.CalendarGroup
import com.hxs.expandedcalendarview.data.Day
import com.hxs.expandedcalendarview.data.Event
import com.hxs.expandedcalendarview.view.MonthCalendarFragment
import com.hxs.expandedcalendarview.view.POS_CUR
import com.hxs.expandedcalendarview.view.POS_LAST
import com.hxs.expandedcalendarview.view.POS_NEXT
import java.util.*

class CalendarPageAdapter(
    activity: FragmentActivity,
    onItemClicked: ((Day) -> Unit)? = null,
) :
    FragmentStateAdapter(activity) {

    private val preFragment: MonthCalendarFragment
    private val actualFragment: MonthCalendarFragment
    private val nextFragment: MonthCalendarFragment


    init {
        actualFragment = MonthCalendarFragment(POS_CUR, onItemClicked, null)
        preFragment = MonthCalendarFragment(POS_LAST, null, null)
        nextFragment = MonthCalendarFragment(POS_NEXT, null, null)
    }


//    fun next(isWeekMode: Boolean): Calendar {
//        calendars.next()
//        preFragment.next(isWeekMode)
//        actualFragment.next(isWeekMode)
//        nextFragment.next(isWeekMode)
//        return calendars.curDate
//    }
//
//    fun last(isWeekMode: Boolean): Calendar {
//        calendars.last()
//        preFragment.last(isWeekMode)
//        actualFragment.last(isWeekMode)
//        nextFragment.last(isWeekMode)
//        return calendars.curDate
//    }

    fun updateClickedItem(day: Day) {
        preFragment.updateClickedItem(day)
        nextFragment.updateClickedItem(day)
    }

    fun changeToToday() {
//        val calendar = Calendar.getInstance()
//        this.calendars = calendars
        actualFragment.changeToToday()
//        calendar.add(Calendar.MONTH, -1)
//        preFragment.changeToToday()
//        calendar.add(Calendar.MONTH, 2)
//        nextFragment.changeToToday()

    }

    fun needUpdate(force: Boolean) {
        preFragment.forceUpdate = force
        actualFragment.forceUpdate = force
        nextFragment.forceUpdate = force
    }

    fun setExpandState(state: Int) {

    }

    fun updateEvents(events: List<Event>?) {
        actualFragment.updateEvents(events)
    }


    fun suitableRowIndex(): Int {
        return actualFragment.suitableRowIndex()
    }

    fun currentWeekIndex(): Int {
        return actualFragment.currentWeekIndex()
    }

    fun setWeekIndex(index: Int) {
        actualFragment.setWeekIndex(index)
    }

    fun getChildHeightAt(context: Context): Int {
        return dip2px(context, 38f).toInt()
//        return actualFragment.getChildHeightAt(index)
    }

    private fun dip2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    fun getTabCount(): Int {
        return actualFragment.getTabCount()
    }


    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            itemCount / 2 - 1 -> {
                preFragment
            }
            itemCount / 2 -> {
                actualFragment
            }
            else -> {
                nextFragment
            }
        }
    }
}