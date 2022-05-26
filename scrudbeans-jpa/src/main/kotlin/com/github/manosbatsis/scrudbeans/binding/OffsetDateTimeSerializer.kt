package com.github.manosbatsis.scrudbeans.binding

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class OffsetDateTimeSerializer : JsonSerializer<OffsetDateTime?>() {

    override fun serialize(value: OffsetDateTime?, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider?) {
        if (value == null) {
            jsonGenerator.writeNull()
        }
        jsonGenerator.writeString(ISO_8601_FORMATTER.format(value))
    }

    companion object {
        private val ISO_8601_FORMATTER: DateTimeFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")
            .withZone(ZoneId.of("UTC"))
    }

}

class OffsetDateTimeDeserializer : JsonDeserializer<OffsetDateTime?>() {

    companion object {
        private val ISO_8601_FORMATTER: DateTimeFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")
            .withZone(ZoneId.of("UTC"))
    }

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): OffsetDateTime? {
        return parser.text?.let{ OffsetDateTime.parse(it, ISO_8601_FORMATTER) }
    }
}