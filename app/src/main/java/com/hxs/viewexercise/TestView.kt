package com.hxs.viewexercise

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlin.math.*


/**
 * Time: 2020/7/27
 * Author: Mightted
 * Description:
 */

private const val VALUE_NORMAL_RADIUS = 10f
private const val VALUE_FOCUS_RADIUS = 10f
private const val VALUE_PAINT_WIDTH = 5f

class TestView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private var paint: Paint = Paint().apply {

        strokeWidth = VALUE_PAINT_WIDTH
        strokeCap = Paint.Cap.ROUND
        color = Color.GREEN
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val reboundAnim = ValueAnimator.ofFloat(0f, 1f).apply {
        interpolator = OvershootInterpolator()
        addUpdateListener {
//            translatePoint[0] = upPoint[0] + (centerPoint[0] - upPoint[0]) * it.animatedValue as Float
//            translatePoint[1] = upPoint[1] + (centerPoint[1] - upPoint[1]) * it.animatedValue as Float
            println("${translatePoint[0]}:${translatePoint[1]}")
            invalidate()
        }
    }

//    private val centerPoint: FloatArray
//        get() = floatArrayOf(
//            measuredWidth / 2f + 150f,
//            measuredHeight / 2f
//        )

    private val count = 3f
    private var currentIndex = 0f
    private val points = ArrayList<Pair<Float, Float>>(8)
    private val translatePoint = FloatArray(2)
    private val centerLinePoint = FloatArray(2)
    private var radius = 0f
    private var centerRadius = VALUE_FOCUS_RADIUS + VALUE_NORMAL_RADIUS
    private val srcPoint1 = FloatArray(2)
    private val srcPoint2 = FloatArray(2)
    private val dstPoint1 = FloatArray(2)
    private val dstPoint2 = FloatArray(2)
    private val upPoint = FloatArray(2)
    private val pressedPoint = FloatArray(2)

    //    private var isDrag = false // 是否拖动圆
    private var interval = 200f

    private val path = Path()
    private val dragPath = Path()

    init {

        setLayerType(LAYER_TYPE_HARDWARE, null)
        post {
            initData()
            translatePoint[0] = points[0].first
            translatePoint[1] = points[0].second
//            translatePoint[0] = centerPoint[0]
//            translatePoint[1] = centerPoint[1]
            invalidate()
        }
    }

    private fun initData() {
        points.clear()
        val initOffset = measuredWidth / 2f - (count - 1) * interval / 2
        for (index in 0 until count.toInt()) {
            points.add(Pair(initOffset + index * interval, measuredHeight / 2f))
        }
    }


    override fun onDraw(canvas: Canvas?) {
//        translatePoint[0] = measuredWidth / 2f - 40f
//        translatePoint[1] = measuredHeight / 2f
        initData()

        if (points.size == 0) {
            return
        }

        super.onDraw(canvas)

        canvas?.run {
            path.reset()
            // 绘制固定点部分的圆

            var index = 0f
            BezierUtil.addPath(path, translatePoint[0], translatePoint[1], VALUE_FOCUS_RADIUS + VALUE_NORMAL_RADIUS)
            points.forEach {
                val ratio = abs(currentIndex - index).coerceIn(0f, 1f)
                val radius = VALUE_NORMAL_RADIUS + (1 - 2.5f * ratio) * VALUE_FOCUS_RADIUS

                if (ratio < 1 && Geometry.length(it, translatePoint) < interval * 0.45) {
                    BezierUtil.addPath(path, it.first, it.second, radius)
                    updateCenterLinePoint(it, radius, translatePoint, VALUE_FOCUS_RADIUS + VALUE_NORMAL_RADIUS)
                    updateRadian(it, radius, translatePoint, VALUE_FOCUS_RADIUS + VALUE_NORMAL_RADIUS, centerLinePoint)
                    dragPath.run {
                        reset()
                        moveTo(srcPoint1[0], srcPoint1[1])
                        quadTo(centerLinePoint[0], centerLinePoint[1], dstPoint1[0], dstPoint1[1])
                        lineTo(dstPoint2[0], dstPoint2[1])
                        quadTo(centerLinePoint[0], centerLinePoint[1], srcPoint2[0], srcPoint2[1])
                        close()
                    }
                    path.addPath(dragPath)
                    drawSupportLine(this)
                } else {
                    BezierUtil.addPath(path, it.first, it.second, VALUE_NORMAL_RADIUS)
                }


//                if (currentIndex == index) {
//                    BezierUtil.addPath(path, it.first, it.second, VALUE_FOCUS_RADIUS)
//                } else {
//                    BezierUtil.addPath(path, it.first, it.second, VALUE_NORMAL_RADIUS)
//                }

                index++
            }


//            val betweenLength = Geometry.length(centerPoint, translatePoint, 0, 0)
//            centerRadius = (60f - betweenLength / 5).coerceAtLeast(VALUE_RAW_RADIUS)
//            radius = (betweenLength / 3).coerceAtMost(VALUE_DRAG_RADIUS)
//            BezierUtil.addPath(path, centerPoint[0], centerPoint[1], centerRadius)
//
//
//            // 绘制拖拽部分的圆
//            BezierUtil.addPath(path, translatePoint[0], translatePoint[1], radius)
//
//            updateCenterLinePoint()
//            updateRadian()
//
//            // 绘制中间拉伸的部分
//            dragPath.run {
//                reset()
//                moveTo(srcPoint1[0], srcPoint1[1])
//                quadTo(centerLinePoint[0], centerLinePoint[1], dstPoint1[0], dstPoint1[1])
//                lineTo(dstPoint2[0], dstPoint2[1])
//                quadTo(centerLinePoint[0], centerLinePoint[1], srcPoint2[0], srcPoint2[1])
//                close()
//            }
//            path.addPath(dragPath)
//            drawSupportLine(this)


            paint.color = Color.GREEN
            drawPath(path, paint)
        }
    }

