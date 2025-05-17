package ru.meldren.annotationpatterns.builder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.kotlin.utils.addToStdlib.applyIf
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.util.addReturnStatement

class BuilderCodeGenerator(
    private val alwaysGenerateVarargFunctions: Boolean,
    private val builderFunction: Boolean,
    private val builderFunctionName: String,
    private val builderFunctionsPrefix: String,
    private val className: ClassName,
    private val parameters: List<BuilderParameter>
) : PatternCodeGenerator {

    private val builderClassName = ClassName(className.packageName, "${className.simpleName}Builder")

    override fun generateFile() = FileSpec.builder(builderClassName)
        .applyIf(builderFunction) {
            addFunction(generateBuilderFunction())
        }
        .addType(generateBuilderClass())

    private fun generateBuilderClass() = TypeSpec.classBuilder(builderClassName)
        .apply {
            parameters.forEach { parameter ->
                addProperty(generateProperty(parameter))
                if (parameter.shouldHaveInitProperty()) {
                    addProperty(generateInitProperty(parameter))
                }
                addFunction(generatePropertyFunction(parameter, parameter.shouldHaveInitProperty()))
                if (alwaysGenerateVarargFunctions || parameter.vararg) {
                    val parameterizedTypeName = parameter.typeName as? ParameterizedTypeName ?: return@forEach
                    val varargType =
                        VarargType.entries.find { it.className == parameterizedTypeName.rawType } ?: return@forEach
                    addFunction(
                        generateVarargPropertyFunction(
                            parameter,
                            varargType.parameterCreator(parameterizedTypeName.typeArguments),
                            varargType.arrayTransformerMemberName
                        )
                    )
                }
            }
        }
        .addFunction(generateBuildFunction())
        .build()

    private fun generateBuildFunction() = FunSpec.builder("build")
        .returns(className)
        .apply {
            this@BuilderCodeGenerator.parameters.forEach { parameter ->
                addStatement("val ${parameter.name} = " + if (parameter.typeName.isNullable) {
                    if (parameter.defaultValue != null) {
                        "if (${parameter.name.toInitPropertyName()}) this.${parameter.name} else (${parameter.defaultValue})"
                    } else {
                        "this.${parameter.name}"
                    }
                } else {
                    "this.${parameter.name} ?: ${parameter.defaultValue?.let { "($it)" } ?: "throw IllegalStateException(\"Property ${parameter.name} must be initialized\")"}"
                })
            }
        }
        .addReturnStatement("%T(${parameters.joinToString { it.name }})", className)
        .build()

    private fun generatePropertyFunction(
        parameter: BuilderParameter,
        updateInitProperty: Boolean
    ) = FunSpec.builder(parameter.name.toPropertyFunctionName())
        .returns(builderClassName)
        .addParameter(parameter.name, parameter.typeName)
        .addStatement("this.${parameter.name} = ${parameter.name}")
        .applyIf(updateInitProperty) {
            addStatement("this.${parameter.name.toInitPropertyName()} = true")
        }
        .addReturnStatement("this")
        .build()

    private fun generateVarargPropertyFunction(
        parameter: BuilderParameter,
        typeName: TypeName,
        arrayTransformerMemberName: MemberName
    ): FunSpec {
        val functionName = parameter.name.toPropertyFunctionName()
        return FunSpec.builder(functionName)
            .returns(builderClassName)
            .addParameter(parameter.name, typeName, KModifier.VARARG)
            .addReturnStatement("$functionName(${parameter.name}.%M())", arrayTransformerMemberName)
            .build()
    }

    private fun generateProperty(parameter: BuilderParameter) = PropertySpec.builder(
        parameter.name,
        parameter.typeName.copy(nullable = true)
    ).initializer("null")
        .mutable()
        .addModifiers(KModifier.PRIVATE)
        .build()

    private fun generateInitProperty(parameter: BuilderParameter) = PropertySpec.builder(
        parameter.name.toInitPropertyName(),
        BOOLEAN
    ).initializer("false")
        .mutable()
        .addModifiers(KModifier.PRIVATE)
        .build()

    private fun generateBuilderFunction() = FunSpec.builder(
        builderFunctionName.ifBlank { className.simpleName.replaceFirstChar { it.lowercaseChar() } }
    ).addParameter("builder", LambdaTypeName.get(receiver = builderClassName, returnType = UNIT))
        .returns(className)
        .addReturnStatement("%T().apply(builder).build()", builderClassName)
        .build()

    private fun BuilderParameter.shouldHaveInitProperty() = typeName.isNullable && defaultValue != null

    private fun String.toInitPropertyName() = "_${this}Inited"

    private fun String.toPropertyFunctionName() = if (builderFunctionsPrefix.isBlank()) {
        this
    } else {
        builderFunctionsPrefix + replaceFirstChar { it.uppercaseChar() }
    }

    private enum class VarargType(
        val className: ClassName,
        val arrayTransformerMemberName: MemberName,
        val parameterCreator: (List<TypeName>) -> TypeName = { it.first() }
    ) {

        LIST("List"),
        SET("Set"),
        MAP("Map", { (key, value) -> ClassName("kotlin", "Pair").parameterizedBy(key, value) }),
        MUTABLE_LIST("MutableList"),
        MUTABLE_SET("MutableSet"),
        MUTABLE_MAP("MutableMap", MAP.parameterCreator),
        HASH_SET("HashSet"),
        SORTED_SET("SortedSet");

        constructor(
            simpleName: String,
            parameterCreator: (List<TypeName>) -> TypeName = { it.first() }
        ) : this(
            ClassName("kotlin.collections", simpleName),
            MemberName("kotlin.collections", "to$simpleName", isExtension = true),
            parameterCreator
        )
    }
}