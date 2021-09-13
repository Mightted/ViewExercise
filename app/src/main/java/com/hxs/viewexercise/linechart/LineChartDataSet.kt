package com.hxs.viewexercise.linechart

import android.graphics.Color

const val TYPE_LINE = 0 // 折线图
const val TYPE_PILLAR = 1 // 柱状图

class LineChartDataSet(
    // 横轴数据项
    val rowItems: List<Pair<Float, String>> = arrayListOf(),

    val checkPoint: Boolean = false, // 是否需要每个位置打点

    // 纵轴数据项
    val columnItems: FloatArray = floatArrayOf(),

    val lines: List<DrawLine>? = null, // 要绘制的线

    val cornerSize: Float = 0f, // 圆角大小，如果为0，表示画的是折线，否则是曲线

    val type: Int = TYPE_PILLAR, // 默认折线或者曲线图

    val dashLineArea: Int = 0, // 如果要画虚线的分块


    var bgLineColor: Int = (0xFFE5E5E5).toInt(), // 背景线的颜色
    var textColor: Int = (0xFF000000).toInt(), // 横纵轴的字的颜色，应该和背景线颜色相同
    var bgColor: Int = Color.WHITE,

    // 横纵坐标的最大最小值，用于确定在坐标中的位置
    var minRowValue: Float = 0f,
    var maxRowValue: Float = 24f,
    var minColumnValue: Float = 0f,
    var maxColumnValue: Float = 200f
)

class DrawLine(val points: Array<Pair<Float, Float>>, val gradientColors: IntArray)
