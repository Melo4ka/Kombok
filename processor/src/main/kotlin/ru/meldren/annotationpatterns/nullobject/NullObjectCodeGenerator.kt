package ru.meldren.annotationpatterns.nullobject

import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.utils.addToStdlib.applyIf
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.util.addReturnStatement
import ru.meldren.annotationpatterns.util.defaultPrimitiveValue

class NullObjectCodeGenerator(
    prefix: String,
    private val className: ClassName,
    private val functions: List<NullObjectFunction>
) : PatternCodeGenerator {

    private val nullObjectClassName = ClassName(
        className.packageName,
        prefix + className.simpleName
    )

    override fun generateFile() = FileSpec.builder(nullObjectClassName)
        .addType(generateClass())

    private fun generateClass() = TypeSpec.classBuilder(nullObjectClassName)
        .addSuperinterface(className)
        .addModifiers(KModifier.OPEN)
        .addFunctions(functions.map { generateFunction(it) })
        .build()

    private fun generateFunction(function: NullObjectFunction) = FunSpec.builder(function.name)
        .returns(function.returnType)
        .addModifiers(KModifier.OVERRIDE)
        .addParameters(function.parameters.map(::generateFunctionParameter))
        .applyIf(function.returnType != UNIT) {
            addReturnStatement(
                if (function.returnType.isNullable) {
                    "null"
                } else {
                    function.returnType.defaultPrimitiveValue ?: "throw NotImplementedError()"
                }
            )
        }
        .build()

    private fun generateFunctionParameter(parameter: NullObjectFunction.FunctionParameter) =
        ParameterSpec(parameter.name, parameter.typeName)
}