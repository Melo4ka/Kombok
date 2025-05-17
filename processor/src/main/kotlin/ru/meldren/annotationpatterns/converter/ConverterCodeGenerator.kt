package ru.meldren.annotationpatterns.converter

import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.fir.builder.escapedStringToCharacter
import org.jetbrains.kotlin.utils.addToStdlib.applyIf
import org.jetbrains.kotlin.utils.addToStdlib.butIf
import org.jetbrains.kotlin.utils.addToStdlib.swap
import org.jetbrains.kotlin.utils.memoryOptimizedMap
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.util.*

class ConverterCodeGenerator(
    private val sourceClassName: ClassName,
    private val targetClassName: ClassName,
    private val parameters: List<ConverterParameter>,
    private val to: Boolean,
    private val from: Boolean
) : PatternCodeGenerator {

    private val converterClassName =
        ClassName(sourceClassName.packageName, "${sourceClassName.simpleName}2${targetClassName.simpleName}Converter")

    override fun generateFile() = FileSpec.builder(converterClassName)
        .applyIf(to) { addFunction(generateFunction(sourceClassName, targetClassName)) }
        .applyIf(from) { addFunction(generateFunction(targetClassName, sourceClassName)) }

    private fun generateFunction(source: ClassName, target: ClassName): FunSpec {
        val directOrder = source === sourceClassName
        return FunSpec.builder("to${target.simpleName}")
            .receiver(source)
            .returns(target)
            .addReturnStatement(
                "%T(${
                    parameters.joinToString(
                        prefix = "⇥",
                        postfix = "⇤\n",
                        separator = ","
                    ) { "\n%N = ${getParameterTransformer(directOrder, it.typeData) ?: "%N"}" }
                })",
                target,
                *parameters.flatMap { getParameterNames(directOrder, it) }.toTypedArray()
            )
            .build()
    }

    private fun getParameterNames(directOrder: Boolean, parameter: ConverterParameter) =
        listOf(parameter.nameMapping ?: parameter.name, parameter.name)
            .butIf(parameter.nameMapping != null && !directOrder) { it.reversed() }

    private fun getParameterTransformer(directOrder: Boolean, typeData: ConverterParameter.TypeData): String? {
        val transformer = typeData.customTypeTransformer?.run {
            if (directOrder) to else from
        } ?: run {
            val (sourceType, targetType) = (typeData.sourceType.copy(nullable = false) to
                    typeData.targetType.copy(nullable = false)).butIf(!directOrder) { it.swap() }

            val collectionTransformer =
                getCollectionTransformer(directOrder, sourceType, targetType, typeData.typeArgumentsData)
            val defaultTransformer =
                DefaultTypeTransformers(sourceType, targetType)

            collectionTransformer?.let {
                defaultTransformer?.replace("%N", it) ?: it
            } ?: defaultTransformer
        }

        return if (transformer != null && (if (directOrder) typeData.targetType else typeData.sourceType).isNullable) {
            "%N?.let { ${transformer.replace("%N", "it")} }"
        } else {
            transformer
        }
    }

    private fun getCollectionTransformer(
        directOrder: Boolean,
        sourceType: TypeName,
        targetType: TypeName,
        typeArgumentsData: List<ConverterParameter.TypeData>
    ): String? {
        val argumentTransformers = typeArgumentsData.map { getParameterTransformer(directOrder, it) }
        if (sourceType.isMap && !targetType.isMap || argumentTransformers.none { it != null }) return null
        return when (targetType) {
            MAP, MUTABLE_MAP -> buildString {
                append("%N.entries.associate { ")
                append(argumentTransformers[0]?.replace("%N", "it.key") ?: "it.key")
                append(" to ")
                append(argumentTransformers[1]?.replace("%N", "it.value") ?: "it.value")
                append(" }")
            }

            ITERABLE, MUTABLE_ITERABLE, COLLECTION, MUTABLE_COLLECTION, LIST, MUTABLE_LIST, SET, MUTABLE_SET -> buildString {
                append("%N.map { ")
                append(argumentTransformers.first()!!.replace("%N", "it"))
                append(" }")
            }

            else -> null
        }
    }

    private val TypeName.isMap get() = this == MAP || this == MUTABLE_MAP
}