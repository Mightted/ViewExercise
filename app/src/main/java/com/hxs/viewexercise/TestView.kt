package com.hxs.viewexercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Time: 2020/7/27
 * Author: Mightted
 * Description:
 */
class TestView:View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var paint :Paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 20f
        style = Paint.Style.STROKE

    }

    private val padding :Float = 200f


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {

            drawColor(Color.BLUE)
            drawRoundRect(padding, padding, width - padding, height - padding,78f, 148f, paint)
        }


    }
}