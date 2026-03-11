package com.photomode.data.util

import com.photomode.domain.util.TimeSource
import java.util.concurrent.TimeUnit

/**
 * Uses system clock; day is UTC for consistency and testability.
 */
class TimeSourceImpl : TimeSource {

    override fun currentDayEpoch(): Long =
        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
}