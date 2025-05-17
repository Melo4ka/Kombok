package ru.meldren.annotationpatterns

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import kotlin.reflect.KClass

class PatternRegistry {

    private val _patterns =
        mutableMapOf<KClass<out Annotation>, (PatternOptions, KSPLogger, Resolver) -> PatternVisitor>()
    val patterns get() = _patterns.toMap()

    fun register(
        annotationClass: KClass<out Annotation>,
        visitorProvider: (PatternOptions, KSPLogger, Resolver) -> PatternVisitor
    ) {
        _patterns[annotationClass] = visitorProvider
    }

    inline fun <reified A : Annotation> register(
        noinline visitorProvider: (PatternOptions, KSPLogger, Resolver) -> PatternVisitor
    ) = register(A::class, visitorProvider)
}