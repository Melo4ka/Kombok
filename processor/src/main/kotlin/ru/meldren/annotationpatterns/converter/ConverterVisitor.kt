package ru.meldren.annotationpatterns.converter

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ENUM
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.meldren.annotationpatterns.Converter
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.util.get
import ru.meldren.annotationpatterns.util.getAnnotation
import ru.meldren.annotationpatterns.util.rawType

class ConverterVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(Converter::class, options, logger, resolver) {

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: KSAnnotation
    ): PatternCodeGenerator? {
        if (classDeclaration.classKind != ClassKind.CLASS || Modifier.ABSTRACT in classDeclaration.modifiers) {
            error("Only class can be annotated with @", classDeclaration)
            return null
        }
        val targetClassDeclaration = data[Converter::targetClass]
        val constructor = classDeclaration.primaryConstructor
        val targetConstructor = targetClassDeclaration.primaryConstructor
        if (constructor == null || targetConstructor == null) {
            error("Class and its target class must have primary constructors", classDeclaration)
            return null
        }
        val parameters = constructor.parameters.mapNotNull { parameter ->
            val name = parameter.name!!.getShortName()
            val nameMapping = parameter.getAnnotation<Converter.Name>()?.get(Converter.Name::mapping)
            val targetParameter =
                targetConstructor.parameters.find { it.name!!.getShortName() == (nameMapping ?: name) }
                    ?: return@mapNotNull null
            ConverterParameter(name, nameMapping, getTypeData(parameter, parameter.type, targetParameter.type))
        }
        return ConverterCodeGenerator(
            classDeclaration.toClassName(),
            targetClassDeclaration.toClassName(),
            parameters,
            data[Converter::to],
            data[Converter::from]
        )
    }

    private fun getTypeData(
        annotated: KSAnnotated,
        type: KSTypeReference,
        targetType: KSTypeReference
    ): ConverterParameter.TypeData {
        val resolvedType = type.resolve()
        val resolvedTargetType = targetType.resolve()
        return ConverterParameter.TypeData(
            resolvedType.toHandledTypeName(),
            resolvedTargetType.toHandledTypeName(),
            annotated.getAnnotation<Converter.Type>()?.let { it[Converter.Type::to] to it[Converter.Type::from] },
            resolvedType.arguments.mapIndexed { index, typeArgument ->
                getTypeData(typeArgument, typeArgument.type!!, resolvedTargetType.arguments[index].type!!)
            }
        )
    }

    private fun KSType.toHandledTypeName() =
        if ((declaration as KSClassDeclaration).classKind == ClassKind.ENUM_CLASS) {
            ENUM
        } else {
            toTypeName().rawType
        }
}