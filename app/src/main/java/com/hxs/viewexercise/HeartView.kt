package com.hxs.viewexercise

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator

/**
 * Time: 2020/7/31
 * Author: Mightted
 * Description: 圆形变成心形的view
 */
class HeartView:View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var paint: Paint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val tagCircle = 0.55f
    private val length = 400f
    private val controlPoint = length * tagCircle
    private var controlPoints = mutableListOf<Float>()
    private var points = mutableListOf<Float>()

    private val path = Path()

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        initPoint()
    }


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.run {
            translate(measuredWidth / 2f, measuredHeight / 2f)

//            paint.style = Paint.Style.STROKE
//            drawRect(-length, -length, length, length, paint)

            paint.style = Paint.Style.FILL
            path.reset()
            path.moveTo(points[0], points[1])
            path.cubicTo(
                controlPoints[0],
                controlPoints[1],
                controlPoints[2],
                controlPoints[3],
                points[2],
                points[3]
            )
            path.cubicTo(
                controlPoints[4],
                controlPoints[5],
                controlPoints[6],
                controlPoints[7],
                points[4],
                points[5]
            )
            path.cubicTo(
                controlPoints[8],
                controlPoints[9],
                controlPoints[10],
                controlPoints[11],
                points[6],
                points[7]
            )
            path.cubicTo(
                controlPoints[12],
                controlPoints[13],
                controlPoints[14],
                controlPoints[15],
                points[0],
                points[1]
            )
            drawPath(path, paint)
        }
    }

    private fun initPoint() {
        points.clear()
        points = mutableListOf(-length, 0f, 0f, -length, length, 0f, 0f, length)
        controlPoints = mutableListOf(
            -length, -controlPoint, -controlPoint, -length,
            controlPoint, -length, length, -controlPoint,
            length, controlPoint, controlPoint, length,
            -controlPoint, length, -length, controlPoint
        )
    }


    fun toHeart() {
        initPoint()
        val animator = ValueAnimator.ofFloat(0f, 200f)
        animator.addUpdateListener {
            points[3] = -length + it.animatedValue as Float
            invalidate()
        }
        val animator2 = ValueAnimator.ofFloat(0f, 50f)
        animator2.addUpdateListener {
            points[7] = length + it.animatedValue as Float
        }
        val set = AnimatorSet()
        set.playTogether(animator, animator2)
        set.duration = 400
        set.interpolator = OvershootInterpolator()
        set.start()

    }

}