package com.subreax.schedule

import com.subreax.schedule.utils.ApproxBinarySearchResult
import com.subreax.schedule.utils.approxBinarySearch
import org.junit.Assert
import org.junit.Test

class ApproxBinarySearchTest {
    @Test
    fun test_valueExist() {
        val list = listOf(0, 1, 2, 3, 4, 5, 6)
        val result = list.approxBinarySearch { it.compareTo(5) }
        Assert.assertEquals(ApproxBinarySearchResult(5, 5), result)
    }

    @Test
    fun test_valueExistLeft() {
        val list = listOf(0, 1, 2, 3, 4, 5, 6)
        val result = list.approxBinarySearch { it.compareTo(0) }
        Assert.assertEquals(ApproxBinarySearchResult(0, 0), result)
    }

    @Test
    fun test_valueExistRight() {
        val list = listOf(0, 1, 2, 3, 4, 5, 6)
        val result = list.approxBinarySearch { it.compareTo(6) }
        Assert.assertEquals(ApproxBinarySearchResult(6, 6), result)
    }

    @Test
    fun test_valueNotExist() {
        val list = listOf(0, 1, 2, 4, 5, 6)
        val result = list.approxBinarySearch { it.compareTo(3) }
        Assert.assertEquals(ApproxBinarySearchResult(2, 3), result)
    }

    @Test
    fun test_valueNotExistLeft() {
        val list = listOf(0, 1, 2, 3, 4, 5)
        val result = list.approxBinarySearch { it.compareTo(-1) }
        Assert.assertEquals(ApproxBinarySearchResult(0, 0), result)
    }

    @Test
    fun test_valueNotExistRight() {
        val list = listOf(0, 1, 2, 3, 4, 5)
        val result = list.approxBinarySearch { it.compareTo(6) }
        Assert.assertEquals(ApproxBinarySearchResult(5, 5), result)
    }

    @Test
    fun test_emptyList() {
        val list = emptyList<Int>()
        val result = list.approxBinarySearch { it.compareTo(0) }
        Assert.assertEquals(ApproxBinarySearchResult(0, 0), result)
    }
}