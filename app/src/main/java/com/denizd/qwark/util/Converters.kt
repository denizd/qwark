package com.denizd.qwark.util

fun Pair<Int, Int>.toMillis(): Long = (first.toLong() * 60 * 60 * 1000) + (second.toLong() * 60 * 1000)
fun Long.toHourAndSecond(): Pair<Int, Int> {
    val minutes = (this / (1000 * 60)).toInt()
    return Pair(minutes / 60, minutes % 60)
}