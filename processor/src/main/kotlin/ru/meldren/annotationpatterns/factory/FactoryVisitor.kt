package ru.meldren.annotationpatterns.factory

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import ru.meldren.annotationpatterns.Factory
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.util.defaultValue
import ru.meldren.annotationpatterns.util.get

class FactoryVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(Factory::class, options, logger, resolver) {

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: KSAnnotation
    ): PatternCodeGenerator? {
        if (classDeclaration.classKind != ClassKind.INTERFACE && !(classDeclaration.classKind == ClassKind.CLASS && Modifier.ABSTRACT in classDeclaration.modifiers)) {
            error("Only interface or abstract class can be annotated with @", classDeclaration)
            return null
        }
        val typeEnumSuffix = data[Factory::typeEnumSuffix].ifBlank { options.factory.defaultTypeEnumSuffix }
        val factoryInstances = resolver.getSymbolsWithAnnotation(Factory.Instance::class.qualifiedName!!).toList()
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() && it.superTypes.any { supertype -> supertype.resolve().declaration == classDeclaration } }
            .map { FactoryInstance(it.toClassName(), getParameters(it)) }
        val typeParameters = classDeclaration.typeParameters.map { it.toTypeVariableName() }
        return FactoryCodeGenerator(
            classDeclaration.toClassName(),
            typeEnumSuffix,
            factoryInstances,
            getParameters(classDeclaration),
            typeParameters
        )
    }

    private fun getParameters(classDeclaration: KSClassDeclaration) =
        classDeclaration.primaryConstructor?.parameters?.map {
            FactoryParameter(it.name!!.getShortName(), it.type.toTypeName(), it.defaultValue)
        } ?: emptyList()
}