package com.hxs.viewexercise

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 血氧检测view
 */
class OxygenView : View {

    private val centerRadius: Float = DensityUtil.dip2px(context, 100f)
    private val outRadius = centerRadius + DensityUtil.dip2px(context, 15f)
    private val lineWidth = DensityUtil.dip2px(context, 3f)
    private val ballRadius = DensityUtil.dip2px(context, 4f)
    private val sweepValue = 70f
    private var sweepDegree: Float = 0f
    private lateinit var rectF: RectF
    private var progress: Float = 0f
    private var ready = false

    private val bubbles = FloatArray(8)

    private val COLOR_LINE = Color.parseColor("#26FF5555") // 描线的颜色
    private val COLOR_POINT = Color.parseColor("#FFFF5555") // 各种点的颜色
    private val COLOR_BACKGROUND = Color.parseColor("#17FF5555") // 圆背景
    private val COLOR_WAVE_BACKGROUND = Color.parseColor("#4DFF5555") // 底部波浪
    private val COLOR_WAVE_FORGROUND = Color.parseColor("#99FF5555") // 前面的波浪


    private val paint = Paint().apply {
        color = Color.parseColor("#FFFEB4B4")
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
        flags = Paint.ANTI_ALIAS_FLAG
    }


    private val waves = mutableListOf<Pair<Float, Float>>()
    private val shadowWaves = mutableListOf<Pair<Float, Float>>()

    private lateinit var gradient: LinearGradient
    private lateinit var shadowGradient: LinearGradient


    private val basePath = Path()
    private val path = Path()
    private val shadowPath = Path()
    private var translateX = 0f
    private var shadowTrans = 0f

    private var startPoint = 0f
    private var endPoint = 0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    fun progressUpdate(p: Float) {
        this.progress = p % 1f
        translateX = progress * 2 * centerRadius
        shadowTrans = progress * 4 * centerRadius

        invalidate()
    }

