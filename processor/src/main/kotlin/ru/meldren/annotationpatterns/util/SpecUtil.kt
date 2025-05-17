@file:Suppress("NOTHING_TO_INLINE")

package ru.meldren.annotationpatterns.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

inline fun FunSpec.Builder.addReturnStatement(format: String, vararg args: Any) =
    addStatement("return $format", *args)

inline fun FunSpec.Builder.beginReturnControlFlow(controlFlow: String, vararg args: Any) =
    beginControlFlow("return $controlFlow", *args)

inline fun TypeSpec.Builder.addEnumConstants(names: Iterable<String>) =
    apply { names.forEach(::addEnumConstant) }