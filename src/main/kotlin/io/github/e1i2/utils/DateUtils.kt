package io.github.e1i2.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun LocalDateTime.toDate(zoneId: ZoneId = ZoneId.systemDefault()): Date {
    return Date.from(atZone(zoneId).toInstant())
}
