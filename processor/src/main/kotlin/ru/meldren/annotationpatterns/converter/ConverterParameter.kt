package ru.meldren.annotationpatterns.converter

import com.squareup.kotlinpoet.TypeName

data class ConverterParameter(
    val name: String,
    val nameMapping: String?,
    val typeData: TypeData
) {

    data class TypeData(
        val sourceType: TypeName,
        val targetType: TypeName,
        val customTypeTransformer: CustomTypeTransformer?,
        val typeArgumentsData: List<TypeData>
    )
}

typealias CustomTypeTransformer = Pair<String, String>

val CustomTypeTransformer.to get() = first
val CustomTypeTransformer.from get() = second