package com.github.manosbatsis.scrudbeans.model

import java.time.OffsetDateTime
import java.util.*

interface BaseEntity<S> {
    var id: S
}
interface UuidIdEntity : BaseEntity<UUID>

interface OffsetDateTimeIdEntity : BaseEntity<OffsetDateTime>