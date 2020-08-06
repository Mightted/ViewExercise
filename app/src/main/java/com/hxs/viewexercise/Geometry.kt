package com.hxs.viewexercise

import kotlin.math.acos
import kotlin.math.sqrt

object Geometry {

    private val temp = FloatArray(8)


    /**
     * 获得两点间的距离
     */
    fun length(src: FloatArray, dst: FloatArray, offset1: Int = 0, offset2: Int = 0): Float {
        return length(dst[offset1] - src[offset2], dst[offset1 + 1] - src[offset2 + 1])
//        val xValue = (dst[offset1] - src[offset2])
//        val yValue = (dst[offset1 + 1] - src[offset2 + 1])
//        return sqrt(xValue * xValue + yValue * yValue)
    }


    /**
     * 获取向量的长度
     */
    fun length(vector: FloatArray, offset: Int = 0): Float {
        return length(vector[offset], vector[offset + 1])
    }

    fun length(x: Float, y: Float): Float {
        return sqrt(x * x + y * y)
    }

    /**
     * 获取两点间的向量
     */
    fun getVector(dst: FloatArray, start: FloatArray, end: FloatArray, offset: Int = 0) {
        dst[offset] = end[0] - start[0]
        dst[offset + 1] = end[1] - start[1]
    }

    fun radianBetween2Edge(
        start1: FloatArray,
        end1: FloatArray,
        start2: FloatArray,
        end2: FloatArray
    ): Float {
        getVector(temp, start1, end1)
        getVector(temp, start2, end2, 2)
        return acos(dotProduct(temp, temp, 0, 2) / length(temp) * length(temp, 2))


    }


    /**
     * 计算点乘
     */
    fun dotProduct(
        start1: FloatArray,
        end1: FloatArray,
        start2: FloatArray,
        end2: FloatArray
    ): Float {
        getVector(temp, start1, end1)
        getVector(temp, start2, end2, 2)
        return dotProduct(temp, temp, 0, 2)
//        return temp[0] * temp[2] + temp[1] * temp[3]
    }

    fun dotProduct(
        vector1: FloatArray,
        vector2: FloatArray,
        offset1: Int = 0,
        offset2: Int = 0
    ): Float {
        return vector1[offset1] * vector2[offset2] + vector1[offset1 + 1] * vector2[offset2 + 1]
    }

}