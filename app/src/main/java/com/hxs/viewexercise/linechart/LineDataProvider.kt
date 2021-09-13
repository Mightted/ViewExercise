package com.hxs.viewexercise.linechart

import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow
import kotlin.random.Random

object LineDataProvider {


    private fun drawAreaChart(startIndex: Float, endIndex: Float, size: Int, callback: (Float) -> Unit) {
        val areaLength = endIndex - startIndex
        for (i in 1..size) {
            callback(i.toFloat() / (size + 1) * areaLength + startIndex)
        }
    }

    private fun createDrawLine(startIndex: Float, endIndex: Float): DrawLine {
        val random = Random(1)
        val points = ArrayList<Pair<Float, Float>>()
        val unitLength = (endIndex - startIndex) / 4f
        for (i in 0..3) {
            drawAreaChart(startIndex + unitLength * i, startIndex + unitLength * (i + 1), 7) { index ->
                points.add(Pair(index, random.nextInt(120) + 80f))
            }
        }
        return DrawLine(
            points = points.toTypedArray(),
            gradientColors = intArrayOf((0xffffd973).toInt(), (0x00ffd973).toInt())
        )
    }


    fun createDataSet(): LineChartDataSet {

        val startIndex = 0f
        val endIndex = 1f
        var maxValue = 0f
        val dashLineArea = 4

        val random = Random(1)
        val points = ArrayList<Pair<Float, Float>>()
        val unitLength = (endIndex - startIndex) / dashLineArea
        for (i in 0 until dashLineArea) {
            drawAreaChart(startIndex + unitLength * i, startIndex + unitLength * (i + 1), 7) { index ->
                val value = random.nextInt(120) + 80f
                maxValue = maxValue.coerceAtLeast(value)
                points.add(Pair(index, value))
            }
        }


        val dataset = LineChartDataSet(
//            columnItems = floatArrayOf(0f, 40f, 80f, 120f, 160f, 200f),
            columnItems = getColumnMaxValue(maxValue),
            minColumnValue = 0f,
            maxColumnValue = 210f,
            dashLineArea = dashLineArea,
            rowItems = listOf(
                Pair(0f, "00:00"),
                Pair(1 / 6f, "04:00"),
                Pair(2 / 6f, "08:00"),
                Pair(3 / 6f, "12:00"),
                Pair(4 / 6f, "16:00"),
                Pair(5 / 6f, "20:00"),
                Pair(1f, "24:00")
            ),
            lines = listOf(
                DrawLine(
                    points = points.toTypedArray(),
                    gradientColors = intArrayOf((0xff57e094).toInt())
                )
            ),
            bgColor = (0xFF25343c).toInt()
//            lines = listOf(
//                DrawLine(
//                    points = arrayOf(
//                        Pair(0f, 160f),
//                        Pair(1 / 6f, 176f),
//                        Pair(2 / 6f, 145f),
//                        Pair(3 / 6f, 163f),
//                        Pair(4 / 6f, 155f),
//                        Pair(5 / 6f, 170f),
//                        Pair(1f, 159f)
//                    ),
//                    gradientColors = intArrayOf((0xffffd973).toInt(), (0x00ffd973).toInt())
//                )
//            )
        )



        return dataset
    }


    fun getColumnMaxValue(value: Float): FloatArray {
        val temp = value / 3f
        val size = log10(temp.toDouble())
        val base = if (size < 0) 1.0 else 10.0.pow(log10(temp.toDouble()).toInt().toDouble())
        var unitValue = (ceil(temp / base) * base).toFloat()
        if (unitValue.isNaN() || unitValue <= 1f) {
            unitValue = 1f

        }
        return floatArrayOf(0f, unitValue, unitValue * 2, unitValue * 3)
    }
}