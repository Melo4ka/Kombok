package ru.meldren.annotationpatterns.builder

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.meldren.annotationpatterns.Builder
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.util.defaultValue
import ru.meldren.annotationpatterns.util.get
import ru.meldren.annotationpatterns.util.hasAnnotation

class BuilderVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(Builder::class, options, logger, resolver) {

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
        val parameters = constructor.parameters.map {
            BuilderParameter(
                it.name!!.asString(),
                it.type.toTypeName(),
                it.hasAnnotation<Builder.Vararg>(),
                it.defaultValue
            )
        }
        val builderFunctionsPrefix =
            data[Builder::builderFunctionsPrefix].ifBlank { options.builder.defaultFunctionsPrefix }
        return BuilderCodeGenerator(
            options.builder.alwaysGenerateVarargFunctions,
            data[Builder::builderFunction],
            data[Builder::builderFunctionName],
            builderFunctionsPrefix,
            classDeclaration.toClassName(),
            parameters
        )
    }
}