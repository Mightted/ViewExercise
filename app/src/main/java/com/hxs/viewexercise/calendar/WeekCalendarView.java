package com.hxs.viewexercise.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hxs.viewexercise.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 日历控件 功能：获得点选的日期区间
 */
public class WeekCalendarView extends View implements View.OnTouchListener {
    private Context context;
    private final static String TAG = "anCalendar";
    //    private Date selectedStartDate;
//    private Date selectedEndDate;
    private Date selectedDate;
    private Date curDate; // 当前日历显示的月
    private Date curMonth; // 当前月1号，用于索引
    private int curWeek; // 当前选择的自然周
    private int curYear; // 当前选择的年份
    //    private int validWeek;
//    private Date today; // 今天的日期文字显示红色
//    private Date downDate; // 手指按下状态时临时日期
//    private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
    private int downIndex; // 按下的格子索引
    private Calendar calendar;
    private Surface surface;
    private ArrayList<Integer> date = new ArrayList<>();
    private DateChangeListener listener;
    //    private boolean completed = false; // 为false表示只选择了开始日期，true表示结束日期也选择了
    private boolean isExpanded = false;

    public WeekCalendarView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        selectedDate = curMonth = curDate = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(curMonth);
        curWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        curYear = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        curMonth = calendar.getTime();
        surface = new Surface();
        surface.density = getResources().getDisplayMetrics().density;
        setBackgroundColor(surface.bgColor);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surface.width = getResources().getDisplayMetrics().widthPixels;
        surface.height = getResources().getDisplayMetrics().heightPixels * 2 / 5;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(surface.width,
                MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(surface.height,
                MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (changed) {
            surface.init();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isExpanded) {
            return;
        }

        surface.cellBgPaint.setColor(0xfff2f2f2);
        canvas.drawRect(0, 0, surface.width, surface.weekHeight,
                surface.cellBgPaint);
        float cellX;
        float baseLine = getPaintBaseLine(surface.weekPaint);
        float cellY = surface.cellHeight * 0.5f + baseLine;
        for (int i = 0; i < surface.weekText.length; i++) {
            cellX = surface.cellWidth * (i + 0.5f);
            canvas.drawText(context.getString(surface.weekText[i]), cellX, cellY,
                    surface.weekPaint);
        }


        calendar.setTime(curMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int curMonth = calendar.get(Calendar.MONTH);
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayIndex = -dayInWeek + 1;
        calendar.add(Calendar.DAY_OF_MONTH, dayIndex);
        date.clear();
        int week;
        int color;
        boolean drawWeekBg = false;
        for (int i = 0; i < 42; i++) {

            if (!isWeekOfCurMonth(calendar, curMonth)) {
                break;
            }
            week = calendar.get(Calendar.DAY_OF_WEEK);
            if ((week == Calendar.SUNDAY || week == Calendar.SATURDAY)
                    && curWeek == calendar.get(Calendar.WEEK_OF_YEAR)
                    && curYear == calendar.get(Calendar.YEAR)) {
                color = Color.WHITE;
                if (!drawWeekBg) {
                    drawSelectedWeekBg(canvas, i);
                    drawWeekBg = true;
                }
            } else if (calendar.get(Calendar.MONTH) == curMonth && !calendar.getTime().after(curDate)) {
                color = surface.todayNumberColor;
            } else {
                color = surface.todayLaterColor;
            }
            drawCellText(canvas, i, calendar.get(Calendar.DAY_OF_MONTH) + "", color);
            date.add(dayIndex++);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        super.onDraw(canvas);
    }

    private boolean isWeekOfCurMonth(Calendar calendar, int curMonth) {
        return (calendar.get(Calendar.MONTH) == curMonth)
                || isDayOfLastMonth(calendar, curMonth) // 这里前面已经确定了上个月最后展示的天数，否则还需要加限制
                || (isDayOfNextMonth(calendar, curMonth) && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY);
    }

    private boolean isDayOfLastMonth(Calendar calendar, int curMonth) {
        return complete(curMonth, calendar.get(Calendar.MONTH));
    }

    private boolean isDayOfNextMonth(Calendar calendar, int curMonth) {
        return complete(calendar.get(Calendar.MONTH), curMonth);
    }

    private boolean complete(int month1, int month2) {
        return month1 > month2 || (month1 + 1) % 12 > (month2 + 1) % 12;
    }


    private void drawSelectedWeekBg(Canvas canvas, int index) {
        if (canvas == null) {
            return;
        }
        float y = surface.cellHeight * getYByIndex(index);
        Paint paint = new Paint();
        paint.setColor(0x1700BAFF);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(surface.cellWidth * 0.1f,
                y + surface.cellHeight * 0.0f,
                surface.width - surface.cellWidth * 0.1f,
                y + surface.cellHeight * 1f,
                surface.cellHeight * 0.5f,
                surface.cellHeight * 0.5f,
                paint);
        paint.setColor(0xFF00BAFF);
        canvas.drawCircle(surface.cellWidth * 0.5f, y + surface.cellHeight * 0.5f, 0.4f * surface.cellHeight, paint);
        canvas.drawCircle(surface.width - surface.cellWidth * 0.5f, y + surface.cellHeight * 0.5f, 0.4f * surface.cellHeight, paint);

    }


    /**
     * @param canvas
     * @param index
     * @param text
     */
    private void drawCellText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        float cellY = (y + 0.5f) * surface.cellHeight;
        float cellX = surface.cellWidth * (x - 0.5f);
        surface.datePaint.setColor(color);
        canvas.drawText(text, cellX, cellY + surface.dateBaseLine, surface.datePaint);
    }


    private int getXByIndex(int i) {
        return i % 7 + 1; // 1 2 3 4 5 6 7
    }

    private int getYByIndex(int i) {
        return i / 7 + 1; // 1 2 3 4 5 6
    }

    // 获得当前应该显示的年月
//    public String getYearAndmonth() {
//        calendar.setTime(curDate);
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        String str = new DecimalFormat("00").format(month);
//        return year + "-" + str;
//    }


    private void setSelectedDateByCoor(float x, float y) {

        if (date == null || date.isEmpty()) {
            return;
        }

        Log.v("tag", "setselecteddate " + x + " " + y);
//        noTouchUp = false;
        if (y < surface.weekHeight) {
            isExpanded = true;
            return;
        }

        if (y > surface.monthHeight + surface.weekHeight) {
            int m = (int) (Math.floor(x / surface.cellWidth) + 1);
            int n = (int) (Math
                    .floor((y - (surface.monthHeight + surface.weekHeight))
                            / Float.valueOf(surface.cellHeight)) + 1);
            downIndex = (n - 1) * 7 + m - 1;
            Log.d(TAG, "downIndex:" + downIndex);

            updateWeek(downIndex);
        }
    }

    public void lastMonth() {
        if (!isExpanded) {
            return;
        }
        calendar.setTime(curMonth);
        calendar.add(Calendar.MONTH, -1);
        curMonth = calendar.getTime();
        if (listener != null) {
            listener.onDateChanged(curMonth);
        }
        invalidate();
    }

    public void nextMonth() {
        if (!isExpanded) {
            return;
        }
        calendar.setTime(curMonth);
        calendar.add(Calendar.MONTH, 1);
        curMonth = calendar.getTime();
        if (listener != null) {
            listener.onDateChanged(curMonth);
        }
        invalidate();
    }


    private void updateWeek(int index) {
        if (date == null || downIndex >= date.size()) {
            return;
        }
        int relativeDay = date.get(index);
        calendar.setTime(curMonth);
        calendar.add(Calendar.DAY_OF_MONTH, relativeDay);
        Date temp = calendar.getTime();
        if (temp.after(curDate)) {
            return;
        }
        isExpanded = false;
        if (curWeek == calendar.get(Calendar.WEEK_OF_YEAR) && curYear == calendar.get(Calendar.YEAR)) {
            return;
        }
        curWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        curYear = calendar.get(Calendar.YEAR);
        selectedDate = calendar.getTime();
        invalidate();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isExpanded) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (!isExpanded) {
                    if (listener != null) {
                        calendar.setTime(selectedDate);
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        Date start = calendar.getTime();
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        Date end = calendar.getTime();
                        listener.onWeekClicked(start, end);
                    }
                    setVisibility(View.GONE);
                }
//                if (noTouchUp)
//                    break;
//                if (downDate != null) {
//                    selectedStartDate = selectedEndDate = downDate;
//                    // 响应监听事件
//                    onItemClickListener.OnItemClick(selectedStartDate,
//                            selectedEndDate, downDate);
//                    invalidate();
//                }

                break;
        }
        return true;
    }


