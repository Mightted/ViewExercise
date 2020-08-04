package com.hxs.viewexercise

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 * Time: 2020/7/27
 * Author: Mightted
 * Description:
 */
class TestView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var paint: Paint = Paint().apply {

        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val centerX = 0f
    private val centerY = 0f
    private var translateX = centerX
    private var translateY = centerY

    private val path = Path()


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.run {
            path.reset()
            BezierUtil.addPath(path, measuredWidth / 2f, measuredHeight / 2f, 80f)
            drawPath(path, paint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
//                if (event.x)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                true
            }

            MotionEvent.ACTION_UP -> {
                true
            }
            else -> {
                super.onTouchEvent(event)
            }
        }


    }


}