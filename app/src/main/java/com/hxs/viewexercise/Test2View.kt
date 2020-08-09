package com.hxs.viewexercise

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Time: 2020/8/6
 * Author: Mightted
 * Description: 基于MetaBall算法的原点指示器
 */
class Test2View : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 横纵轴的绘制范围
     * 通过MetaBall算法来计算每个原点的形状和位置，难以通过path来得到其路径，只能遍历像素点来绘制
     * 而绘制的范围越大，计算的压力也越大，所以需要预先确定最小绘制范围，保证动画的流畅度
     *
     */
    private val rangeWidth = IntArray(2)
    private val rangeHeight = IntArray(2)

    /**
     * 需要绘制的点集合
     */
    private val points = mutableListOf<Float>()
    private val centerPoints = ArrayList<Pair<Float, Float>>(4)
    private val focusPoint = FloatArray(2)

    /**
     * 是否能循环滚动
     */
    private var isCycle = true
    private var count = 2

    private var index = 0

    private val unitValue = 16f
    private var threshold = 2f
    private var normalRadius = unitValue * threshold
    private var focusRadius = unitValue * (threshold - 1)
    private val rectPath = Path()
    private val animator = ValueAnimator().apply {
        addUpdateListener {
            focusPoint[0] = it.animatedValue as Float
            invalidate()
        }
    }

    private val paint = Paint().apply {
        strokeWidth = 1f
        color = Color.BLUE

        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
    }

    fun setOffset(progress: Float) {

        focusPoint[0] = progress * (centerPoints[centerPoints.size - 1].first) + (1 - progress) * centerPoints[0].first
//        offset = (centerPoints[centerPoints.size - 1].first - centerPoints[0].first) * value
        invalidate()
    }

    fun offsetIndex(offset: Int) {
        if (isCycle) {
            currentIndex((((index + offset) % centerPoints.size) + centerPoints.size) % centerPoints.size)
        } else {
            currentIndex((index + offset).coerceIn(centerPoints.indices))
        }

    }

    fun currentIndex(index: Int) {
//        var tempIndex = index
//        if (index < 0) {
//            tempIndex = centerPoints.size - 1
//        } else if (index > centerPoints.size - 1) {
//            tempIndex = 0
//        }
        if (index !in centerPoints.indices) {
            throw IndexOutOfBoundsException("Wrong index!current index is $index, isCycle is $isCycle")
        }
        if (this.index == index) {
            return
        }
        animator.setFloatValues(centerPoints[this.index].first, centerPoints[index].first)
        animator.start()
        this.index = index
//        invalidate()
    }

    init {
        post {
            initData()
            invalidate()

        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {

            rectPath.reset()

            paint.color = Color.LTGRAY
            drawRect(
                rangeWidth[0].toFloat(), rangeHeight[0].toFloat(),
                rangeWidth[1].toFloat(), rangeHeight[1].toFloat(), paint
            )

            paint.color = Color.BLUE
            points.clear()

            for (row in rangeHeight[0]..rangeHeight[1]) {
                for (column in rangeWidth[0]..rangeWidth[1]) {
                    var tempThreshold = 0f
                    centerPoints.forEach {
                        tempThreshold += BezierUtil.metaBall(it, column.toFloat(), row.toFloat(), normalRadius)
                    }
                    tempThreshold += BezierUtil.metaBall(focusPoint, column.toFloat(), row.toFloat(), focusRadius)
                    if (tempThreshold >= threshold) {
                        points.add(column.toFloat())
                        points.add(row.toFloat())
                    }
                }
            }

            drawPoints(points.toFloatArray(), paint)
        }

    }


    private fun initData() {
        centerPoints.clear()
        val initOffset = measuredWidth / 2f - (count - 1) * 2 * normalRadius
        val unitOffset = normalRadius * 4f
        for (index in 0 until count) {
            centerPoints.add(Pair(initOffset + index * unitOffset, measuredHeight / 2f))
        }

        updateRect()
        focusPoint[0] = centerPoints[0].first
        focusPoint[1] = centerPoints[0].second
    }

    private fun updateRect() {
        rangeWidth[0] = measuredWidth / 2f.toInt()
        rangeHeight[0] = measuredHeight / 2f.toInt()
        rangeWidth[1] = measuredWidth / 2f.toInt()
        rangeHeight[1] = measuredHeight / 2f.toInt()

        centerPoints.forEach {
            rangeWidth[0] = (rangeWidth[0].coerceAtMost((it.first - unitValue * 3).toInt()))
            rangeHeight[0] = (rangeHeight[0].coerceAtMost((it.second - unitValue *  3).toInt()))
            rangeWidth[1] = (rangeWidth[0].coerceAtLeast((it.first + unitValue *  3).toInt()))
            rangeHeight[1] = (rangeHeight[1].coerceAtLeast((it.second + unitValue *  3).toInt()))
        }
    }
}
