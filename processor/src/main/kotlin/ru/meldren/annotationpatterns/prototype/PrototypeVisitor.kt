package ru.meldren.annotationpatterns.prototype

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.Prototype
import ru.meldren.annotationpatterns.util.get
import ru.meldren.annotationpatterns.util.hasAnnotation
import ru.meldren.annotationpatterns.util.name

class PrototypeVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(Prototype::class, options, logger, resolver) {

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: KSAnnotation
    ): PatternCodeGenerator? {
        if (classDeclaration.classKind != ClassKind.CLASS || Modifier.ABSTRACT in classDeclaration.modifiers) {
            error("Only class can be annotated with @", classDeclaration)
            return null
        }
        val constructor = classDeclaration.primaryConstructor
        if (constructor == null) {
            error("Class must have primary constructor with at least one parameter", classDeclaration)
            return null
        }
        val typeParameterResolver = classDeclaration.typeParameters.toTypeParameterResolver()
        val parameters = constructor.parameters.map {
            val resolvedType = it.type.resolve()
            val typeName = resolvedType.toTypeName(typeParameterResolver)
            val copyFunction = (resolvedType.declaration as? KSClassDeclaration)?.let { parameterClassDeclaration ->
                PrototypeCopyFunctionType.entries
                    .find { type -> type.applicablePredicate(parameterClassDeclaration) }
                    ?.argumentProvider
                    ?.invoke(parameterClassDeclaration)
            }
            PrototypeParameter(it.name!!.asString(), typeName, copyFunction)
        }
        val typeParameters = classDeclaration.typeParameters.map { it.toTypeVariableName() }
        return PrototypeCodeGenerator(
            data[Prototype::copy],
            data[Prototype::deepCopy],
            classDeclaration.toClassName(),
            parameters,
            typeParameters
        )
    }

    private enum class PrototypeCopyFunctionType(
        val applicablePredicate: (KSClassDeclaration) -> Boolean,
        val argumentProvider: (KSClassDeclaration) -> PrototypeCopyFunction
    ) {

        PROTOTYPE(
            { it.hasAnnotation<Prototype>() },
            { "%M" to MemberName(it.packageName.asString(), "deepCopy", isExtension = true) }
        ),
        DATA(
            { Modifier.DATA in it.modifiers },
            { "%N" to "copy" }
        ),
        CLONEABLE(
            {
                it.getAllSuperTypes().any { supertype ->
                    supertype.name == "kotlin.Cloneable"
                } && it.getAllFunctions().any { function ->
                    function.simpleName.asString() == "clone" && function.isPublic()
                }
            },
            { "%N" to "clone" }
        );
    }
}