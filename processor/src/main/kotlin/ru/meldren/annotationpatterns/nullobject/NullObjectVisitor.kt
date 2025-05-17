package ru.meldren.annotationpatterns.nullobject

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.meldren.annotationpatterns.NullObject
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.PatternOptions
import ru.meldren.annotationpatterns.PatternVisitor
import ru.meldren.annotationpatterns.util.get

class NullObjectVisitor(
    options: PatternOptions,
    logger: KSPLogger,
    resolver: Resolver
) : PatternVisitor(NullObject::class, options, logger, resolver) {

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: KSAnnotation
    ): PatternCodeGenerator? {
        if (classDeclaration.classKind != ClassKind.INTERFACE) {
            error("Only interface can be annotated with @", classDeclaration)
            return null
        }
        val functions = classDeclaration.getAllFunctions().toList()
            .filter { it.isAbstract }
            .map { functionDeclaration ->
                if (functionDeclaration.typeParameters.isNotEmpty()) {
                    error("@ functions can't have type parameters", functionDeclaration)
                    return null
                }
                NullObjectFunction(
                    functionDeclaration.simpleName.asString(),
                    functionDeclaration.returnType!!.resolve().toClassName(),
                    functionDeclaration.parameters.map { parameter ->
                        NullObjectFunction.FunctionParameter(
                            parameter.name!!.getShortName(),
                            parameter.type.toTypeName()
                        )
                    }
                )
            }
        val prefix = data[NullObject::prefix].ifBlank { options.nullObject.defaultPrefix }
        return NullObjectCodeGenerator(
            prefix,
            classDeclaration.toClassName(),
            functions
        )
    }
}