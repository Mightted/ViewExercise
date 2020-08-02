package com.hxs.viewexercise

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.random.Random

/**
 * Time: 2020/8/1
 * Author: Mightted
 * Description: 会动的波浪view
 */
class WaveView: View {

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
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        flags = Paint.ANTI_ALIAS_FLAG
    }

    private val waves = mutableListOf<Pair<Float, Float>>()
    private val shadowWaves = mutableListOf<Pair<Float, Float>>()


    private val path = Path()
    private val shadowPath = Path()
    private var translateX = 0f
    private var shadowTrans = 0f
    private val horizontalLineHeight = 400f

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        for (index in 0..8) {
            waves.add(
                Pair(getRandomValue(200, 50), getRandomValue(50, 20))
            )
        }

        for (index in 0..8) {
            shadowWaves.add(
                Pair(getRandomValue(200, 80), getRandomValue(50, 10))
            )
        }
    }


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.run {
            path.reset()
            shadowPath.reset()

            // 水平位移的长度已经大于第一个波浪的宽，也就是说第一个波浪已经完全在屏幕外了，需要移除
            if (translateX > waves[0].first) {
                translateX -= waves[0].first
                waves.removeAt(0)
                waves.add(
                    Pair(getRandomValue(200, 50), getRandomValue(50, 20))
                )
            }

            if (shadowTrans > shadowWaves[0].first) {
                shadowTrans -= shadowWaves[0].first
                shadowWaves.removeAt(0)
                shadowWaves.add(
                    Pair(getRandomValue(200, 80), getRandomValue(50, 10))
                )
            }

            shadowPath.moveTo(-shadowTrans, measuredHeight - horizontalLineHeight)
            shadowWaves.forEach {
                shadowPath.rQuadTo(it.first / 4, -it.second, it.first / 2, 0f)
                shadowPath.rQuadTo(it.first / 4, it.second, it.first / 2, 0f)
            }

            shadowPath.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
            shadowPath.lineTo(0f, measuredHeight.toFloat())
            shadowPath.close()



            path.moveTo(-translateX, measuredHeight - horizontalLineHeight)
            waves.forEach {
                path.rQuadTo(it.first / 4, -it.second, it.first / 2, 0f)
                path.rQuadTo(it.first / 4, it.second, it.first / 2, 0f)
            }

            path.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
            path.lineTo(0f, measuredHeight.toFloat())
            path.close()
            paint.color = (0x9903A9F4).toInt()
            drawPath(shadowPath, paint)
            paint.color = (0xFF03A9F4).toInt()
            drawPath(path, paint)
        }
    }

    fun animator() {
        val animator = ValueAnimator.ofInt(1, 60)
        animator.duration = 1000
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener {
            translateX += 5f
            shadowTrans += 2f
            invalidate()
        }
        animator.start()
    }

    private fun getRandomValue(rawValue: Int, range: Int): Float {
        return rawValue + range - Random.nextInt(range * 2).toFloat()
    }

}