package com.hxs.viewexercise.linechart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class LineChartView : View {

    private lateinit var dataSet: LineChartDataSet

    private val paint = Paint()
    private val textPaint = TextPaint()
    private val path = Path()
    private val borderHeight = 60f
    private val borderPath = Path()
    private val trianglePath = Path()
    private val borderPaint = Paint()
    private val dashEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
    private val offsetY = 0.5f
    private val rightOffset = 0.5f
    private var columnTextWidth: Float = 140f
    private var effectiveDrawWidth = 900f // 坐标横轴实际有效距离
    private var clickPosX = 0f
    //    private val columnCount: Int = 8 // 列8段
//    private val rowCount: Int = 7 // 行7段
//    private val rowItems = arrayOf("00:00", "04:00", "08:00", "12:00", "16:00", "20:00", "24:00")
//    private val columnItems = arrayOf("200", "160", "120", "80", "40", "0")


    private var drawHeight: Float = 0f
    private var drawWidth: Float = 1080f

    private var totalColumnValue: Float = 0f
    private var totalRowValue: Float = 0f

    private var gradient: LinearGradient? = null


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {

        borderPaint.strokeWidth = 1f
        borderPaint.style = Paint.Style.FILL
        borderPaint.color = Color.WHITE


        updateDataSet {

            LineDataProvider.createDataSet()

//            LineChartDataSet(
//                columnItems = floatArrayOf(0f, 1f, 2f, 3f, 4f, 5f)
//            )
//            LineChartDataSet(
//                columnItems = floatArrayOf(0f, 40f, 80f, 120f, 160f, 200f),
//                rowItems = listOf(
//                    Pair(0f, "00:00"),
//                    Pair(1 / 6f, "04:00"),
//                    Pair(2 / 6f, "08:00"),
//                    Pair(3 / 6f, "12:00"),
//                    Pair(4 / 6f, "16:00"),
//                    Pair(5 / 6f, "20:00"),
//                    Pair(1f, "24:00")
//                ),
//                lines = listOf(
//                    DrawLine(
//                        points = arrayOf(
//                            Pair(0f, 160f),
//                            Pair(1 / 6f, 176f),
//                            Pair(2 / 6f, 145f),
//                            Pair(3 / 6f, 163f),
//                            Pair(4 / 6f, 155f),
//                            Pair(5 / 6f, 170f),
//                            Pair(1f, 159f)
//                        ),
////                        floatArrayOf(160f, 176f, 145f, 163f, 155f, 170f, 159f),
//                        gradientColors = intArrayOf((0xffffd973).toInt(), (0x00ffd973).toInt())
//                    )
////                    DrawLine(
////                        points = floatArrayOf(108f, 120f, 100f, 118f, 98f, 117f, 110f),
////                        gradientColors = intArrayOf((0xff9686b8).toInt(), (0x009686b8).toInt())
////                    )
//                )
//            )
        }


    }

    private fun widthByPixel(width: Float): Float {
        return width * drawWidth / 1080f
    }

    fun updateDataSet(callback: () -> LineChartDataSet) {
        dataSet = callback().apply {
            totalColumnValue =
                (maxColumnValue - minColumnValue) * ((columnItems.size + 1f) / (columnItems.size - 1f))  // 纵轴实际上下两端还有两节未用到的

            totalRowValue =
                (maxRowValue - minRowValue) * ((rowItems.size + 1f) / (rowItems.size))


            paint.apply {
                flags = Paint.ANTI_ALIAS_FLAG
                strokeCap = Paint.Cap.BUTT
                style = Paint.Style.STROKE
                strokeWidth = 1f
                color = bgLineColor
                // 如果设置了圆角，则是曲线，否则是折线
                if (cornerSize > 0f) {
                    pathEffect = CornerPathEffect(cornerSize)
                }

            }

            textPaint.apply {
                color = textColor
                textSize = widthByPixel(40f)
                textAlign = Paint.Align.CENTER
                flags = Paint.ANTI_ALIAS_FLAG
                typeface = Typeface.DEFAULT
            }

        }
        invalidate()
    }


    private fun getYByColumnValue(value: Float): Float {
        return dataSet.run {
            drawHeight - ((value + (totalColumnValue - (maxColumnValue - minColumnValue)) / 2) / totalColumnValue) * drawHeight
        }
    }

    private fun getYByColumnIndex(index: Float): Float {
        return dataSet.run {
            drawHeight - (index + 1) / (columnItems.size + 1) * drawHeight
        }

    }

    private fun getXByRowValue(value: Float): Float {
        return dataSet.run {
            ((value - (totalRowValue - (maxRowValue - minRowValue) / 2)) / totalRowValue) / drawWidth
        }
    }

    /**
     * [index] 在每个曲线中的数据下标
     * [offset] 实际纵坐标的下标和数据并不在一条线上，所以要设置一定的便宜
     * [total] 用于打点或者绘制下面的字的时候，总的点的数量
     */
    private fun getXByRowIndex(index: Float): Float {
        return dataSet.run {
            columnTextWidth + index * effectiveDrawWidth
            //            min(max((index + 1), 1f), rowItems.size + 0.5f) / (rowItems.size + 1.5f) * drawWidth
//            (index + offset) / (total + rightOffset) * (drawWidth - columnTextWidth) + columnTextWidth
//            ((index * rowItems.size / total) + 1 + offset) / (total + 0.5f) * (drawWidth - columnTextWidth) + columnTextWidth

//            (index + 1) / (rowItems.size + 1.5f) * drawWidth
        }

    }

    // 获取默认的线的末端的X值
    private fun getDefaultEndX(): Float {
        return 12f / (12 + rightOffset) * (drawWidth - columnTextWidth) + columnTextWidth
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 保持宽高比为4:3
        if (width < height) {
            drawWidth = width.toFloat()
            drawHeight = (drawWidth * 1 / 2).coerceAtMost(height.toFloat())
        } else {
            drawHeight = height.toFloat()
            drawWidth = (drawHeight * 2).coerceAtMost(width.toFloat())
        }

        // 根据像素计算合适的宽度
        columnTextWidth = widthByPixel(140f)
        textPaint.textSize = widthByPixel(40f)
        textPaint.textAlign = Paint.Align.CENTER
        effectiveDrawWidth = widthByPixel(850f)


        canvas?.apply {

            canvas.drawColor(dataSet.bgColor)

            paint.strokeCap = Paint.Cap.BUTT
            paint.strokeWidth = 1f
            paint.color = dataSet.bgLineColor

            for (index in dataSet.columnItems.indices) {

                // 绘制纵坐标的字
                drawTextOnPath(dataSet.columnItems[index].toInt().toString(), path.apply {
                    reset()
                    moveTo(0f, getYByColumnIndex(index.toFloat()) + textPaint.textSize / 2)
                    lineTo(getXByRowIndex(0f), getYByColumnIndex(index.toFloat()) + textPaint.textSize / 2)
                }, 0f, 0f, textPaint)


                // 绘制背景线
                drawLine(
                    columnTextWidth,
                    getYByColumnIndex(index.toFloat()),
                    columnTextWidth + effectiveDrawWidth,
                    getYByColumnIndex(index.toFloat()),
                    paint
                )
            }


            if (dataSet.dashLineArea > 0) {
                paint.pathEffect = dashEffect
                for (i in 0..dataSet.dashLineArea) {
                    drawLine(
                        getXByRowIndex(i.toFloat() / dataSet.dashLineArea),
                        getYByColumnValue(dataSet.maxColumnValue),
                        getXByRowIndex(i.toFloat() / dataSet.dashLineArea),
                        getYByColumnIndex(0f), paint
                    )
                }
            }
            paint.pathEffect = null


            // 绘制横坐标的下标
            for (index in dataSet.rowItems.indices) {

                drawText(
                    dataSet.rowItems[index].second,
                    getXByRowIndex(dataSet.rowItems[index].first),
                    getYByColumnIndex(-0.5f),
                    textPaint
                )
            }
//            if (dataSet.endRowItem.isNotEmpty()) {
//                drawText(
//                        dataSet.endRowItem,
//                        getXByRowIndex(dataSet.rowItems.size.toFloat()),
//                        getYByColumnIndex(-0.5f),
//                        textPaint
//                )
//            }


            if (dataSet.lines.isNullOrEmpty()) {
                return
            }

            var clickIndex = -1

            // 绘制折线/曲线
            dataSet.lines?.forEach {

                path.reset()
                paint.color = it.gradientColors[0]
                paint.strokeWidth = widthByPixel(18f)
                paint.strokeCap = Paint.Cap.ROUND
                paint.style = Paint.Style.STROKE

                for (index in it.points.indices) {

                    if (dataSet.type == TYPE_PILLAR) {
//                        paint.strokeCap = Paint.Cap.BUTT
                        paint.strokeWidth = widthByPixel(20f)
                        drawLine(
                            getXByRowIndex(it.points[index].first),
                            getYByColumnValue(it.points[index].second) + paint.strokeWidth * 0.5f,
                            getXByRowIndex(it.points[index].first),
                            getYByColumnIndex(0f) - paint.strokeWidth * 0.5f, paint
                        )

                        if (clickPosX != 0f && abs(getXByRowIndex(it.points[index].first) - clickPosX) <= 10) {
                            clickIndex = index
                        }
                    }
//                    else {
//                        if (index == 0) {
//                            // 起点为第一个折点
//                            path.moveTo(
//                                getXByRowIndex(it.points[index].first),
//                                getYByColumnValue(it.points[index].second)
//                            )
//
//                        } else {
//                            path.lineTo(
//                                getXByRowIndex(it.points[index].first),
//                                getYByColumnValue(it.points[index].second)
//                            )
//                        }
//                    }

//                    if (dataSet.checkPoint) {
//                        drawPoint(
//                            getXByRowIndex(it.points[index].first),
//                            getYByColumnValue(it.points[index].second), paint
//                        )
//                    }

                }

                if (clickIndex != -1) {
                    drawValueBorder(
                        it.points[clickIndex].second.toInt().toString(),
                        getXByRowIndex(it.points[clickIndex].first),
                        getYByColumnValue(it.points[clickIndex].second), canvas
                    )
                }

//                if (dataSet.type == TYPE_LINE) {
//
//
//                    paint.strokeWidth = widthByPixel(5f)
//                    drawPath(path, paint)
//
//
//                    if (it.gradientColors.size >= 2) {
//                        path.lineTo(getXByRowIndex(it.points[it.points.lastIndex].first), getYByColumnIndex(0f))
//                        path.lineTo(getXByRowIndex(it.points[0].first), getYByColumnIndex(0f))
//
//                        path.close()
//
//
//                        if (gradient == null) {
//                            gradient = LinearGradient(
//                                0.5f * drawWidth,
//                                0f,
//                                0.5f * drawWidth,
//                                getYByColumnValue(0f),
//                                it.gradientColors,
//                                null,
//                                Shader.TileMode.CLAMP
//                            )
//                        }
//
//                        paint.style = Paint.Style.FILL_AND_STROKE
//                        paint.shader = gradient
//
//
//                        drawPath(path, paint)
//                        gradient = null
//                        paint.shader = null
//                    }
//                }

            }


        }
    }


    private fun drawValueBorder(value: String, chartX: Float, chartY: Float, canvas: Canvas) {
        borderPath.reset()
        borderPath.addRoundRect(
            chartX - borderHeight,
            chartY - borderHeight * 1.2f,
            chartX + borderHeight,
            chartY - borderHeight * 0.2f,
            floatArrayOf(5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f),
            Path.Direction.CCW
        )

        trianglePath.reset()
        trianglePath.moveTo(chartX, chartY)
        trianglePath.lineTo(chartX + borderHeight * 0.2f, chartY - borderHeight * 0.2f)
        trianglePath.lineTo(chartX - borderHeight * 0.2f, chartY - borderHeight * 0.2f)
        trianglePath.close()
        borderPath.op(trianglePath, Path.Op.UNION)

        canvas.drawPath(borderPath, borderPaint)

        canvas.drawText(value, chartX, chartY - borderHeight * 0.7f + getPaintBaseLine(textPaint), textPaint)
    }


    private fun getPaintBaseLine(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return (metrics.bottom - metrics.top) / 2f - metrics.bottom
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            clickPosX = event.x
            invalidate()
        }
        return super.onTouchEvent(event)
    }


}