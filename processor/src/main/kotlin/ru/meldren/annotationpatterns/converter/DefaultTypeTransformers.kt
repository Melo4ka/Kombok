package ru.meldren.annotationpatterns.converter

import com.squareup.kotlinpoet.*
import ru.meldren.annotationpatterns.util.DATE
import ru.meldren.annotationpatterns.util.UUID

object DefaultTypeTransformers {

    private val Transformers = mutableMapOf<TransformerName, String>()

    init {
        // String <-> Number
        transformer(STRING, INT, "%N.toInt()", "%N.toString()")
        transformer(STRING, LONG, "%N.toLong()", "%N.toString()")
        transformer(STRING, DOUBLE, "%N.toDouble()", "%N.toString()")
        transformer(STRING, FLOAT, "%N.toFloat()", "%N.toString()")
        transformer(STRING, BOOLEAN, "%N.toBoolean()", "%N.toString()")
        transformer(STRING, CHAR, "%N.first()", "%N.toString()")
        transformer(STRING, SHORT, "%N.toShort()", "%N.toString()")
        transformer(STRING, BYTE, "%N.toByte()", "%N.toString()")

        // Number <-> Number
        transformer(INT, LONG, "%N.toLong()", "%N.toInt()")
        transformer(INT, DOUBLE, "%N.toDouble()", "%N.toInt()")
        transformer(INT, FLOAT, "%N.toFloat()", "%N.toInt()")
        transformer(INT, SHORT, "%N.toShort()", "%N.toInt()")
        transformer(INT, BYTE, "%N.toByte()", "%N.toInt()")
        transformer(LONG, DOUBLE, "%N.toDouble()", "%N.toLong()")
        transformer(LONG, FLOAT, "%N.toFloat()", "%N.toLong()")
        transformer(LONG, SHORT, "%N.toShort()", "%N.toLong()")
        transformer(LONG, BYTE, "%N.toByte()", "%N.toLong()")
        transformer(FLOAT, DOUBLE, "%N.toDouble()", "%N.toFloat()")
        transformer(FLOAT, SHORT, "%N.toShort()", "%N.toFloat()")
        transformer(FLOAT, BYTE, "%N.toByte()", "%N.toFloat()")
        transformer(SHORT, DOUBLE, "%N.toDouble()", "%N.toShort()")
        transformer(SHORT, BYTE, "%N.toByte()", "%N.toShort()")
        transformer(BYTE, DOUBLE, "%N.toDouble()", "%N.toByte()")

        transformer(BOOLEAN, INT, "if (%N) 1 else 0", "%N != 0")
        transformer(CHAR, INT, "%N.code", "%N.toChar()")

        // Collection <-> Collection
        transformer(ITERABLE, COLLECTION, "%N.toList()")
        transformer(ITERABLE, LIST, "%N.toList()")
        transformer(ITERABLE, SET, "%N.toSet()")
        transformer(COLLECTION, LIST, "%N.toList()")
        transformer(COLLECTION, SET, "%N.toSet()")
        transformer(ITERABLE, MUTABLE_COLLECTION, "%N.toMutableList()")
        transformer(ITERABLE, MUTABLE_LIST, "%N.toMutableList()")
        transformer(ITERABLE, MUTABLE_SET, "%N.toMutableSet()")
        transformer(COLLECTION, MUTABLE_LIST, "%N.toMutableList()")
        transformer(COLLECTION, MUTABLE_SET, "%N.toMutableSet()")
        transformer(LIST, MUTABLE_LIST, "%N.toMutableList()")
        transformer(SET, MUTABLE_SET, "%N.toMutableSet()")
        transformer(MAP, MUTABLE_MAP, "%N.toMutableMap()")

        // Array <-> Collection
        transformer(ARRAY, LIST, "%N.toList()", "%N.toTypedArray()")
        transformer(ARRAY, SET, "%N.toSet()", "%N.toTypedArray()")
        transformer(ARRAY, MUTABLE_LIST, "%N.toMutableList()", "%N.toTypedArray()")
        transformer(ARRAY, MUTABLE_SET, "%N.toMutableSet()", "%N.toTypedArray()")

        // Enum
        transformer(ENUM, STRING, "%N.name", "enumValueOf(%N)")
        transformer(ENUM, ENUM, "enumValueOf(%N.name)")

        // UUID
        transformer(UUID, STRING, "%N.toString()", "UUID.fromString(%N)")

        // Date
        transformer(DATE, LONG, "%N.time", "Date(%N)")
    }

    operator fun invoke(
        fromClassName: TypeName,
        toClassName: TypeName
    ) = Transformers[TransformerName(fromClassName, toClassName)]

    private fun transformer(
        fromClassName: ClassName,
        toClassName: ClassName,
        to: String
    ) {
        val name = TransformerName(fromClassName, toClassName)
        if (name in Transformers) {
            throw IllegalArgumentException("Transformer ${fromClassName.simpleName} -> ${toClassName.simpleName} already exists")
        }
        Transformers[name] = to
    }

    private fun transformer(
        fromClassName: ClassName,
        toClassName: ClassName,
        to: String,
        from: String
    ) {
        transformer(fromClassName, toClassName, to)
        transformer(toClassName, fromClassName, from)
    }

    private data class TransformerName(val fromClassName: TypeName, val toClassName: TypeName)
}