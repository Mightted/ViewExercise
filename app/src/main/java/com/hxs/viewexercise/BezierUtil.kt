package com.hxs.viewexercise

import android.graphics.Path
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan


object BezierUtil {

    private val circleRatio = 4 / 3f * tan(PI / 8).toFloat()

    fun addPath(path: Path, x: Float, y: Float, radius: Float, offset: Float = radius) {
        path.moveTo(x - radius, y)

        // 左上角
        path.cubicTo(
            x - radius,
            y - circleRatio * radius,
            x - circleRatio * radius,
            y - radius,
            x,
            y - radius
        )


        // 右上角
        path.cubicTo(
            x + circleRatio * radius,
            y - radius,
            x + radius,
            y - circleRatio * radius,
            x + offset,
            y
        )


        // 右下角
        path.cubicTo(
            x + radius,
            y + circleRatio * radius,
            x + circleRatio * radius,
            y + radius,
            x,
            y + radius
        )

        // 左下角
        path.cubicTo(
            x - circleRatio * radius,
            y + radius,
            x - radius,
            y + circleRatio * radius,
            x - radius,
            y
        )
    }


    fun metaBall(center: FloatArray, dst: FloatArray, radius: Float): Float {
        return metaBall(center, dst[0], dst[1], radius)
    }

    fun metaBall(center: FloatArray, x: Float, y: Float, radius: Float): Float {
        return radius / sqrt((x - center[0]).pow(2) + (y - center[1]).pow(2))
    }
}