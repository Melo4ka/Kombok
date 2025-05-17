package ru.meldren.annotationpatterns.prototype

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import org.jetbrains.kotlin.utils.addToStdlib.applyIf
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.Prototype
import ru.meldren.annotationpatterns.util.addReturnStatement
import ru.meldren.annotationpatterns.util.hasAnnotation
import ru.meldren.annotationpatterns.util.name

class PrototypeCodeGenerator(
    private val copy: Boolean,
    private val deepCopy: Boolean,
    className: ClassName,
    private val parameters: List<PrototypeParameter>,
    private val typeParameters: List<TypeVariableName>
) : PatternCodeGenerator {

    private val className = if (typeParameters.isEmpty()) {
        className
    } else {
        className.parameterizedBy(typeParameters)
    }
    private val prototypeClassName = ClassName(className.packageName, "${className.simpleName}Prototype")

    override fun generateFile() = FileSpec.builder(prototypeClassName)
        .applyIf(copy) { addFunction(generateCopyFunction("copy", deep = false)) }
        .applyIf(deepCopy) { addFunction(generateCopyFunction("deepCopy", deep = true)) }

    private fun generateCopyFunction(name: String, deep: Boolean) = FunSpec.builder(name)
        .returns(className)
        .receiver(className)
        .addParameters(parameters.map { generateParameter(it, deep) })
        .addTypeVariables(typeParameters)
        .addReturnStatement(
            "%T(${parameters.joinToString { "%N" }})",
            className,
            *parameters.map { it.name }.toTypedArray()
        )
        .build()

    private fun generateParameter(parameter: PrototypeParameter, deep: Boolean) = ParameterSpec.builder(
        parameter.name,
        parameter.typeName
    ).apply {
        val copyFunction = parameter.copyFunction
        if (deep && copyFunction != null) {
            defaultValue(
                "this.%N${if (parameter.typeName.isNullable) "?" else ""}.${copyFunction.accessor}()",
                parameter.name,
                copyFunction.argument
            )
            return@apply
        }
        defaultValue("this.%N", parameter.name)
    }.build()
}