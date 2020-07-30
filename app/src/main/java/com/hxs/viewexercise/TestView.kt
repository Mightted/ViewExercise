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
        color = Color.BLACK
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val path = Path()
    private var touchX = -1f
    private var touchY = -1f

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }


    override fun onDraw(canvas: Canvas?) {
        
        super.onDraw(canvas)
        canvas?.run {
            path.reset()

            path.moveTo(measuredWidth / 2f, 200f)
            if (touchX != -1f || touchY != -1f) {
                path.lineTo(touchX, touchY)
            }

            path.lineTo(measuredWidth / 2f, measuredHeight - 200f)

            drawPath(path, paint)

        }


    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        println("touchX:$touchX, touchY:$touchY")
        return when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                touchX = event.x
                touchY = event.y
                invalidate()
                true
            }
            MotionEvent.ACTION_UP -> {
                touchX = -1f
                touchY = -1f
                invalidate()
                true
            }
            else -> super.onTouchEvent(event)
        }
    }
}