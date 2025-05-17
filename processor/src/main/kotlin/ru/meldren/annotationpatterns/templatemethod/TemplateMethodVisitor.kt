package ru.meldren.annotationpatterns.templatemethod

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toClassName
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.TemplateMethod
import ru.meldren.annotationpatterns.util.get
import ru.meldren.annotationpatterns.util.hasAnnotation

class TemplateMethodVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(TemplateMethod::class, options, logger, resolver) {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: KSAnnotation): PatternCodeGenerator {
        val functionsToInvoke = getTemplateSteps(classDeclaration).map { it.simpleName.getShortName() }
        return TemplateMethodCodeGenerator(
            classDeclaration.toClassName(),
            data[TemplateMethod::functionName],
            functionsToInvoke
        )
    }

    private fun getTemplateSteps(classDeclaration: KSClassDeclaration) = classDeclaration.getDeclaredFunctions()
        .filter { it.validate() && !it.isConstructor() && it.extensionReceiver == null && it.isPublic() && !it.hasAnnotation<TemplateMethod.Ignore>() }
        .toList()
}