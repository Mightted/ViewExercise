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
import kotlin.math.max


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

        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
        color = Color.GREEN
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val centerX: Float get() = measuredWidth / 2f
    private val centerY: Float get() = measuredHeight / 2f
    private var translateX = -1f
    private var translateY = -1f
    private var centerPointX = 0f
    private var centerPointY = 0f
    private val radius = 80f

    private val path = Path()

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        post {
//            centerX = measuredWidth / 2f
//            centerY = measuredHeight / 2f
            translateX = centerX
            translateY = centerY
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.run {
//            paint.color = Color.BLUE
//            drawRect(centerX - 80f, centerY - 80f, centerX + 80f, centerY + 80f, paint)

            path.reset()
            BezierUtil.addPath(path, centerX, centerY, radius)
//            path.moveTo(centerX, centerY)
            if ((translateX != -1f && translateY != -1f) || !(translateX == centerX && translateY == centerY)) {
                BezierUtil.addPath(path, translateX, translateY, radius)
                centerPointX = (centerX + translateX) / 2f
                centerPointY = (centerY + translateY) / 2f
                val rangle =
                    Math.atan2((translateY - centerY).toDouble(), (translateY - centerY).toDouble())
                val point1 = floatArrayOf(
                    centerX + Math.sin(rangle).toFloat() * radius,
                    centerY - Math.cos(rangle).toFloat() * radius
                )
                val point2 = floatArrayOf(
                    translateX + Math.sin(rangle).toFloat() * radius,
                    translateY - Math.cos(rangle).toFloat() * radius
                )

                path.moveTo(point1[0], point1[1])
                path.quadTo(centerPointX, centerPointY, point2[0], point2[1])

                paint.color = Color.GREEN
                drawPath(path, paint)

                paint.color = Color.CYAN
                drawPoints(point1, paint)
                drawPoints(point2, paint)
//                path.lineTo(translateX, translateY)
            }

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x in (centerX - 80f)..(centerX + 80f) &&
                    event.y in (centerY - 80f)..(centerY + 80f)
                ) {
                    true
                } else {
                    super.onTouchEvent(event)
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if (event.x in (centerX - 80f)..(centerX + 80f) &&
                    event.y in (centerY - 80f)..(centerY + 80f)
                ) {
                    translateX = centerX
                    translateY = centerY
                } else {
                    translateX = event.x
                    translateY = event.y
                }
                invalidate()
                true
            }

            MotionEvent.ACTION_UP -> {
                translateX = centerX
                translateY = centerY
                invalidate()
                super.onTouchEvent(event)
            }
            else -> {
                super.onTouchEvent(event)
            }
        }
    }


}