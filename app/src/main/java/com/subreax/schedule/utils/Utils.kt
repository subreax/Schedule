package com.subreax.schedule.utils

import kotlin.math.abs

data class ApproxBinarySearchResult(
    val left: Int,
    val right: Int
)

fun <T> List<T>.approxBinarySearch(
    from: Int = 0,
    to: Int = this.size,
    comparison: (T) -> Int
): ApproxBinarySearchResult {
    if (abs(to - from) == 0) {
        return ApproxBinarySearchResult(0, 0)
    }

    var left = from
    var right = to - 1

    while (right - left > 1) {
        val mid = (left + right) / 2
        val cmp = comparison(get(mid))
        if (cmp > 0) {
            right = mid
        } else if (cmp < 0) {
            left = mid
        } else {
            return ApproxBinarySearchResult(mid, mid)
        }
    }

    val cmpLeft = comparison(get(left))
    val cmpRight = comparison(get(right))

    return if (cmpLeft >= 0) {
        ApproxBinarySearchResult(left, left)
    } else if (cmpRight <= 0) {
        ApproxBinarySearchResult(right, right)
    } else {
        ApproxBinarySearchResult(left, right)
    }
}
