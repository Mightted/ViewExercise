package com.hxs.viewexercise

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.*


/**
 * Time: 2020/7/27
 * Author: Mightted
 * Description: 优化过的指示器，移除掉通过MetaBall算法逐像素绘制的性能低下的方式
 * 改用贝塞尔拟合的方式
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
        color = Color.GREEN
        style = Paint.Style.FILL_AND_STROKE
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val count = 3f
    private var currentIndex = 0f
    private val points = ArrayList<Pair<Float, Float>>(8)
    private val translatePoint = FloatArray(2)
    private val centerLinePoint = FloatArray(2)
    private var focusRadius = VALUE_FOCUS_RADIUS + VALUE_NORMAL_RADIUS

    private val srcPoint1 = FloatArray(2)
    private val srcPoint2 = FloatArray(2)
    private val dstPoint1 = FloatArray(2)
    private val dstPoint2 = FloatArray(2)
    /**
     * 回弹下标，确定当前拖拽点是否与定点连接和与哪个点连着
     */
    private var reboundIndex = currentIndex

    /**
     * 定点的圆点之间的距离
     */
    private var interval = 200f

    private val path = Path()
    private val dragPath = Path()

    init {

        setLayerType(LAYER_TYPE_HARDWARE, null)
        post {
            initData()
            translatePoint[0] = points[0].first
            translatePoint[1] = points[0].second
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
        if (points.size == 0) {
            initData()
        }

        if (points.size == 0) {
            return
        }

        super.onDraw(canvas)

        canvas?.run {
            path.reset()
            var index = 0f
            // 绘制拖拽中的圆
            BezierUtil.addPath(path, translatePoint[0], translatePoint[1], focusRadius)
            points.forEach {
                // 拖拽圆两边的圆的半径
                val ratio = abs(currentIndex - index).coerceIn(0f, 1f)
                val radius = VALUE_NORMAL_RADIUS + (1 - 2.5f * ratio) * VALUE_FOCUS_RADIUS

                // 如果存在连接的点，就绘制连接部分
                if (reboundIndex == index) {
                    BezierUtil.addPath(path, it.first, it.second, radius)
                    updateCenterLinePoint(it, radius, translatePoint, focusRadius)
                    updateRadian(it, radius, translatePoint, focusRadius, centerLinePoint)
                    dragPath.run {
                        reset()
                        moveTo(srcPoint1[0], srcPoint1[1])
                        quadTo(centerLinePoint[0], centerLinePoint[1], dstPoint1[0], dstPoint1[1])
                        lineTo(dstPoint2[0], dstPoint2[1])
                        quadTo(centerLinePoint[0], centerLinePoint[1], srcPoint2[0], srcPoint2[1])
                        close()
                    }
                    path.addPath(dragPath)
                } else {
                    BezierUtil.addPath(path, it.first, it.second, VALUE_NORMAL_RADIUS)
                }
                index++
            }
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
        val baseRadian = atan2((point2[1] - point1.second).toDouble(), (point2[0] - point1.first).toDouble())

        // 两圆中线指向原圆的切线形成的弧度
        val srcRadian = acos((radius1) / Geometry.length(point1, centerLinePoint).toDouble())


        (baseRadian - srcRadian).let { radian ->
            srcPoint1[0] = point1.first + cos(radian).toFloat() * (radius1)
            srcPoint1[1] = point1.second + sin(radian).toFloat() * (radius1)
        }

        (baseRadian + srcRadian).let { radian ->
            srcPoint2[0] = point1.first + cos(radian).toFloat() * (radius1)
            srcPoint2[1] = point1.second + sin(radian).toFloat() * (radius1)
        }


        // 两圆中线指向拖拽圆的切线形成的弧度
        val dstRadian = acos((radius2) / Geometry.length(centerLinePoint, point2).toDouble())


        (baseRadian + PI + dstRadian).let { radian ->
            dstPoint1[0] = point2[0] + cos(radian).toFloat() * (radius2)
            dstPoint1[1] = point2[1] + sin(radian).toFloat() * (radius2)
        }

        (baseRadian + PI - dstRadian).let { radian ->
            dstPoint2[0] = point2[0] + cos(radian).toFloat() * (radius2)
            dstPoint2[1] = point2[1] + sin(radian).toFloat() * (radius2)
        }
    }


    fun drag(process: Int) {
        updateIndex((count - 1) * process / 100f)
    }

    private fun updateIndex(index: Float) {
        currentIndex = index
        if (reboundIndex != -1f) {
            if (abs(currentIndex - reboundIndex) > 0.6f) {
                reboundIndex = -1f
            }
        } else {
            if (abs(currentIndex - round(currentIndex)) < 0.25f) {
                reboundIndex = round(currentIndex)
            }
        }
        translatePoint[0] = points[0].first + currentIndex * interval
        invalidate()
    }

    fun reboundIndex(): Float {
        val newIndex = if (reboundIndex != -1f) reboundIndex else round(currentIndex)
        ValueAnimator.ofFloat(currentIndex, newIndex).run {
            addUpdateListener {
                updateIndex(it.animatedValue as Float)
            }
            start()
        }
        return newIndex / (count - 1)
    }


}