    /**
     * 更新中点位置
     * 中点并不总是在两圆点之间，半径的不同会有差异
     */
    private fun updateCenterLinePoint(point1: Pair<Float, Float>, radius1: Float, point2: FloatArray, radius2: Float) {
        val ratio = (Geometry.length(point1, point2)).let { length ->
            ((length - radius1 - radius2) / 2 + radius1) / length
        }
        centerLinePoint[0] = point1.first + (point2[0] - point1.first) * ratio
        centerLinePoint[1] = point1.second + (point2[1] - point1.second) * ratio
    }

    /**
     * 更新拉伸部分与两个圆的连接点
     * 选择合适的点很重要，否则线会跟圆内重合
     */
    private fun updateRadian(
        point1: Pair<Float, Float>,
        radius1: Float,
        point2: FloatArray,
        radius2: Float,
        centerLinePoint: FloatArray
    ) {

        // 指向原圆的弧度
        val baseRadian =
            atan2(
                (point2[1] - point1.second).toDouble(),
                (point2[0] - point1.first).toDouble()
            )

        // 两圆中线指向原圆的切线形成的弧度
        val srcRadian =
            acos((radius1) / Geometry.length(point1, centerLinePoint).toDouble())


        (baseRadian - srcRadian).let { radian ->
            srcPoint1[0] = point1.first + cos(radian).toFloat() * (radius1)
            srcPoint1[1] = point1.second + sin(radian).toFloat() * (radius1)
        }

        (baseRadian + srcRadian).let { radian ->
            srcPoint2[0] = point1.first + cos(radian).toFloat() * (radius1)
            srcPoint2[1] = point1.second + sin(radian).toFloat() * (radius1)
        }


        // 两圆中线指向拖拽圆的切线形成的弧度
        val dstRadian = acos((radius) / Geometry.length(centerLinePoint, point2).toDouble())


        (baseRadian + PI + dstRadian).let { radian ->
            dstPoint1[0] = point2[0] + cos(radian).toFloat() * (radius2)
            dstPoint1[1] = point2[1] + sin(radian).toFloat() * (radius2)
        }

        (baseRadian + PI - dstRadian).let { radian ->
            dstPoint2[0] = point2[0] + cos(radian).toFloat() * (radius2)
            dstPoint2[1] = point2[1] + sin(radian).toFloat() * (radius2)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInCircle(event.x, event.y)) {
                    pressedPoint[0] = event.x
                    pressedPoint[1] = event.y
                    true
                } else {
                    super.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {

//                translatePoint[0] = centerPoint[0] + event.x - pressedPoint[0]
//                translatePoint[1] = centerPoint[1] + event.y - pressedPoint[1]
                invalidate()
                true
            }

            MotionEvent.ACTION_UP -> {
                upPoint[0] = translatePoint[0]
                upPoint[1] = translatePoint[1]
                reboundAnim.start()
                super.onTouchEvent(event)
            }
            else -> {
                super.onTouchEvent(event)
            }
        }
    }


    fun drag(process: Int) {
        currentIndex = (count - 1) * process / 100f
        translatePoint[0] = points[0].first + currentIndex * interval
        invalidate()
    }

    /**
     * 判断点击位置是否在圆内，外部的地方消化点击事件
     */
    private fun isInCircle(x: Float, y: Float): Boolean {
        return true
//        return Geometry.length(x - centerPoint[0], y - centerPoint[1]) <= radius
    }

    /**
     * 辅助线，调试用
     */
    private fun drawSupportLine(canvas: Canvas?) {
        canvas?.run {
            paint.color = Color.RED
            drawPoints(srcPoint1, paint)
            drawPoints(dstPoint1, paint)
//            drawLine(
//                centerPoint[0],
//                centerPoint[1],
//                translatePoint[0],
//                translatePoint[1],
//                paint
//            )
            drawLine(srcPoint1[0], srcPoint1[1], centerLinePoint[0], centerLinePoint[1], paint)
            drawLine(dstPoint1[0], dstPoint1[1], centerLinePoint[0], centerLinePoint[1], paint)
            drawLine(srcPoint2[0], srcPoint2[1], centerLinePoint[0], centerLinePoint[1], paint)
            drawLine(dstPoint2[0], dstPoint2[1], centerLinePoint[0], centerLinePoint[1], paint)
        }
    }


}