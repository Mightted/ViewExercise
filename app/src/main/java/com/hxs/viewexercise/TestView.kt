package com.hxs.viewexercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

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
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val path = Path()
    private val path1 = Path()
    private val path2 = Path()
    private val path3 = Path()
    private val path4 = Path()

    private val padding: Float = 200f


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            drawColor(Color.BLUE)
            translate(measuredWidth / 2f, measuredHeight / 2f)
            clipRect(-400f, -400f, 400f, 400f)

            save()
            drawColor(Color.CYAN)
//            scale(0.5f, 0.5f)
//            drawRect(-400f, -400f, 400f, 400f, paint)

        }


    }
}