package com.hxs.viewexercise

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator

class PhysicalLineView : View {


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
    private val pathMeasure = PathMeasure()
    private var lineLength = 0f
    private var touchX = -1f
    private var touchY = -1f
    private var isTouch = false
    private var pressY = -1f

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.run {

            drawColor(Color.CYAN)
            if (lineLength == 0f) {
                lineLength = measuredHeight - 400f
            }
            path.reset()

            path.moveTo(measuredWidth / 2f, 200f)
            if (touchX != -1f || touchY != -1f) {
                path.lineTo(touchX, touchY)
            }
            path.lineTo(measuredWidth / 2f, measuredHeight - 200f)
            pathMeasure.setPath(path, false)
            paint.strokeWidth = lineLength / pathMeasure.length * 20f


            drawPath(path, paint)

        }


    }

    private fun isTouchLine(x: Float, y: Float): Boolean {
        isTouch = y > 200f && y < measuredHeight - 200f
                && x > measuredWidth / 2 - paint.strokeWidth
                && x < measuredWidth / 2 + paint.strokeWidth
        return isTouch
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isTouchLine(event.x, event.y)) {
                    touchX = event.x
                    touchY = event.y
                    pressY = event.y
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTouch) {
                    touchX = event.x
                    touchY = event.y
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isTouch) {
                    val set = AnimatorSet()
                    val animatorX = ValueAnimator.ofFloat(touchX, measuredHeight / 2f)
                    animatorX.interpolator = OvershootInterpolator()
                    animatorX.duration = 150
                    animatorX.addUpdateListener {
                        touchX = it.animatedValue as Float
                        invalidate()
                    }

                    val animatorY = ValueAnimator.ofFloat(touchY, pressY)
                    animatorY.addUpdateListener {
                        touchY = it.animatedValue as Float
                    }
                    animatorY.duration = 10
                    set.playTogether(animatorX, animatorY)
                    set.start()
                    isTouch = false
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

}