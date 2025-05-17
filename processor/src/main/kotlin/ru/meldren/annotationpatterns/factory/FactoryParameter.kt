package ru.meldren.annotationpatterns.factory

import com.squareup.kotlinpoet.TypeName

data class FactoryParameter(
    val name: String,
    val typeName: TypeName,
    val defaultValue: String?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FactoryParameter

        if (name != other.name) return false
        if (typeName != other.typeName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + typeName.hashCode()
        return result
    }
}