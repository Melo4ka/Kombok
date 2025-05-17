package ru.meldren.annotationpatterns.visitor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toClassName
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.Visitor
import ru.meldren.annotationpatterns.util.get

class VisitorVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(Visitor::class, options, logger, resolver) {

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: KSAnnotation
    ): PatternCodeGenerator {
        val visitFunctionPrefix =
            data[Visitor::visitFunctionPrefix].ifBlank { options.visitor.defaultVisitFunctionPrefix }
        val classesToVisit = getSubclasses(classDeclaration).map { it.toClassName() }
        return VisitorCodeGenerator(
            classDeclaration.toClassName(),
            visitFunctionPrefix,
            data[Visitor::returnType].toClassName(),
            classesToVisit
        )
    }

    private fun getSubclasses(classDeclaration: KSClassDeclaration) = resolver.getNewFiles()
        .flatMap { it.declarations }
        .filterIsInstance<KSClassDeclaration>()
        .flatMap { it.getNestedClasses() }
        .filter { it.validate() && it.superTypes.any { supertype -> supertype.resolve().declaration == classDeclaration } }
        .toList()

    private fun KSClassDeclaration.getNestedClasses(): Sequence<KSClassDeclaration> = declarations
        .filterIsInstance<KSClassDeclaration>()
        .flatMap { it.getNestedClasses() } + this
} 