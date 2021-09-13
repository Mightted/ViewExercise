package com.hxs.viewexercise.calendar;

import java.util.Date;

interface DateChangeListener {
    void onDateChanged(Date date);

    void onWeekClicked(Date startDate, Date endDate);
}
