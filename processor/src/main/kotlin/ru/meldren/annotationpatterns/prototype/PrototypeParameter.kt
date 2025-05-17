package ru.meldren.annotationpatterns.prototype

import com.squareup.kotlinpoet.TypeName

data class PrototypeParameter(
    val name: String,
    val typeName: TypeName,
    val copyFunction: PrototypeCopyFunction?
)