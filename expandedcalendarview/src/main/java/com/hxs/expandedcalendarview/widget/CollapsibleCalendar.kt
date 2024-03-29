package com.hxs.expandedcalendarview.widget

/**
 * Created by shrikanthravi on 07/03/18.
 */


import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.hxs.expandedcalendarview.CalendarModel
import com.hxs.expandedcalendarview.R
import com.hxs.expandedcalendarview.data.CalendarAdapter
import com.hxs.expandedcalendarview.data.CalendarGroup
import com.hxs.expandedcalendarview.data.Day
import com.hxs.expandedcalendarview.data.Event
import com.hxs.expandedcalendarview.view.BounceAnimator
import com.hxs.expandedcalendarview.view.ExpandIconView
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class CollapsibleCalendar : UICalendar, View.OnClickListener {

    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", getCurrentLocale(context))

    private lateinit var model: CalendarModel


    fun initModel(activity: FragmentActivity) {
        model = ViewModelProvider(activity).get(CalendarModel::class.java)
    }

//    private val calendars: CalendarGroup = CalendarGroup(
//        Calendar.getInstance().apply { add(Calendar.MONTH, -1) },
//        Calendar.getInstance(),
//        Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
//    )
//
//    private fun resetCalendars() {
//        calendars.preDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
//        calendars.curDate = Calendar.getInstance()
//        calendars.nextDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
//    }

    override fun changeToToday() {
//        resetCalendars()
        val calendar = Calendar.getInstance()
        val calenderAdapter = CalendarAdapter(context, calendar)
        calenderAdapter.mEventList = mAdapter!!.mEventList
        calenderAdapter.setFirstDayOfWeek(firstDayOfWeek)
        val today = GregorianCalendar()
        this.selectedItem = null
        this.selectedItemPosition = -1
        this.selectedDay =
            Day(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
        mCurrentWeekIndex = suitableRowIndex
        setAdapter(calenderAdapter)
        mListener?.onItemClick(selectedDay as Day)
        pagerAdapter?.changeToToday()
        model.curDate.postValue(Calendar.getInstance())
    }

    override fun onClick(view: View?) {
        view?.let {
            mListener.let { mListener ->
                if (mListener == null) {
                    expandIconView.performClick()
                }
            }
        }
    }

    private var mAdapter: CalendarAdapter? = null
    private var mListener: CalendarListener? = null

    var expanded = false

    private var mInitHeight = 0

    private val mHandler = Handler()
    private var mIsWaitingForUpdate = false

    private var mCurrentWeekIndex: Int = 0

    private val suitableRowIndex: Int
        get() {
            return pagerAdapter?.suitableRowIndex() ?: 0
        }
//        get() {
//            if (selectedItemPosition != -1) {
//                val view = mAdapter!!.getView(selectedItemPosition)
//                val row = view.parent as TableRow
//
//                return mTableBody.indexOfChild(row)
//            } else if (todayItemPosition != -1) {
//                val view = mAdapter!!.getView(todayItemPosition)
//                val row = view.parent as TableRow
//
//                return mTableBody.indexOfChild(row)
//            } else {
//                return 0
//            }
//        }

    val year: Int
        get() = mAdapter!!.calendar.get(Calendar.YEAR)

    val month: Int
        get() = mAdapter!!.calendar.get(Calendar.MONTH)

    /**
     * The date has been selected and can be used on Calender Listener
     */
    var selectedDay: Day? = null
        get() {
            if (selectedItem == null) {
                val cal = Calendar.getInstance()
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                val year = cal.get(Calendar.YEAR)
                return Day(
                    year,
                    month,
                    day
                )
            }
            return Day(
                selectedItem!!.year,
                selectedItem!!.month,
                selectedItem!!.day
            )
        }
        set(value: Day?) {
            field = value
            redraw()
        }

    var selectedItemPosition: Int = -1
        get() {
            var position = -1
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)

                if (isSelectedDay(day)) {
                    position = i
                    break
                }
            }
            if (position == -1) {
                position = todayItemPosition
            }
            return position
        }

    val todayItemPosition: Int
        get() {
            var position = -1
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)

                if (isToday(day)) {
                    position = i
                    break
                }
            }
            return position
        }

    override var state: Int
        get() = super.state
        set(state) {
            super.state = state
            if (state == STATE_COLLAPSED) {
                expanded = false
                dateFormat = SimpleDateFormat("yyyy-MM-dd", getCurrentLocale(context))
            }
            if (state == STATE_EXPANDED) {
                expanded = true
                dateFormat = SimpleDateFormat("yyyy-MM", getCurrentLocale(context))
            }
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    protected fun init(context: Context) {


        val cal = Calendar.getInstance()
        val adapter = CalendarAdapter(context, cal)
        setAdapter(adapter)


        // bind events

        mBtnPrevMonth.setOnClickListener { prevMonth() }

        mBtnNextMonth.setOnClickListener { nextMonth() }

        mBtnPrevWeek.setOnClickListener { prevWeek() }

        mBtnNextWeek.setOnClickListener { nextWeek() }

        mTodayIcon.setOnClickListener { changeToToday() }

        expandIconView.setState(ExpandIconView.MORE, true)


        expandIconView.setOnClickListener {

            if (expanded) {
                collapse(300)
                model.expandState.value = STATE_COLLAPSED
            } else {
                expand(300)
                model.expandState.value = STATE_EXPANDED
            }
            model.curDate.postValue(mAdapter!!.calendar)
            pagerAdapter?.needUpdate(true)

        }

        this.post { collapseTo(mCurrentWeekIndex) }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

//        mInitHeight = mTableBody.measuredHeight
        mInitHeight = pager.measuredHeight

        if (mIsWaitingForUpdate) {
            redraw()
            mHandler.post { collapseTo(pagerAdapter?.currentWeekIndex() ?: 0) }
            mIsWaitingForUpdate = false
//            if (mListener != null) {
//                mListener!!.onDataUpdate()
//            }
        }
    }

    override fun redraw() {
        // redraw all views of week
        val rowWeek = mTableHead.getChildAt(0) as TableRow?
        if (rowWeek != null) {
            for (i in 0 until rowWeek.childCount) {
                (rowWeek.getChildAt(i) as TextView).setTextColor(textColor)
            }
        }
        // redraw all views of day
        if (mAdapter != null) {
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)
                val view = mAdapter!!.getView(i)
                val txtDay = view.findViewById<View>(R.id.txt_day) as TextView
                txtDay.setBackgroundColor(Color.TRANSPARENT)
                txtDay.setTextColor(textColor)

                // set today's item
                if (isToday(day)) {
                    txtDay.setBackgroundDrawable(todayItemBackgroundDrawable)
                    txtDay.setTextColor(todayItemTextColor)
                }

                // set the selected item
                if (isSelectedDay(day)) {
                    txtDay.setBackgroundDrawable(selectedItemBackgroundDrawable)
                    txtDay.setTextColor(selectedItemTextColor)
                }
            }
        }
    }

    override fun reload() {
        mAdapter?.let { mAdapter ->
            mAdapter.refresh()
//            val calendar = Calendar.getInstance()
//            val tempDatePattern = "yyyy-MM"
//            if (calendar.get(Calendar.YEAR) != mAdapter.calendar.get(Calendar.YEAR)) {
//                tempDatePattern = "YYYY-MM"
//            } else {
//                tempDatePattern = datePattern
//            }
            // reset UI
//            val dateFormat = SimpleDateFormat(tempDatePattern, getCurrentLocale(context))
//            dateFormat.timeZone = mAdapter.calendar.timeZone
            if (state == STATE_COLLAPSED) {
                changeTimeFormat(selectedDay?.toUnixTime() ?: mAdapter.calendar.timeInMillis)
            } else {
                changeTimeFormat(mAdapter.calendar.timeInMillis)
            }
//            mTxtTitle.text = dateFormat.format(mAdapter.calendar.time)
            mTableHead.removeAllViews()
//            mTableBody.removeAllViews()

            var rowCurrent: TableRow
            rowCurrent = TableRow(context)
            rowCurrent.layoutParams = TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            for (i in 0..6) {
                val view = mInflater.inflate(R.layout.layout_day_of_week, null)
                val txtDayOfWeek = view.findViewById<View>(R.id.txt_day_of_week) as TextView
                txtDayOfWeek.setText(DateFormatSymbols().getShortWeekdays()[(i + firstDayOfWeek) % 7 + 1])
                view.layoutParams = TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                rowCurrent.addView(view)
            }
            mTableHead.addView(rowCurrent)

            // set day view
//            for (i in 0 until mAdapter.count) {
//
//                if (i % 7 == 0) {
//                    rowCurrent = TableRow(context)
//                    rowCurrent.layoutParams = TableLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                    )
//                    mTableBody.addView(rowCurrent)
//                }
//                val view = mAdapter.getView(i)
//                view.layoutParams = TableRow.LayoutParams(
//                    0,
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    1f
//                )
//                params.let { params ->
//                    if (params != null && (mAdapter.getItem(i).diff < params.prevDays || mAdapter.getItem(i).diff > params.nextDaysBlocked)) {
//                        view.isClickable = false
//                        view.alpha = 0.3f
//                    } else {
//                        if (view.isClickable) {
//                            view.setOnClickListener { v -> onItemClicked(v, mAdapter.getItem(i)) }
//                        }
//                    }
//                }
//                rowCurrent.addView(view)
//            }

            redraw()
            mIsWaitingForUpdate = true
        }
    }

    private fun changeTimeFormat(time: Long) {
        mTxtTitle.text = dateFormat.format(Date(time))

    }

    fun onItemClicked(day: Day) {
        select(day)
        if (expanded) {
            expandIconView.performClick()
        }
        mListener?.onMonthChange(mAdapter!!.calendar)

        val cal = mAdapter!!.calendar

        val newYear = day.year
        val newMonth = day.month
        val oldYear = cal.get(Calendar.YEAR)
        val oldMonth = cal.get(Calendar.MONTH)
        if (newMonth != oldMonth) {
            cal.set(day.year, day.month, 1)

            if (newYear > oldYear || newMonth > oldMonth) {
                mCurrentWeekIndex = 0
            }
            if (newYear < oldYear || newMonth < oldMonth) {
                mCurrentWeekIndex = -1
            }
//            if (mListener != null) {
//                mListener!!.onMonthChange()
//            }
            reload()
        }

        if (mListener != null) {
            mListener!!.onItemClick(day)
        }
    }

    // public methods
    fun setAdapter(adapter: CalendarAdapter) {
        mAdapter = adapter
        adapter.setFirstDayOfWeek(firstDayOfWeek)

        reload()

        // init week
        mCurrentWeekIndex = suitableRowIndex
    }

    fun addEventTag(numYear: Int, numMonth: Int, numDay: Int) {
        mAdapter!!.addEvent(Event(numYear, numMonth, numDay, eventColor))

        reload()
    }

    fun addEventTag(numYear: Int, numMonth: Int, numDay: Int, color: Int) {
        mAdapter!!.addEvent(Event(numYear, numMonth, numDay, color))


        reload()
    }

    fun addAllEventTag(events: List<Event>) {
        mAdapter!!.addAllEvent(events)
        reload()
    }

    fun prevMonth() {
        val cal = mAdapter!!.calendar
        params.let {
            if (it != null && (Calendar.getInstance().get(Calendar.YEAR) * 12 + Calendar.getInstance()
                    .get(Calendar.MONTH) + it.prevDays / 30) > (cal.get(Calendar.YEAR) * 12 + cal.get(
                    Calendar.MONTH
                ))
            ) {
                val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)
                val interpolator = BounceAnimator(0.1, 10.0)
                myAnim.setInterpolator(interpolator)
//                mTableBody.startAnimation(myAnim)
                mTableHead.startAnimation(myAnim)
                return
            }
            if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
                cal.set(cal.get(Calendar.YEAR) - 1, cal.getActualMaximum(Calendar.MONTH), 1)
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1)
            }
            reload()
