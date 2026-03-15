package com.photomode.domain.usecase.lesson

import com.photomode.domain.util.TimeSource

/**
 * Fake for tests. Returns a fixed day so tests are deterministic.
 */
class FakeTimeSource(var dayEpoch: Long = 0L) : TimeSource {

    override fun currentDayEpoch(): Long = dayEpoch
}
