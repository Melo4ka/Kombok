package ru.meldren.annotationpatterns.flyweight

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.meldren.annotationpatterns.Flyweight
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.util.get

class FlyweightVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(Flyweight::class, options, logger, resolver) {

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
            error("Class must have primary constructor", classDeclaration)
            return null
        }
        if (classDeclaration.typeParameters.isNotEmpty()) {
            error("Class must not have type parameters", classDeclaration)
            return null
        }
        if (constructor.parameters.count { !it.hasDefault } != 1) {
            error("Class must have primary constructor with one parameter", classDeclaration)
            return null
        }
        val keyParameter = constructor.parameters.first { !it.hasDefault }
        val mapClass = data[Flyweight::mapClass]
        if (mapClass.getConstructors().none { it.parameters.all(KSValueParameter::hasDefault) }) {
            error("Provided map implementation must have default constructor", classDeclaration)
            return null
        }
        val suffix = data[Flyweight::suffix].ifBlank { options.flyweight.defaultSuffix }
        return FlyweightCodeGenerator(
            classDeclaration.toClassName(),
            suffix,
            mapClass.toClassName(),
            keyParameter.name!!.getShortName(),
            keyParameter.type.toTypeName()
        )
    }
}