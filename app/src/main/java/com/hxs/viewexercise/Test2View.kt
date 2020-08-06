package com.hxs.viewexercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Region
import android.util.AttributeSet
import android.view.View

/**
 * Time: 2020/8/6
 * Author: Mightted
 * Description:
 */
class Test2View : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val srcPoint = FloatArray(2)
    private val dstPoint = FloatArray(2)
    private val srcRadius = 100f
    private val dstRadius = 100f
    private val circleRect = IntArray(4)
    val region = Region()

    private val paint = Paint().apply {
        strokeWidth = 1f
        color = Color.BLUE
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        srcPoint[0] = measuredWidth / 2 - 100f
        srcPoint[1] = measuredHeight / 2f
        dstPoint[0] = measuredWidth / 2 + 100f
        dstPoint[1] = measuredHeight / 2f


        canvas?.run {
            updateRect()

            paint.color = Color.WHITE
            drawRect(
                circleRect[0].toFloat(),
                circleRect[1].toFloat(),
                circleRect[2].toFloat(),
                circleRect[3].toFloat(),
                paint
            )
            paint.color = Color.BLUE
            for (row in circleRect[1]..circleRect[3]) {
                for (column in circleRect[0]..circleRect[2]) {
                    val threshold = BezierUtil.metaBall(srcPoint, column.toFloat(), row.toFloat(), srcRadius) +
                            BezierUtil.metaBall(dstPoint, column.toFloat(), row.toFloat(), dstRadius)
                    if (threshold >= 1.6f) {
                        drawPoint(column.toFloat(), row.toFloat(), paint)
                    }
                }
            }

        }

    }


    private fun updateRect() {
        circleRect[0] = ((srcPoint[0] - srcRadius).coerceAtMost(dstPoint[0] - dstRadius)).toInt()
        circleRect[1] = ((srcPoint[1] - srcRadius).coerceAtMost(dstPoint[1] - dstRadius)).toInt()
        circleRect[2] = ((srcPoint[0] + srcRadius).coerceAtLeast(dstPoint[0] + dstRadius)).toInt()
        circleRect[3] = ((srcPoint[1] + srcRadius).coerceAtLeast(dstPoint[1] + dstRadius)).toInt()
    }
}
