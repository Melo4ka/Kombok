package ru.meldren.annotationpatterns.builder

import com.squareup.kotlinpoet.TypeName

data class BuilderParameter(
    val name: String,
    val typeName: TypeName,
    val vararg: Boolean,
    val defaultValue: String?
)