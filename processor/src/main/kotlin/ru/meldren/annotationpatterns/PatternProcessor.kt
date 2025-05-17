package ru.meldren.annotationpatterns

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ksp.originatingKSFiles
import com.squareup.kotlinpoet.ksp.writeTo
import ru.meldren.annotationpatterns.util.getAnnotations

class PatternProcessor(
    val options: PatternOptions,
    val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val registry: PatternRegistry
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val unableToProcess = mutableListOf<KSAnnotated>()

        for ((annotationClass, visitorProvider) in registry.patterns) {
            val symbols = resolver
                .getSymbolsWithAnnotation(annotationClass.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

            if (!symbols.iterator().hasNext()) continue

            symbols.forEach { classDeclaration ->
                if (!classDeclaration.validate()) {
                    unableToProcess.add(classDeclaration)
                    return@forEach
                }
                val visitor = visitorProvider(options, logger, resolver)
                classDeclaration.getAnnotations(annotationClass)
                    .mapNotNull { classDeclaration.accept(visitor, it) }
                    .forEach { patternCodeGenerator ->
                        val generatedFile = patternCodeGenerator.generateFile()
                            .indent("\t")
                            .addKotlinDefaultImports(includeJvm = false)
                            .addAnnotation(
                                AnnotationSpec.builder(Suppress::class)
                                    .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
                                    .addMember("\"warnings\"")
                                    .build()
                            )
                            .build()
                        generatedFile.writeTo(codeGenerator, false, generatedFile.originatingKSFiles())
                    }
            }
        }

        return unableToProcess
    }
}