package com.hxs.viewexercise

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener

/**
 * Time: 2020/8/1
 * Author: Mightted
 * Description:
 */
class NightSkyView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var nightSky: NightSky? = null

    private var bgBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_night_sky)
    private val dstRect: Rect = Rect()
    private lateinit var animator: ValueAnimator


    private val paint = Paint().apply {
        color = Color.WHITE
        flags = Paint.ANTI_ALIAS_FLAG
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dstRect.set(0, 0, width, height)
        canvas?.drawBitmap(bgBitmap, null, dstRect, paint)


        nightSky?.onDraw(canvas)

    }


    // 绘制✨图案
    private fun getStarPath(): Path {
        val path = Path()
        path.quadTo(8f, 8f, 16f, 0f)
        path.quadTo(8f, 8f, 16f, 16f)
        path.quadTo(8f, 8f, 0f, 16f)
        path.quadTo(8f, 8f, 0f, 0f)
        return path
    }


    private fun animator(): ValueAnimator {
        return ValueAnimator.ofInt(0, 999).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE

            var frame = 0
            val frameUnit = 1000 / 60f

            addUpdateListener {
                invalidate()
                if (it.animatedValue as Int / frameUnit > frame) {
                    frame++
                    invalidate()
                } else {
                    println("${it.animatedValue as Int / frameUnit} : $frame")
                }
            }

            addListener(onRepeat = {
                frame = 0
                nightSky?.addMeteor()
            })
        }
    }


    fun play() {
        post {
            nightSky = NightSky(width.toFloat(), height * 0.5f)
            nightSky?.initData()
            animator = animator()
            animator.start()
        }
    }

    fun resume() {
        if (nightSky != null) {
            animator.resume()
        }

    }

    fun pause() {
        if (nightSky != null) {
            animator.pause()
        }
    }
}


