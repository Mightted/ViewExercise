package com.hxs.viewexercise

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class RingView : View {

    private var curNum = 75
    private var size = 0f
    private val padding = 50f
    private val stroke = 20f
    private var shader: SweepGradient? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
    }
    private val bgPaint = Paint().apply {
        color = Color.parseColor("#1C2DA7FF")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun update(num: Int) {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {


            size = width.toFloat()

            if (shader == null) {
                shader =
                    SweepGradient(
                        size / 2f,
                        size / 2f,
                        Color.parseColor("#FF2DA7FF"),
                        Color.parseColor("#FF345EF3")
                    )
                paint.shader = shader
            }

            drawCircle(size / 2f, size / 2f, size / 2f - padding, bgPaint)
            paint.style = Paint.Style.STROKE
            val newPadding = padding - stroke / 2
            val angle = curNum / 100f * 360
            paint.style = Paint.Style.STROKE
            drawArc(
                newPadding,
                newPadding,
                size - newPadding,
                size - newPadding,
                -90f,
                curNum / 100f * 360,
                false,
                paint
            )

            paint.style = Paint.Style.FILL
            curIndex(angle / 180 * PI.toFloat()) { xOffset, yOffset ->
                drawCircle(
                    size / 2f + (size / 2f - newPadding) * xOffset,
                    size / 2f + (size / 2f - newPadding) * yOffset,
                    stroke * 1.5f, paint
                )

            }
        }
    }

    private fun curIndex(radian: Float, callback: (Float, Float) -> Unit) {
        callback(sin(radian), cos(radian))
    }

}