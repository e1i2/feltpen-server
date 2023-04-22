package io.github.e1i2.global.r2dbc

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class JsonNodeToByteArrayConverter(
    private val objectMapper: ObjectMapper
) : Converter<JsonNode, String> {
    override fun convert(source: JsonNode): String = objectMapper.writeValueAsString(source)
}

@ReadingConverter
class ByteArrayToJsonNodeConverter(
    private val objectMapper: ObjectMapper
) : Converter<String, JsonNode> {
    override fun convert(source: String): JsonNode = objectMapper.readTree(source)
}
