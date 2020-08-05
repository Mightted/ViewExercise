package com.hxs.viewexercise

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI
import kotlin.math.asin


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
        pathEffect = CornerPathEffect(50f)
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val centerPoint: FloatArray
        get() = floatArrayOf(
            measuredWidth / 2f,
            measuredHeight / 2f
        )

    private val translatePoint = FloatArray(2)
    private val centerLinePoint = FloatArray(2)
    private val radius = 100f
    private val srcPoint1 = FloatArray(2)
    private val srcPoint2 = FloatArray(2)
    private val dstPoint1 = FloatArray(2)
    private val dstPoint2 = FloatArray(2)
    private val radians = DoubleArray(2)
    private var isDrag = false

    private val path = Path()
    private val srcPath = Path()
    private val srcMatrix = Matrix()

    init {

        setLayerType(LAYER_TYPE_HARDWARE, null)
        post {
            translatePoint[0] = centerPoint[0]
            translatePoint[1] = centerPoint[1]
//            translateX = centerX
//            translateY = centerY
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.run {

            path.reset()
            if (!isDrag) {
                BezierUtil.addPath(path, centerPoint[0], centerPoint[1], radius)
            } else {
                //                path2.reset()
                srcPath.reset()
                srcMatrix.reset()
                BezierUtil.addPath(
                    srcPath,
                    centerPoint[0],
                    centerPoint[1],
                    radius,
                    Geometry.length(centerPoint, translatePoint)
                )
                val degress:Float = (Math.atan2(
                    (translatePoint[1] - centerPoint[1]).toDouble(),
                    (translatePoint[0] - centerPoint[0]).toDouble()
                ) / PI * 180f).toFloat()

                srcMatrix.setRotate(degress, centerPoint[0], centerPoint[1])
                path.addPath(srcPath, srcMatrix)

//                path.op(path2, Path.Op.UNION)

                centerLinePoint[0] = (centerPoint[0] + translatePoint[0]) / 2
                centerLinePoint[1] = (centerPoint[1] + translatePoint[1]) / 2
//                val radian =
//                    Math.asin(
//                        radius / (Math.sqrt(
//                            Math.pow((translatePoint[1] - centerPoint[1]).toDouble(), 2.0) +
//                                    Math.pow((translatePoint[0] - centerPoint[0]).toDouble(), 2.0)
//                        ))
//                    )
//                val radian =
//                    Math.atan2(
//                        (translatePoint[1] - centerPoint[1]).toDouble(),
//                        (translatePoint[0] - centerPoint[0]).toDouble()
//                    )

                updateRadian(radians)
                srcPoint1[0] = centerPoint[0] + Math.cos(radians[0]).toFloat() * radius
                srcPoint1[1] = centerPoint[1] - Math.sin(radians[0]).toFloat() * radius
                srcPoint2[0] = centerPoint[0] - Math.sin(radians[1]).toFloat() * radius
                srcPoint2[1] = centerPoint[1] + Math.cos(radians[1]).toFloat() * radius

                dstPoint1[0] = translatePoint[0] + Math.sin(radians[1]).toFloat() * radius
                dstPoint1[1] = translatePoint[1] - Math.cos(radians[1]).toFloat() * radius
                dstPoint2[0] = translatePoint[0] - Math.cos(radians[0]).toFloat() * radius
                dstPoint2[1] = translatePoint[1] + Math.sin(radians[0]).toFloat() * radius

//                path.moveTo(srcPoint1[0], srcPoint1[1])
//                path.quadTo(centerLinePoint[0], centerLinePoint[1], dstPoint1[0], dstPoint1[1])
//                path.moveTo(srcPoint2[0], srcPoint2[1])
//                path.quadTo(centerLinePoint[0], centerLinePoint[1], dstPoint2[0], dstPoint2[1])


                paint.color = Color.CYAN
                drawPoints(srcPoint1, paint)
                drawPoints(dstPoint1, paint)
//                path.lineTo(translateX, translateY)
            }
            paint.color = Color.GREEN
            drawPath(path, paint)

        }
    }


    private fun updateRadian(radian: DoubleArray) {
        val radian1 = asin(radius / Geometry.length(centerPoint, translatePoint).toDouble())
        //                    (Math.sqrt(
//                    Math.pow((translatePoint[1] - centerPoint[1]).toDouble(), 2.0) +
//                            Math.pow((translatePoint[0] - centerPoint[0]).toDouble(), 2.0)
//                ) / 2)
        val radian2 =
            Math.atan2(
                (translatePoint[1] - centerPoint[1]).toDouble(),
                (translatePoint[0] - centerPoint[0]).toDouble()
            )
        radian[0] = (radian1 - radian2)
        radian[1] = (radian1 - PI / 2 + radian2)


    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInCircle(event.x, event.y)) {
                    true
                } else {
                    super.onTouchEvent(event)
                }

            }
            MotionEvent.ACTION_MOVE -> {
                isDrag = !isInCircle(event.x, event.y)
                translatePoint[0] = event.x
                translatePoint[1] = event.y
                invalidate()
                true
            }

            MotionEvent.ACTION_UP -> {
                isDrag = false
                translatePoint[0] = centerPoint[0]
                translatePoint[1] = centerPoint[1]
                invalidate()
                super.onTouchEvent(event)
            }
            else -> {
                super.onTouchEvent(event)
            }
        }
    }

//    private fun getLength(src: FloatArray, dst: FloatArray): Float {
//        return sqrt(
//            (dst[1] - src[1]).toDouble().pow(2.0) + (dst[0] - src[0]).toDouble().pow(2.0)
//        ).toFloat()
//    }

    private fun isInCircle(x: Float, y: Float): Boolean {
        return x in centerPoint[0].let { it - radius - paint.strokeWidth..it + radius + paint.strokeWidth }
                && y in centerPoint[1].let { it - radius - paint.strokeWidth..it + radius + paint.strokeWidth }
    }


}