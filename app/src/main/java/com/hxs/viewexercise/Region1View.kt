package com.hxs.viewexercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class Region1View:View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint().apply{
        color = Color.DKGRAY
        flags = Paint.ANTI_ALIAS_FLAG
    }





    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {

        }
    }
}