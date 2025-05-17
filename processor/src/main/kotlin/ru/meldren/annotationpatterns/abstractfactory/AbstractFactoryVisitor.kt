package ru.meldren.annotationpatterns.abstractfactory

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName
import ru.meldren.annotationpatterns.AbstractFactory
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.util.get

class AbstractFactoryVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(AbstractFactory::class, options, logger, resolver) {

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: KSAnnotation
    ): PatternCodeGenerator? {
        val factoryItems = data.get(AbstractFactory::factoryItems).map { it.toClassName() }
        if (factoryItems.isEmpty()) {
            error("@ must specify at least one factory item", classDeclaration)
            return null
        }
        val functionPrefix =
            data[AbstractFactory::functionPrefix].ifBlank { options.abstractFactory.defaultFunctionPrefix }
        return AbstractFactoryCodeGenerator(
            classDeclaration.toClassName(),
            functionPrefix,
            factoryItems
        )
    }
} 