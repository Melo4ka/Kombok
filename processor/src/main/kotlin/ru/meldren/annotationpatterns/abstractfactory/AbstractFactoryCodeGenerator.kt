package ru.meldren.annotationpatterns.abstractfactory

import com.squareup.kotlinpoet.*
import ru.meldren.annotationpatterns.PatternCodeGenerator

class AbstractFactoryCodeGenerator(
    private val className: ClassName,
    private val functionPrefix: String,
    private val factoryItems: List<ClassName>
) : PatternCodeGenerator {

    private val factoryClassName = ClassName(className.packageName, "${className.simpleName}Factory")

    override fun generateFile() = FileSpec.builder(factoryClassName)
        .addType(generateFactoryInterface())

    private fun generateFactoryInterface() = TypeSpec.interfaceBuilder(factoryClassName)
        .addFunctions(factoryItems.map(::generateFactoryFunction))
        .build()

    private fun generateFactoryFunction(className: ClassName) = FunSpec.builder(functionPrefix + className.simpleName)
        .returns(className)
        .addModifiers(KModifier.ABSTRACT)
        .build()
} 