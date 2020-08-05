package com.hxs.viewexercise

import android.graphics.Path
import kotlin.math.PI
import kotlin.math.tan


object BezierUtil {

    private val circleRatio = 4 / 3f * tan(PI / 8).toFloat()

    fun addPath(path: Path, x: Float, y: Float, rawRadius: Float, offset: Float = rawRadius) {
        val radius = ((1 - ((offset / rawRadius) - 1) * 0.1f) * rawRadius).coerceAtLeast(10f)
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
}