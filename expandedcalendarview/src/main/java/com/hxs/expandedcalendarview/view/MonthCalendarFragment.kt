package com.hxs.expandedcalendarview.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hxs.expandedcalendarview.CalendarModel
import com.hxs.expandedcalendarview.R
import com.hxs.expandedcalendarview.data.Day
import com.hxs.expandedcalendarview.data.Event
import com.hxs.expandedcalendarview.newwidget.NewCollapsibleCalendar
import com.hxs.expandedcalendarview.newwidget.STATE_COLLAPSED
import com.hxs.expandedcalendarview.newwidget.STATE_EXPANDED
import java.util.*

const val POS_LAST = -1
const val POS_CUR = 0
const val POS_NEXT = 1

class MonthCalendarFragment(

    private val position: Int = POS_CUR,
    private val onItemClicked: ((Day) -> Unit)? = null,
    private val onMonthChange: ((Calendar) -> Unit)? = null
) : Fragment() {

    private var changeToToday = false
    var forceUpdate = false


    private lateinit var model: CalendarModel


    private var calendarBody: NewCollapsibleCalendar? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_calendar_month, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarBody = view.findViewById(R.id.calendarBody)
//        calendarBody?.init(requireContext(), calendar)
        initModel()
        calendarBody?.setCalendarListener(object : NewCollapsibleCalendar.CalendarListener {
            override fun onItemClick(day: Day) {
                onItemClicked?.invoke(day)
            }

            override fun onMonthChange(calendar: Calendar) {
                onMonthChange?.invoke(calendar)
            }
        })
    }

    fun changeToToday() {
        changeToToday = true
//        calendar = cal
//        calendarBody?.init(requireContext(), cal)
    }

    fun next(isWeekMode: Boolean) {
        if (isWeekMode) {
            calendarBody?.nextWeek()
        } else {
            calendarBody?.nextMonth()
        }

    }

    fun last(isWeekMode: Boolean) {
        if (isWeekMode) {
            calendarBody?.prevWeek()
        } else {
            calendarBody?.prevMonth()
        }
    }

    fun updateClickedItem(day: Day) {
        calendarBody?.select(day)
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        onAttachCallback?.invoke()
//    }

    private fun initModel() {
        model = ViewModelProvider(requireActivity()).get(CalendarModel::class.java)
        model.curDate.observe(viewLifecycleOwner) {
            if (changeToToday) {
                calendarBody?.changeToToday(it.clone() as Calendar)
                changeToToday = false
            } else {
                calendarBody?.init(requireContext(), it.clone() as Calendar)
                forceUpdate = false
            }

//            if(needUpdate) {
//                needUpdate = false
//
//            }
            when (position) {
                POS_LAST -> {
                    if (model.expandState.value == STATE_COLLAPSED) {
                        calendarBody?.prevWeek()
                    } else {
                        calendarBody?.prevMonth()
                    }
                }
                POS_NEXT -> {
                    if (model.expandState.value == STATE_COLLAPSED) {
                        calendarBody?.nextWeek()
                    } else {
                        calendarBody?.nextMonth()
                    }
                }
            }
//            val calendar = when (position) {
//                POS_LAST -> {
//                    if (model.expandState.value == STATE_EXPANDED) {
//                        (it.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
//                    } else {
//                        it
//                    }
//                }
//
//                POS_CUR -> {
//                    it
//                }
//                POS_NEXT -> {
//                    if (model.expandState.value == STATE_EXPANDED) {
//                        (it.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
//                    } else {
//                        it
//                    }
//                }
//                else -> {
//                    it
//                }
//            }
//            calendarBody?.init(requireContext(), calendar)
        }

        model.expandState.observe(viewLifecycleOwner) {
            if (it == STATE_EXPANDED) {
                calendarBody?.expand(0)
            } else {
                calendarBody?.collapse(0)
            }
        }
    }


    fun updateEvents(events: List<Event>?) {
        calendarBody?.updateEvents(events)
    }


    fun getTabCount(): Int {
        return calendarBody?.mTableBody?.childCount ?: 0
    }

    fun getChildHeightAt(index: Int): Int {
        return calendarBody?.mTableBody?.getChildAt(index)?.measuredHeight ?: 0
    }

    fun suitableRowIndex(): Int {
        return calendarBody?.suitableRowIndex ?: 0
    }

    fun currentWeekIndex(): Int {
        return calendarBody?.mCurrentWeekIndex ?: 0
    }

    fun setWeekIndex(index: Int) {
        calendarBody?.mCurrentWeekIndex = index
    }


}