//            if (mListener != null) {
//                mListener!!.onMonthChange(cal)
//            }
        }

    }

    fun nextMonth() {
        val cal = mAdapter!!.calendar
        params.let {
            if (it != null && (Calendar.getInstance().get(Calendar.YEAR) * 12 + Calendar.getInstance()
                    .get(Calendar.MONTH) + it.nextDaysBlocked / 30) < (cal.get(Calendar.YEAR) * 12 + cal.get(
                    Calendar.MONTH
                ))
            ) {
                val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)
                val interpolator = BounceAnimator(0.1, 10.0)
                myAnim.setInterpolator(interpolator)
//                mTableBody.startAnimation(myAnim)
                mTableHead.startAnimation(myAnim)
                return
            }
            if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
                cal.set(cal.get(Calendar.YEAR) + 1, cal.getActualMinimum(Calendar.MONTH), 1)
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1)
            }
            reload()
//            if (mListener != null) {
//                mListener!!.onMonthChange(cal)
//            }
        }
    }

    fun updateEvents(eventList: List<Event>?) {
        mAdapter?.addAllEvent(eventList)
        pagerAdapter?.updateEvents(eventList)
        reload()
    }

    fun nextDay() {
        if (selectedItemPosition == mAdapter!!.count - 1) {
            nextMonth()
            mAdapter!!.getView(0).performClick()
            reload()
            mCurrentWeekIndex = 0
            collapseTo(mCurrentWeekIndex)
        } else {
            mAdapter!!.getView(selectedItemPosition + 1).performClick()
            if (((selectedItemPosition + 1 - mAdapter!!.calendar.firstDayOfWeek) / 7) > mCurrentWeekIndex) {
                nextWeek()
            }
        }
//        mListener?.onDayChanged()
    }

    fun prevDay() {
        if (selectedItemPosition == 0) {
            prevMonth()
            mAdapter!!.getView(mAdapter!!.count - 1).performClick()
            reload()
            return
        } else {
            mAdapter!!.getView(selectedItemPosition - 1).performClick()
            if (((selectedItemPosition - 1 + mAdapter!!.calendar.firstDayOfWeek) / 7) < mCurrentWeekIndex) {
                prevWeek()
            }
        }
//        mListener?.onDayChanged()
    }

    fun prevWeek() {
        if (mCurrentWeekIndex - 1 < 0) {
            mCurrentWeekIndex = -1
            prevMonth()
        } else {
            mCurrentWeekIndex--
            collapseTo(mCurrentWeekIndex)
        }
    }

    fun nextWeek() {
        if (mCurrentWeekIndex + 1 >= pagerAdapter?.getTabCount() ?: 0) {
            mCurrentWeekIndex = 0
            nextMonth()
        } else {
            mCurrentWeekIndex++
            collapseTo(mCurrentWeekIndex)
        }
    }

    fun isSelectedDay(day: Day?): Boolean {

        val select = selectedItem ?: selectedDay
        return (day != null
                && select != null
                && day.year == select.year
                && day.month == select.month
                && day.day == select.day)
    }

    fun isToday(day: Day?): Boolean {
        val todayCal = Calendar.getInstance()
        return (day != null
                && day.year == todayCal.get(Calendar.YEAR)
                && day.month == todayCal.get(Calendar.MONTH)
                && day.day == todayCal.get(Calendar.DAY_OF_MONTH))
    }

    /**
     * collapse in milliseconds
     */
    open fun collapse(duration: Int) {

        if (state == STATE_EXPANDED) {
            state = STATE_COLLAPSED

            mLayoutBtnGroupMonth.visibility = View.GONE
            mLayoutBtnGroupWeek.visibility = View.VISIBLE
            mBtnPrevWeek.isClickable = false
            mBtnNextWeek.isClickable = false

            val index = suitableRowIndex
            mCurrentWeekIndex = index

            val currentHeight = mInitHeight
            // TODO: 2021/9/13
            val targetHeight = pagerAdapter?.getChildHeightAt(context) ?: 0
//            val targetHeight = mTableBody.getChildAt(index).measuredHeight
            var tempHeight = 0
            // TODO: 2021/9/13 ???
            for (i in 0 until index) {
                tempHeight += pagerAdapter?.getChildHeightAt(context) ?: 0
//                tempHeight += mTableBody.getChildAt(i).measuredHeight
            }
            val topHeight = tempHeight

            val anim = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

                    mScrollViewBody.layoutParams.height = if (interpolatedTime == 1f)
                        targetHeight
                    else
                        currentHeight - ((currentHeight - targetHeight) * interpolatedTime).toInt()
                    mScrollViewBody.requestLayout()

                    if (mScrollViewBody.measuredHeight < topHeight + targetHeight) {
                        val position = topHeight + targetHeight - mScrollViewBody.measuredHeight
                        mScrollViewBody.smoothScrollTo(0, position)
                    }

                    if (interpolatedTime == 1f) {
//                        state = STATE_COLLAPSED

                        mBtnPrevWeek.isClickable = true
                        mBtnNextWeek.isClickable = true
                    }
                }
            }
            anim.duration = duration.toLong()
            startAnimation(anim)
        }

        expandIconView.setState(ExpandIconView.MORE, true)
        reload()
    }

    private fun collapseTo(index: Int) {
        var index = index
        if (state == STATE_COLLAPSED) {
            if (index == -1) {
                // TODO: 2021/9/13
                index = (pagerAdapter?.getTabCount() ?: 1) - 1
//                index = mTableBody.childCount - 1
            }
            mCurrentWeekIndex = index

            // TODO: 2021/9/13
            val targetHeight = pagerAdapter?.getChildHeightAt(context) ?: 0
//            val targetHeight = mTableBody.getChildAt(index).measuredHeight
            var tempHeight = 0
            for (i in 0 until index) {
                // TODO: 2021/9/13
                tempHeight += pagerAdapter?.getChildHeightAt(context) ?: 0
//                tempHeight += mTableBody.getChildAt(i).measuredHeight
            }
            val topHeight = tempHeight

            mScrollViewBody.layoutParams.height = targetHeight
            mScrollViewBody.requestLayout()

            mHandler.post { mScrollViewBody.scrollTo(0, topHeight) }


//            if (mListener != null) {
//                mListener!!.onWeekChange(mCurrentWeekIndex)
//            }
        }
    }

    fun expand(duration: Int) {
        if (state == STATE_COLLAPSED) {
            state = STATE_EXPANDED

            mLayoutBtnGroupMonth.visibility = View.VISIBLE
            mLayoutBtnGroupWeek.visibility = View.GONE
            mBtnPrevMonth.isClickable = false
            mBtnNextMonth.isClickable = false

            val currentHeight = mScrollViewBody.measuredHeight
            val targetHeight = mInitHeight

            val anim = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

                    mScrollViewBody.layoutParams.height = if (interpolatedTime == 1f)
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    else
                        currentHeight - ((currentHeight - targetHeight) * interpolatedTime).toInt()
                    mScrollViewBody.requestLayout()

                    if (interpolatedTime == 1f) {
//                        state = STATE_EXPANDED

                        mBtnPrevMonth.isClickable = true
                        mBtnNextMonth.isClickable = true
                    }
                }
            }
            anim.duration = duration.toLong()
            startAnimation(anim)
        }

        expandIconView.setState(ExpandIconView.LESS, true)
        reload()
    }

    fun select(day: Day) {
        selectedItem = Day(day.year, day.month, day.day)

        changeTimeFormat(day.toUnixTime())


        redraw()

//        if (mListener != null) {
//            mListener!!.onDaySelect()
//        }
    }

    fun setStateWithUpdateUI(state: Int) {
        this@CollapsibleCalendar.state = state

        if (state != state) {
            mIsWaitingForUpdate = true
            requestLayout()
        }
    }

    // callback
    fun setCalendarListener(listener: CalendarListener) {
        mListener = listener
    }


    fun initPager(activity: FragmentActivity) {
        pagerAdapter = CalendarPageAdapter(activity) {
            onItemClicked(it)
            pagerAdapter?.updateClickedItem(it)
//            mListener?.onItemClick(it)
//            pagerAdapter?.changeToToday()
//            collapseTo(mCurrentWeekIndex)

        }

        pager.run {
            adapter = pagerAdapter
            val centerItem = (pagerAdapter?.itemCount ?: 0) / 2
            setCurrentItem(centerItem, false)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    if (position != centerItem && positionOffset == 0f) {
                        if (position > centerItem) {
                            if (isNewest()) {
                                pager.setCurrentItem(centerItem, true)
                                return
                            }
                            if (state == STATE_COLLAPSED) {
                                mBtnNextWeek.performClick()
                            } else if (state == STATE_EXPANDED) {
                                mBtnNextMonth.performClick()
                            }

                        } else {
                            if (state == STATE_COLLAPSED) {
                                mBtnPrevWeek.performClick()
                            } else if (state == STATE_EXPANDED) {
                                mBtnPrevMonth.performClick()
                            }
//                            prevMonth()
//                            model.curDate.postValue(mAdapter!!.calendar)
//                            mListener?.onMonthChange(mAdapter!!.calendar)
//                            mListener?.onMonthChange(
//                                pagerAdapter?.last(state == STATE_COLLAPSED) ?: Calendar.getInstance()
//                            )
//                            pagerAdapter?.last()
                        }
                        model.curDate.value = mAdapter!!.calendar
                        mListener?.onMonthChange(mAdapter!!.calendar)
                        pagerAdapter?.setWeekIndex(mCurrentWeekIndex)
                        pager.setCurrentItem(centerItem, false)
                    }
                }
            })
        }
    }

    private fun isNewest(): Boolean {
        return mAdapter?.calendar?.let {
            if (state == STATE_COLLAPSED) {
                isNewestMonth(it.timeInMillis) && it.get(Calendar.WEEK_OF_MONTH) >= mCurrentWeekIndex + 1
//                mBtnNextWeek.performClick()
            } else {
                isNewestMonth(it.timeInMillis)

//                mBtnNextMonth.performClick()
            }
//            true

        } ?: false
//        if (state == STATE_COLLAPSED) {
//            mBtnNextWeek.performClick()
//        } else if (state == STATE_EXPANDED) {
//
//
//            mBtnNextMonth.performClick()
//        }
//        return false
    }

    private fun isNewestMonth(time: Long): Boolean {
        val calendar = Calendar.getInstance()
        val curYear = calendar.get(Calendar.YEAR)
        val curMonth = calendar.get(Calendar.MONTH)
        calendar.timeInMillis = time
        val selectedYear = calendar.get(Calendar.YEAR)
        val selectedMonth = calendar.get(Calendar.MONTH)
        return (selectedYear - curYear) * 12 + selectedMonth - curMonth >= 0

    }

    interface CalendarListener {

        // triggered when a day is selected programmatically or clicked by user.
//        fun onDaySelect()

        // triggered only when the views of day on calendar are clicked by user.
        fun onItemClick(day: Day)

        // triggered when the data of calendar are updated by changing month or adding events.
//        fun onDataUpdate()

        // triggered when the month are changed.
        fun onMonthChange(calendar: Calendar)

        // triggered when the week position are changed.
//        fun onWeekChange(position: Int)

//        fun onClickListener()

//        fun onDayChanged()
    }

    fun setExpandIconVisible(visible: Boolean) {
        if (visible) {
            expandIconView.visibility = View.VISIBLE
        } else {
            expandIconView.visibility = View.GONE
        }
    }

    data class Params(val prevDays: Int, val nextDaysBlocked: Int)

    var params: Params? = null
        set(value) {
            field = value
        }
}

