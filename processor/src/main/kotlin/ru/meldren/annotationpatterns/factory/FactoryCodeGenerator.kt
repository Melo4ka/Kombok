package ru.meldren.annotationpatterns.factory

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.util.addEnumConstants
import ru.meldren.annotationpatterns.util.beginReturnControlFlow

class FactoryCodeGenerator(
    private val className: ClassName,
    typeEnumSuffix: String,
    private val factoryInstances: List<FactoryInstance>,
    private val parameters: List<FactoryParameter>,
    private val typeParameters: List<TypeVariableName>
) : PatternCodeGenerator {

    private val factoryClassName = ClassName(className.packageName, "${className.simpleName}Factory")
    private val enumClassName = ClassName(
        factoryClassName.packageName,
        className.simpleName + typeEnumSuffix
    )

    override fun generateFile() = FileSpec.builder(factoryClassName)
        .addType(generateInstancesEnum())
        .addFunction(generateFactoryFunction())

    private fun generateInstancesEnum() = TypeSpec.enumBuilder(enumClassName)
        .addEnumConstants(factoryInstances.map { it.name })
        .build()

    private fun generateFactoryFunction() = FunSpec.builder(className.simpleName)
        .returns(
            if (typeParameters.isEmpty()) {
                className
            } else {
                className.parameterizedBy(typeParameters.map { STAR })
            }
        )
        .addParameter("type", enumClassName)
        .addParameters(parameters.map(::generateFactoryParameter))
        .beginReturnControlFlow("when (type)")
        .apply {
            factoryInstances.forEach { instance ->
                val instanceParameters = this@FactoryCodeGenerator.parameters.filter { it in instance.parameters }
                addStatement(
                    "%T.%L -> %T(${instanceParameters.joinToString { "%N = %N" }})",
                    enumClassName,
                    instance.name,
                    instance.className,
                    *instanceParameters.flatMap { List(2) { _ -> it.name } }.toTypedArray()
                )
            }
        }
        .endControlFlow()
        .build()

    private fun generateFactoryParameter(parameter: FactoryParameter) = ParameterSpec.builder(
        parameter.name,
        parameter.typeName
    )
        .apply { parameter.defaultValue?.let(::defaultValue) }
        .build()
}