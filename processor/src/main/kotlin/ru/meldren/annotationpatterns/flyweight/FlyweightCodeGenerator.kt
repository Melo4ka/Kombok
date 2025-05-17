package ru.meldren.annotationpatterns.flyweight

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.util.addReturnStatement

class FlyweightCodeGenerator(
    private val className: ClassName,
    suffix: String,
    mapType: ClassName,
    private val keyPropertyName: String,
    private val keyType: TypeName
) : PatternCodeGenerator {

    private val flyweightClassName = ClassName(
        className.packageName,
        className.simpleName + suffix
    )
    private val mapType = mapType.parameterizedBy(keyType, className)
    private val mapPropertyName = "${className.simpleName}s"

    override fun generateFile() = FileSpec.builder(flyweightClassName)
        .addType(generateClass())

    private fun generateClass() = TypeSpec.objectBuilder(flyweightClassName)
        .addProperty(generateProperty())
        .addFunction(generateFunction())
        .build()

    private fun generateProperty() = PropertySpec.builder(mapPropertyName, mapType)
        .addModifiers(KModifier.PRIVATE)
        .initializer("%T()", mapType)
        .build()

    private fun generateFunction() = FunSpec.builder("invoke")
        .returns(className)
        .addModifiers(KModifier.OPERATOR)
        .addParameter("key", keyType)
        .addReturnStatement("%N.getOrPut(key) { %T(%N = key) }", mapPropertyName, className, keyPropertyName)
        .build()
}