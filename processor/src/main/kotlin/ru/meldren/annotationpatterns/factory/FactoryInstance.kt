package ru.meldren.annotationpatterns.factory

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import ru.meldren.annotationpatterns.util.toSnakeCase

data class FactoryInstance(
    val className: ClassName,
    val parameters: List<FactoryParameter>
) {

    val name = className.simpleName.toSnakeCase().uppercase()
}