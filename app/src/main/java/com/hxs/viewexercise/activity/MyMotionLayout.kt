package com.hxs.viewexercise.activity

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout

class MyMotionLayout : MotionLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    var needIntercept = false

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {

        if (needIntercept) {
            return true
        } else {
            return super.onInterceptTouchEvent(event)
        }

    }


}