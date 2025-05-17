package ru.meldren.annotationpatterns

import com.squareup.kotlinpoet.FileSpec

interface PatternCodeGenerator {

    fun generateFile(): FileSpec.Builder
}