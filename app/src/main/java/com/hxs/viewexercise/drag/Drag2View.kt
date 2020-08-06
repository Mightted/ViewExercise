package com.hxs.viewexercise.drag

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import com.hxs.viewexercise.BezierUtil
import com.hxs.viewexercise.Geometry
import kotlin.math.*

/**
 * Time: 2020/8/5
 * Author: Mightted
 * Description: 红点拖拽效果view
 */
class Drag2View :View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val VALUE_RAW_RADIUS = 80f
    private val VALUE_DRAG_RADIUS = 20f
    private val VALUE_PAINT_WIDTH = 5f

    private var paint: Paint = Paint().apply {

        strokeWidth = VALUE_PAINT_WIDTH
        strokeCap = Paint.Cap.ROUND
        color = Color.GREEN
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val reboundAnim = ValueAnimator.ofFloat(0f,1f).apply {
        interpolator = OvershootInterpolator()
        addUpdateListener {
            translatePoint[0] = upPoint[0] + (centerPoint[0] - upPoint[0]) * it.animatedValue as Float
            translatePoint[1] = upPoint[1] + (centerPoint[1] - upPoint[1]) * it.animatedValue as Float
            println("${translatePoint[0]}:${translatePoint[1]}")
            invalidate()
        }
        addListener {
            isDrag = false
        }
    }

    private val centerPoint: FloatArray
        get() = floatArrayOf(
            measuredWidth / 2f,
            measuredHeight / 2f
        )

    private val translatePoint = FloatArray(2)
    private val centerLinePoint = FloatArray(2)
    private val radius = VALUE_RAW_RADIUS
    private var centerRadius = VALUE_DRAG_RADIUS
    private val srcPoint1 = FloatArray(2)
    private val srcPoint2 = FloatArray(2)
    private val dstPoint1 = FloatArray(2)
    private val dstPoint2 = FloatArray(2)
    private val upPoint = FloatArray(2)
    private val pressedPoint = FloatArray(2)
    private var isDrag = false // 是否拖动圆

    private val path = Path()
    private val dragPath = Path()

    init {

        setLayerType(LAYER_TYPE_HARDWARE, null)
        post {
            translatePoint[0] = centerPoint[0]
            translatePoint[1] = centerPoint[1]
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)

        canvas?.run {
            path.reset()
            // 绘制固定点部分的圆
            BezierUtil.addPath(path, centerPoint[0], centerPoint[1], centerRadius)
            if (isDrag) {
                // 绘制拖拽部分的圆
                BezierUtil.addPath(path, translatePoint[0], translatePoint[1], radius)

                updateCenterLinePoint()
                updateRadian()

                // 绘制中间拉伸的部分
                dragPath.run {
                    reset()
                    moveTo(srcPoint1[0], srcPoint1[1])
                    quadTo(centerLinePoint[0], centerLinePoint[1], dstPoint1[0], dstPoint1[1])
                    lineTo(dstPoint2[0], dstPoint2[1])
                    quadTo(centerLinePoint[0], centerLinePoint[1], srcPoint2[0], srcPoint2[1])
                    close()
                }
                path.addPath(dragPath)
//                drawSupportLine(this)
            }

            paint.color = Color.GREEN
            drawPath(path, paint)
        }
    }

    /**
     * 更新中点位置
     * 中点并不总是在两圆点之间，半径的不同会有差异
     */
    private fun updateCenterLinePoint() {
        val ratio = (Geometry.length(centerPoint, translatePoint)).let { length ->
            ((length - radius - centerRadius) / 2 + centerRadius) / length
        }
        centerLinePoint[0] = centerPoint[0] + (translatePoint[0] - centerPoint[0]) * ratio
        centerLinePoint[1] = centerPoint[1] + (translatePoint[1] - centerPoint[1]) * ratio
    }

    /**
     * 更新拉伸部分与两个圆的连接点
     * 选择合适的点很重要，否则线会跟圆内重合
     */
    private fun updateRadian() {

        // 指向原圆的弧度
        val baseRadian =
            atan2(
                (translatePoint[1] - centerPoint[1]).toDouble(),
                (translatePoint[0] - centerPoint[0]).toDouble()
            )

        // 两圆中线指向原圆的切线形成的弧度
        val srcRadian =
            acos((centerRadius) / Geometry.length(centerPoint, centerLinePoint).toDouble())


        (baseRadian - srcRadian).let { radian ->
            srcPoint1[0] = centerPoint[0] + cos(radian).toFloat() * (centerRadius)
            srcPoint1[1] = centerPoint[1] + sin(radian).toFloat() * (centerRadius)
        }

        (baseRadian + srcRadian).let { radian ->
            srcPoint2[0] = centerPoint[0] + cos(radian).toFloat() * (centerRadius)
            srcPoint2[1] = centerPoint[1] + sin(radian).toFloat() * (centerRadius)
        }


        // 两圆中线指向拖拽圆的切线形成的弧度
        val dstRadian = acos(
            (radius) / Geometry.length(centerLinePoint, translatePoint).toDouble())


        (baseRadian + PI + dstRadian).let { radian ->
            dstPoint1[0] = translatePoint[0] + cos(radian).toFloat() * (radius)
            dstPoint1[1] = translatePoint[1] + sin(radian).toFloat() * (radius)
        }

        (baseRadian + PI - dstRadian).let { radian ->
            dstPoint2[0] = translatePoint[0] + cos(radian).toFloat() * (radius)
            dstPoint2[1] = translatePoint[1] + sin(radian).toFloat() * (radius)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInCircle(event.x, event.y)) {
                    isDrag = true
                    pressedPoint[0] = event.x
                    pressedPoint[1] = event.y
                    true
                } else {
                    super.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {

                translatePoint[0] = centerPoint[0] + event.x - pressedPoint[0]
                translatePoint[1] = centerPoint[1] + event.y - pressedPoint[1]
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

    /**
     * 判断点击位置是否在圆内，外部的地方消化点击事件
     */
    private fun isInCircle(x: Float, y: Float): Boolean {
        return Geometry.length(x - centerPoint[0], y - centerPoint[1]) <= radius
    }

    /**
     * 辅助线，调试用
     */
    private fun drawSupportLine(canvas: Canvas?) {
        canvas?.run {
            paint.color = Color.CYAN
            drawPoints(srcPoint1, paint)
            drawPoints(dstPoint1, paint)
            drawLine(
                centerPoint[0],
                centerPoint[1],
                translatePoint[0],
                translatePoint[1],
                paint
            )
            drawLine(srcPoint1[0], srcPoint1[1], centerLinePoint[0], centerLinePoint[1], paint)
            drawLine(dstPoint1[0], dstPoint1[1], centerLinePoint[0], centerLinePoint[1], paint)
            drawLine(srcPoint2[0], srcPoint2[1], centerLinePoint[0], centerLinePoint[1], paint)
            drawLine(dstPoint2[0], dstPoint2[1], centerLinePoint[0], centerLinePoint[1], paint)
        }
    }


}