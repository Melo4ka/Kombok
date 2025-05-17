package ru.meldren.annotationpatterns.nullobject

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

data class NullObjectFunction(
    val name: String,
    val returnType: ClassName,
    val parameters: List<FunctionParameter>
) {

    data class FunctionParameter(
        val name: String,
        val typeName: TypeName
    )
}