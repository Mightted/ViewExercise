package com.hxs.viewexercise.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MonthCalendarView extends View implements View.OnTouchListener {
    private Context context;
    private final static String TAG = "anCalendar";
    //    private Date selectedStartDate;
//    private Date selectedEndDate;
    private Date selectedDate;
    private Date curDate; // 当前日历显示的月
    private int curMonth; // 当前月1号，用于索引
    //    private int curWeek; // 当前选择的自然周
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

    public MonthCalendarView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        selectedDate = curDate = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
//        curWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH);
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//        curMonth = calendar.getTime(Calendar);
        surface = new Surface();
        surface.density = getResources().getDisplayMetrics().density;
        setBackgroundColor(surface.bgColor);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surface.width = getResources().getDisplayMetrics().widthPixels;
        surface.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.3);
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

//        surface.cellBgPaint.setColor(0xfff2f2f2);
//        canvas.drawRect(0, 0, surface.width, surface.weekHeight,
//                surface.cellBgPaint);
//        float cellX;
//        float baseLine = getPaintBaseLine(surface.weekPaint);
//        float cellY = surface.cellHeight * 0.5f + baseLine;
//        for (int i = 0; i < surface.weekText.length; i++) {
//            cellX = surface.cellWidth * (i + 0.5f);
//            canvas.drawText(context.getString(surface.weekText[i]), cellX, cellY,
//                    surface.weekPaint);
//        }


        calendar.setTime(curDate);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//        int curMonth = calendar.get(Calendar.MONTH);
//        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        int dayIndex = -dayInWeek + 1;
//        calendar.add(Calendar.DAY_OF_MONTH, dayIndex);
        date.clear();
//        int week;
        int color = Color.BLACK;
//        boolean drawWeekBg = false;

        for (int i = 0; i < 12; i++) {

//            if (!isWeekOfCurMonth(calendar, curMonth)) {
//                break;
//            }
//            week = calendar.get(Calendar.DAY_OF_WEEK);
            if (i == curMonth && curYear == calendar.get(Calendar.YEAR)) {
                drawSelectedWeekBg(canvas, i);
                color = 0xFF00BAFF;
//                drawCellText(canvas, i, i + 1 + "", 0xFF00BAFF);
            } else if (calendar.after(Calendar.getInstance())) {
                color = surface.todayLaterColor;
            } else {
                color = Color.BLACK;
            }
            drawCellText(canvas, i, i + 1 + "", color);

            date.add(i);
            calendar.add(Calendar.MONTH, 1);
//            if ((week == Calendar.SUNDAY || week == Calendar.SATURDAY)
//                    && curWeek == calendar.get(Calendar.WEEK_OF_YEAR)
//                    && curYear == calendar.get(Calendar.YEAR)) {
//                color = Color.WHITE;
//                if (!drawWeekBg) {
//                    drawSelectedWeekBg(canvas, i);
//                    drawWeekBg = true;
//                }
//            } else if (calendar.get(Calendar.MONTH) == curMonth && !calendar.getTime().after(curDate)) {
//                color = surface.todayNumberColor;
//            } else {
//                color = surface.todayLaterColor;
//            }
//
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        super.onDraw(canvas);
    }
//
//    private boolean isWeekOfCurMonth(Calendar calendar, int curMonth) {
//        return (calendar.get(Calendar.MONTH) == curMonth)
//                || isDayOfLastMonth(calendar, curMonth) // 这里前面已经确定了上个月最后展示的天数，否则还需要加限制
//                || (isDayOfNextMonth(calendar, curMonth) && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY);
//    }

//    private boolean isDayOfLastMonth(Calendar calendar, int curMonth) {
//        return complete(curMonth, calendar.get(Calendar.MONTH));
//    }
//
//    private boolean isDayOfNextMonth(Calendar calendar, int curMonth) {
//        return complete(calendar.get(Calendar.MONTH), curMonth);
//    }

