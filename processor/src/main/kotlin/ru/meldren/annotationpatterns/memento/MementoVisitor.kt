package ru.meldren.annotationpatterns.memento

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toClassName
import ru.meldren.annotationpatterns.Memento
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.util.get

class MementoVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(Memento::class, options, logger, resolver) {

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: KSAnnotation
    ): PatternCodeGenerator? {
        if (classDeclaration.classKind != ClassKind.CLASS || Modifier.ABSTRACT in classDeclaration.modifiers) {
            error("Only class can be annotated with @", classDeclaration)
            return null
        }
        val historySizePropertyName =
            data[Memento::historySizePropertyName].ifBlank { options.memento.defaultHistorySizePropertyName }
        val saveFunctionName = data[Memento::saveFunctionName].ifBlank { options.memento.defaultSaveFunctionName }
        val restoreFunctionName =
            data[Memento::restoreFunctionName].ifBlank { options.memento.defaultRestoreFunctionName }
        return MementoCodeGenerator(
            classDeclaration.toClassName(),
            data[Memento::historyClass].toClassName(),
            historySizePropertyName,
            saveFunctionName,
            restoreFunctionName
        )
    }
} 