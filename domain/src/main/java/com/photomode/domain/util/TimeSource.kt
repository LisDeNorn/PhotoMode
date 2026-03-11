package com.photomode.domain.util

/**
 * Provides current time for domain logic. Allows tests to inject a fixed time.
 */
interface TimeSource {

    /**
     * Returns the current day as epoch days (e.g. days since 1970-01-01 UTC).
     * Same calendar day always returns the same value.
     */
    fun currentDayEpoch(): Long
}