    init {
        post {
            doInit()
            ready = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!ready) {
            return
        }

//        doInit()


        canvas?.run {
            paint.style = Paint.Style.STROKE
            paint.color = COLOR_LINE
            drawCircle(width / 2f, height / 2f, centerRadius, paint)

            // 绘制外环环绕
            for (i in 0..3) {
                sweepDegree = (i * 90f - progress * 360) % 360
                paint.style = Paint.Style.FILL
                paint.color = COLOR_POINT
                drawCircle(
                    width / 2f + cos(sweepDegree * PI.toFloat() / 180f) * outRadius,
                    height / 2f + sin(sweepDegree * PI.toFloat() / 180f) * outRadius,
                    ballRadius,
                    paint
                )

                // 外环上的点
                paint.style = Paint.Style.STROKE
                paint.color = COLOR_LINE
                drawArc(rectF, sweepDegree, sweepValue, false, paint)
            }


            // 绘制波浪
            basePath.reset()
            path.reset()
            shadowPath.reset()

            shadowPath.moveTo(startPoint - shadowTrans, height / 2f + centerRadius / 4f)
            shadowWaves.forEach {

                shadowPath.rQuadTo(it.first / 4, -it.second, it.first / 2, 0f)
                shadowPath.rQuadTo(it.first / 4, it.second, it.first / 2, 0f)
            }

            shadowPath.lineTo(startPoint - shadowTrans + centerRadius * 8, height / 2f + centerRadius)
            shadowPath.lineTo(startPoint - shadowTrans, height / 2f + centerRadius)
            shadowPath.close()



            path.moveTo(startPoint - translateX, height / 2f)
            waves.forEach {
                path.rQuadTo(it.first / 4, -it.second, it.first / 2, 0f)
                path.rQuadTo(it.first / 4, it.second, it.first / 2, 0f)
            }


            path.lineTo(startPoint - translateX + centerRadius * 4, height / 2f + centerRadius)
            path.lineTo(startPoint - translateX, height / 2f + centerRadius)
            path.close()

            basePath.addCircle(width / 2f, height / 2f, centerRadius, Path.Direction.CW)
            paint.style = Paint.Style.FILL
            paint.color = COLOR_BACKGROUND
            drawPath(basePath, paint)

            shadowPath.op(basePath, Path.Op.INTERSECT)
            paint.color = COLOR_WAVE_BACKGROUND
            paint.shader = shadowGradient
            drawPath(shadowPath, paint)

            path.op(basePath, Path.Op.INTERSECT)
            paint.color = COLOR_WAVE_FORGROUND
            paint.shader = gradient
            drawPath(path, paint)
            paint.shader = null


            //绘制圆点
            paint.color = COLOR_POINT
            paint.style = Paint.Style.FILL
            drawBubble(
                this,
                width / 2f - centerRadius * 0.76f,
                height / 2f + centerRadius * 0.3f,
                bubbles[0],
                centerRadius * 0.21f, false
            )

            drawBubble(
                this,
                width / 2f - centerRadius * 0.62f,
                height / 2f + centerRadius * 0.4f,
                bubbles[1],
                centerRadius * 0.21f, false
            )

            drawBubble(
                this,
                width / 2f - centerRadius * 0.5f,
                height / 2f + centerRadius * 0.44f,
                bubbles[2],
                centerRadius * 0.21f
            )

            drawBubble(
                this,
                width / 2f - centerRadius * 0.21f,
                height / 2f + centerRadius * 0.5f,
                bubbles[3],
                centerRadius * 0.21f, false
            )

            drawBubble(
                this,
                width / 2f,
                height / 2f + centerRadius / 2,
                bubbles[4],
                centerRadius / 4
            )

            drawBubble(
                this,
                width / 2f + centerRadius * 0.2f,
                height / 2f + centerRadius / 2,
                bubbles[5],
                centerRadius / 4
            )

            drawBubble(
                this,
                width / 2f + centerRadius * 0.37f,
                height / 2f + centerRadius / 2,
                bubbles[6],
                centerRadius / 4, false
            )

            drawBubble(
                this,
                width / 2f + centerRadius * 0.66f,
                height / 2f + centerRadius / 2,
                bubbles[7],
                centerRadius * 0.1f
            )
        }

    }

    private fun randomRadius(): Float = DensityUtil.dip2px(context,(6 - Random.nextInt(3)).toFloat())


    private fun drawBubble(canvas: Canvas, x: Float, y: Float, radius: Float, range: Float, sin: Boolean = true) {
        canvas.run {
            drawCircle(
                x,
                y + range * if (sin) sin(progress * 2 * PI.toFloat()) else cos((progress * 2 * PI.toFloat())),
                radius,
                paint
            )
        }
    }


    private fun doInit() {
        rectF = RectF(
            width / 2f - outRadius,
            height / 2f - outRadius,
            width / 2f + outRadius,
            height / 2f + outRadius
        )

        startPoint = width / 2 - centerRadius
        endPoint = width / 2 + centerRadius


        for (index in 0..1) {
            waves.add(Pair(centerRadius * 2, 50f))
        }

        for (index in 0..1) {
            shadowWaves.add(Pair(centerRadius * 4, 100f))
        }

        for (index in bubbles.indices) {
            bubbles[index] = randomRadius()
        }


        gradient = LinearGradient(
            width * 0.5f,
            height * 0.5f,
            width * 0.5f,
            height * 0.5f + centerRadius,
            COLOR_WAVE_FORGROUND,
            COLOR_POINT,
            Shader.TileMode.CLAMP
        )

        shadowGradient = LinearGradient(
            width * 0.5f,
            height * 0.5f,
            width * 0.5f,
            height * 0.5f + centerRadius,
            COLOR_WAVE_BACKGROUND,
            COLOR_POINT,
            Shader.TileMode.CLAMP
        )
    }

}