    private float getPaintBaseLine(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return (metrics.bottom - metrics.top) / 2f - metrics.bottom;
    }

    public void setOnDateChangedListener(DateChangeListener listener) {
        this.listener = listener;
    }


    interface DateChangeListener {
        void onDateChanged(Date date);

        void onWeekClicked(Date startDate, Date endDate);
    }

    public void show() {
        isExpanded = true;
        setVisibility(View.VISIBLE);
        invalidate();
    }

    public void close() {
        isExpanded = false;
        setVisibility(View.GONE);
//        invalidate();
    }


    /**
     * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
     */
    private static class Surface {
        public float density;
        public int width; // 整个控件的宽度
        public int height; // 整个控件的高度
        public float monthHeight; // 显示月的高度
        public float weekHeight; // 显示星期的高度
        public float cellWidth; // 日期方框宽度
        public float cellHeight; // 日期方框高度
        public int bgColor = Color.parseColor("#FFFFFF");
        private int textColor = Color.BLACK;
        public int todayNumberColor = Color.BLACK;
        public int todayLaterColor = Color.parseColor("#b3b3b3");
        public int cellSelectedColor = Color.parseColor("#57c7c2");
        public Paint weekPaint;
        public Paint datePaint;
        public Paint cellBgPaint;
        public int[] weekText = {R.string.history_Sun, R.string.history_Mon, R.string.history_Tues, R.string.history_Wed,
                R.string.history_Thur, R.string.history_Fri, R.string.history_Sat};

        public float dateBaseLine;

        public void init() {
            monthHeight = 0;// (float) ((temp + temp * 0.3f) * 0.6);
            weekHeight = height / 7f;
            cellHeight = (height - monthHeight - weekHeight) / 6f;
            cellWidth = width / 7f;
            weekPaint = new Paint();
            weekPaint.setColor(Color.BLACK);
            weekPaint.setAntiAlias(true);
            float weekTextSize = weekHeight * 0.44f;
            weekPaint.setTextSize(weekTextSize);
            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
            weekPaint.setTextAlign(Paint.Align.CENTER);
            datePaint = new Paint();
            datePaint.setColor(textColor);
            datePaint.setAntiAlias(true);
            datePaint.setTextAlign(Paint.Align.CENTER);
            float cellTextSize = weekHeight * 0.45f;
            datePaint.setTextSize(cellTextSize);
            Paint.FontMetrics metrics = datePaint.getFontMetrics();
            dateBaseLine = (metrics.bottom - metrics.top) / 2f - metrics.bottom;
            cellBgPaint = new Paint();
            cellBgPaint.setAntiAlias(true);
            cellBgPaint.setStyle(Paint.Style.FILL);
            cellBgPaint.setColor(cellSelectedColor);
        }
    }
}
