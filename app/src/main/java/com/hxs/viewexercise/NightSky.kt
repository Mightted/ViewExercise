package com.hxs.viewexercise

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.*
import android.text.TextPaint
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random

class NightSky(private val width: Float, private val height: Float) {


    /**
     * 用于并发绘制中，保持同步
     */
    private val meteors = CopyOnWriteArrayList<Meteor>()
    private val stars = mutableListOf<Star>()

    private val paint = Paint().apply {
        color = Color.WHITE
        flags = Paint.ANTI_ALIAS_FLAG
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private val textPaint = TextPaint().apply {
        textSize = 15f
        textAlign = Paint.Align.RIGHT
    }


    fun pause() {
        stars.forEach {
            it.set.pause()
        }
    }

    fun resume() {
        stars.forEach {
            it.set.resume()
        }
    }

    fun updateMeteors() {
        meteors.forEach {
            if (!it.isShow) {
                meteors.remove(it)
            }
        }
    }


    fun onDraw(canvas: Canvas?) {
        meteors.forEach {
            if (it.isShow) {
                it.draw(canvas, paint)
            }
        }

        stars.forEach {
            it.draw(canvas, textPaint)
        }

    }

    suspend fun onDraw(canvas: Canvas?, mutex: Mutex) {
        meteors.forEach {
            if (it.isShow) {
                it.draw(canvas, paint, mutex)
            }
        }

        stars.forEach {
            it.draw(canvas, textPaint)
        }
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


    init {
//        meteors.add(
//            Meteor(
//                floatArrayOf(width - 200f, 200f),
//                floatArrayOf(200f, 0.8f * height)
//            )
//        )
//        meteors.add(Meteor(width, height))

        stars.add(Star(floatArrayOf(width - 200f, 300f)))
        stars.add(Star(floatArrayOf(width - 300f, 400f)))
        stars.add(Star(floatArrayOf(width / 2f, 500f)))
        stars.add(Star(floatArrayOf(400f, 200f)))


    }


    fun addMeteor() {
        meteors.add(Meteor(width, height))
    }


    /**
     * 流星实体类，单独管理流星生命周期
     */
    class Meteor {

        /**
         * [start] 流星开始位置
         * [end] 流星结束位置
         */
        constructor(start: FloatArray, end: FloatArray) {
            updatePath(start, end)
        }


        constructor(widthRange: Float, heightRange: Float) {
            val startX = widthRange / 4 + Random.nextInt(widthRange.toInt() / 4)
            val startY = heightRange / 4 + Random.nextInt(heightRange.toInt() / 4)
            updatePath(
                floatArrayOf(widthRange / 2 + startX, heightRange / 2 - startY),
                floatArrayOf(widthRange / 2 - startY, heightRange / 2 + startX)
            )
        }


        private var lineStart = 0f
        private var lineEnd = 0f
        private val startPoint = FloatArray(2)
        private val endPoint = FloatArray(2)

        /**
         * 流星轨迹的渐变色的两端
         */
        private var startColor = (0xFFFFFFFF).toInt()
        private var endColor = (0x00FFFFFF).toInt()
        private var linearGradient: LinearGradient? = null

        /**
         * 流星划过的距离
         */
        private var translate = 400f

        private var speed = 5f + Random.nextInt(5)

        /**
         * 流星轨迹的长度
         */
        private val tailLength = 200f + Random.nextInt(50)

        /**
         * 记录流星整个路线
         */
        private val path = Path()
        private val pathMeasure = PathMeasure()
        var isShow = true

        /**
         * 当前流星轨迹的path
         */
        private val movePath = Path()


        private fun updatePath(start: FloatArray, end: FloatArray) {
            path.reset()
            path.moveTo(start[0], start[1])
            path.quadTo(
                (end[0] + start[0]) / 2 - Random.nextInt(100),
                (end[1] + start[1]) / 2 - Random.nextInt(100),
                end[0],
                end[1]
            )
            pathMeasure.setPath(path, false)
            translate = 0f
        }

        /**
         * 更新流星轨迹的透明度
         */
        private fun updateAlpha(): Int {
            // 策略：在流星轨迹完全出现之前，流星头采用渐变的方式出现；
            // 在流星轨迹变短直至消失，流星头采用渐变的方式消失
            // 其他阶段，流星头为无透明度
            pathMeasure.length.let { length ->
                val progress = when {
                    translate <= tailLength -> {
                        translate / (tailLength)
                    }
                    length - translate <= tailLength -> {
                        (length - translate) / tailLength
                    }
                    else -> {
                        1f
                    }
                }
                return (progress * 255).toInt()
            }

        }


        /**
         * 更新着色器
         */
        private fun updateShader(
            startPoints: FloatArray = endPoint, endPoints: FloatArray = startPoint,
            sColor: Int = startColor, eColor: Int = endColor
        ) {
            linearGradient = null
            linearGradient = LinearGradient(
                startPoints[0], startPoints[1],
                endPoints[0], endPoints[1],
                sColor, eColor,
                Shader.TileMode.CLAMP
            )
        }


        suspend fun draw(canvas: Canvas?, paint: Paint, mutex: Mutex) {

            doDraw(canvas, paint)
            mutex.withLock {
                translate = (translate + speed).coerceAtMost(pathMeasure.length)
                isShow = !(translate == 0f || translate == pathMeasure.length)
            }
        }


        fun draw(canvas: Canvas?, paint: Paint) {
            doDraw(canvas, paint)
            translate = (translate + speed).coerceAtMost(pathMeasure.length)
            isShow = !(translate == 0f || translate == pathMeasure.length)
        }

        /**
         * 绘制流星轨迹，不同的时间段，尾巴长度不同
         */
        private fun doDraw(canvas: Canvas?, paint: Paint) {

            if (pathMeasure.length == 0f || !isShow) {
                return
            }

            canvas?.run {
                movePath.reset()

                lineEnd = translate.coerceAtMost(pathMeasure.length)
                lineStart = (translate - tailLength).coerceAtLeast(0f)

                pathMeasure.getPosTan(lineStart, startPoint, null)
                pathMeasure.getPosTan(lineEnd, endPoint, null)

                pathMeasure.getSegment(lineStart, lineEnd, movePath, true)
                startColor = ColorUtils.setAlphaComponent(startColor, updateAlpha())

                updateShader()
                paint.shader = linearGradient

                drawPath(movePath, paint)
            }
        }

    }


    class Star(private val point: FloatArray) {

        private var size = 15f
        private var color = 0
        var set = AnimatorSet()

        init {
            // 星光透明度变化
            val animator1 = ValueAnimator.ofArgb(
                (0x00FFFFFF).toInt(), (0xFFFFFFFF).toInt(), (0x00FFFFFF).toInt()
            ).apply {
                repeatCount = ValueAnimator.INFINITE
                addUpdateListener {
                    color = it.animatedValue as Int
                }
            }

            // 星光大小变化
            val animator2 = ValueAnimator
                .ofFloat(0f, 10f + Random.nextInt(10), 0f).apply {
                    repeatCount = ValueAnimator.INFINITE
                    addUpdateListener {
                        size = it.animatedValue as Float
                    }
                }
            set.run {
                playTogether(animator1, animator2)
                duration = 1500 + Random.nextLong(1000)
                startDelay = 500 + Random.nextLong(1000)
                start()
            }


        }

        fun draw(canvas: Canvas?, textPaint: TextPaint) {
            textPaint.color = color
            textPaint.textSize = size
            canvas?.drawText("✨", point[0], point[1], textPaint)
        }
    }
}