package ru.meldren.annotationpatterns.templatemethod

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import ru.meldren.annotationpatterns.PatternCodeGenerator

class TemplateMethodCodeGenerator(
    private val className: ClassName,
    private val functionName: String,
    private val functionsToInvoke: List<String>
) : PatternCodeGenerator {

    private val templateMethodClassName = ClassName(className.packageName, "${className.simpleName}TemplateMethod")

    override fun generateFile() = FileSpec.builder(templateMethodClassName)
        .addFunction(generateTemplateMethodFunction())

    private fun generateTemplateMethodFunction() = FunSpec.builder(functionName)
        .receiver(className)
        .apply {
            functionsToInvoke.forEach { addStatement("%N()", it) }
        }
        .build()
} 