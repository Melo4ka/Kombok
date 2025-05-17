package ru.meldren.annotationpatterns.visitor

import com.squareup.kotlinpoet.*
import ru.meldren.annotationpatterns.PatternCodeGenerator

class VisitorCodeGenerator(
    private val className: ClassName,
    private val visitFunctionPrefix: String,
    private val returnType: ClassName,
    private val classesToVisit: List<ClassName>
) : PatternCodeGenerator {

    private val visitorClassName = ClassName(className.packageName, "${className.simpleName}Visitor")

    override fun generateFile() = FileSpec.builder(visitorClassName)
        .addType(generateVisitorInterface())

    private fun generateVisitorInterface() = TypeSpec.interfaceBuilder(visitorClassName)
        .addFunction(generateVisitorFunction(className))
        .addFunctions(classesToVisit.map(::generateVisitorFunction))
        .build()

    private fun generateVisitorFunction(className: ClassName) =
        FunSpec.builder(visitFunctionPrefix + className.simpleName)
            .addParameter(className.simpleName.replaceFirstChar { it.lowercaseChar() }, className)
            .returns(returnType)
            .addModifiers(KModifier.ABSTRACT)
            .build()
} 