package com.hxs.viewexercise

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class TemperatureView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val radius = DensityUtil.dip2px(context, 100f)
    private val lineWidth = DensityUtil.dip2px(context, 30f)
    private val COLOR_RING = Color.parseColor("#FFFF871A")

    private lateinit var rectF: RectF
    private lateinit var gradient: SweepGradient
    private var progress = 0.6f

    private val paint = Paint().apply {
        color = Color.WHITE
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
    }


    private fun doInit() {
        rectF = RectF(
            width * 0.5f - radius,
            height * 0.5f - radius,
            width * 0.5f + radius,
            height * 0.5f + radius
        )




        setProgress(0.5f)
//        paint.shader = gradient
    }

    fun setProgress(value: Float) {


        val actualValue = value.coerceIn(0f, 1f)

        progress = actualValue - 0.02f

        val ringValue = actualValue * 0.8f + 0.02f
//        toTrueProgress(actualValue)


//        progress = p

        gradient = SweepGradient(
            width / 2f,
            height / 2f,
            intArrayOf(
                Color.WHITE,
                Color.WHITE,
                COLOR_RING,
                Color.WHITE,
                Color.WHITE
            ),
            floatArrayOf(0f, 0.001f, ringValue, ringValue + 0.001f, 1f)
        )

        val matrix = Matrix()
        matrix.postRotate(126f, width * 0.5f, height * 0.5f)
        gradient.setLocalMatrix(matrix)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        doInit()

        canvas?.run {
            drawColor(Color.GREEN)
            paint.shader = null
            drawArc(rectF, 126f, 288f, false, paint)

            paint.shader = gradient
//            paint.color = Color.RED
            drawArc(rectF, 126f, 288f * progress, false, paint)
        }


    }
}