package com.hxs.viewexercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

// 八卦图
class BaguaView:View {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var paint: Paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val path1 = Path()
    private val path2 = Path()
    private val path3 = Path()
    private val path4 = Path()

    private val padding: Float = 200f


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {

            path1.addCircle(
                measuredWidth / 2f,
                measuredHeight / 2f,
                measuredHeight / 2f - padding,
                Path.Direction.CW
            )
            path2.addRect(
                measuredWidth / 2f,
                padding,
                measuredWidth - padding,
                measuredHeight - padding,
                Path.Direction.CW
            )

            path3.addCircle(
                measuredWidth / 2f,
                measuredHeight / 4f + padding / 2f,
                measuredHeight / 4f - padding / 2f,
                Path.Direction.CW
            )

            path4.addCircle(
                measuredWidth / 2f,
                measuredHeight / 4 * 3f - padding / 2f,
                measuredHeight / 4f - padding / 2f,
                Path.Direction.CW
            )

            paint.style = Paint.Style.STROKE
            drawPath(path1, paint)
            paint.style = Paint.Style.FILL

            path1.op(path2, Path.Op.DIFFERENCE)
            path1.op(path3, Path.Op.UNION)
            path1.op(path4, Path.Op.DIFFERENCE)

            drawPath(path1, paint)

        }


    }
}