//    private boolean complete(int month1, int month2) {
//        return month1 > month2 || (month1 + 1) % 12 > (month2 + 1) % 12;
//    }


    private void drawSelectedWeekBg(Canvas canvas, int index) {
        if (canvas == null) {
            return;
        }
        float x = surface.cellWidth * getXByIndex(index);
        float y = surface.cellHeight * getYByIndex(index);
        Paint paint = new Paint();
        paint.setColor(0x1700BAFF);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(x + surface.cellWidth * 0.1f,
                y + surface.cellHeight * 0.2f,
                x + surface.cellWidth * 0.9f,
                y + surface.cellHeight * 0.8f,
                surface.cellHeight * 0.3f,
                surface.cellHeight * 0.3f,
                paint);
        paint.setColor(0xFF00BAFF);
//        canvas.drawCircle(surface.cellWidth * 0.5f, y + surface.cellHeight * 0.5f, 0.4f * surface.cellHeight, paint);
//        canvas.drawCircle(surface.width - surface.cellWidth * 0.5f, y + surface.cellHeight * 0.5f, 0.4f * surface.cellHeight, paint);

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
        float cellX = surface.cellWidth * (x + 0.5f);
        surface.datePaint.setColor(color);
        canvas.drawText(text, cellX, cellY + surface.dateBaseLine, surface.datePaint);
    }


    private int getXByIndex(int i) {
        return i % 4; // 1 2 3 4 5 6 7
    }

    private int getYByIndex(int i) {
        return i / 4; // 1 2 3 4 5 6
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
//        if (y < surface.weekHeight) {
//            isExpanded = true;
//            return;
//        }

        if (y < surface.height) {
            int m = (int) Math.floor(x / surface.cellWidth);
            int n = (int) Math.floor(y / surface.cellHeight);
//            int m = (int) (Math.floor(x / surface.cellWidth) + 1);
//            int n = (int) (Math.floor((y - (surface.monthHeight + surface.weekHeight))
//                    / Float.valueOf(surface.cellHeight)) + 1);
//            downIndex = (n - 1) * 4 + m - 1;
            downIndex = n * 4 + m;
            Log.d(TAG, "downIndex:" + downIndex);

            updateWeek(downIndex);
        } else {
            isExpanded = false;
        }
    }

    public void last() {
        if (!isExpanded) {
            return;
        }
        calendar.setTime(curDate);
        calendar.add(Calendar.YEAR, -1);
        curDate = calendar.getTime();
        if (listener != null) {
            listener.onDateChanged(curDate);
        }
        invalidate();
    }

    public void next() {
        if (!isExpanded) {
            return;
        }
        calendar.setTime(curDate);
        calendar.add(Calendar.YEAR, 1);
        curDate = calendar.getTime();
        if (listener != null) {
            listener.onDateChanged(curDate);
        }
        invalidate();
    }


    private void updateWeek(int index) {
        if (date == null || downIndex >= date.size()) {
            return;
        }
//        int relativeDay = date.get(index);
        calendar.setTime(curDate);
        calendar.set(Calendar.MONTH, index);
//        Date temp = calendar.getTime();
        if (calendar.after(Calendar.getInstance())) {
            return;
        }
        isExpanded = false;
        if (curMonth == calendar.get(Calendar.MONTH) && curYear == calendar.get(Calendar.YEAR)) {
            return;
        }
        curMonth = calendar.get(Calendar.MONTH);
        curYear = calendar.get(Calendar.YEAR);
        selectedDate = calendar.getTime();
        invalidate();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isExpanded) {
                    setSelectedDateByCoor(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isExpanded) {
                    if (listener != null) {
                        calendar.setTime(selectedDate);
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        Date start = calendar.getTime();
                        calendar.add(Calendar.MONTH, 1);
                        calendar.add(Calendar.SECOND, -1);
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
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


//    interface DateChangeListener {
//        void onDateChanged(Date date);
//
//        void onWeekClicked(Date startDate, Date endDate);
//    }

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
        //        public float monthHeight; // 显示月的高度
//        public float weekHeight; // 显示星期的高度
        public float cellWidth; // 日期方框宽度
        public float cellHeight; // 日期方框高度
        public int bgColor = Color.parseColor("#FFFFFF");
        private int textColor = Color.BLACK;
        //        public int todayNumberColor = Color.BLACK;
        public int todayLaterColor = Color.parseColor("#b3b3b3");
        public int cellSelectedColor = Color.parseColor("#57c7c2");
        //        public Paint weekPaint;
        public Paint datePaint;
        public Paint cellBgPaint;
//        public int[] weekText = {R.string.history_Sun, R.string.history_Mon, R.string.history_Tues, R.string.history_Wed,
//                R.string.history_Thur, R.string.history_Fri, R.string.history_Sat};

        public float dateBaseLine;

        public void init() {
//            monthHeight = 0;// (float) ((temp + temp * 0.3f) * 0.6);
//            weekHeight = height / 7f;
            cellHeight = height / 3f;
            cellWidth = width / 4f;
//            weekPaint = new Paint();
//            weekPaint.setColor(Color.BLACK);
//            weekPaint.setAntiAlias(true);
//            float weekTextSize = weekHeight * 0.44f;
//            weekPaint.setTextSize(weekTextSize);
//            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
//            weekPaint.setTextAlign(Paint.Align.CENTER);
            datePaint = new Paint();
            datePaint.setColor(textColor);
            datePaint.setAntiAlias(true);
            datePaint.setTextAlign(Paint.Align.CENTER);
            float cellTextSize = cellHeight * 0.35f;
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