package com.hxs.viewexercise

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

// 具有横向开屏效果的ImageView
class Region1View : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint().apply {
        color = Color.DKGRAY
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val progressPath = Path()
    private var process = 0.2f
    private var index = 0
    private val unitHeight = 40f
    private var color = Color.CYAN


    override fun onDraw(canvas: Canvas?) {

        canvas?.run {
            println(process)
            index = 0
            while (index * unitHeight < measuredHeight) {
                if (index % 2 == 0) {
                    progressPath.addRect(
                        0f,
                        index * unitHeight,
                        measuredWidth * process,
                        (index + 1) * unitHeight,
                        Path.Direction.CW
                    )
                } else {
                    progressPath.addRect(
                        (1 - process) * measuredWidth,
                        index * unitHeight,
                        measuredWidth.toFloat(),
                        (index + 1) * unitHeight,
                        Path.Direction.CW
                    )
                }
                index++
            }
//            drawColor(Color.CYAN)

            clipPath(progressPath)
//            drawColor(color)
//            drawable.draw(this)
        }
        super.onDraw(canvas)
    }

    fun animate(duration: Long) {
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            addUpdateListener {
                process = it.animatedValue as Float
                invalidate()
            }
        }
        animator.start()
    }

    fun changeColor(color: Int) {
        this.color = color
        process = 0.9f
        invalidate()
    }

}