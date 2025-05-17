package ru.meldren.annotationpatterns

import kotlin.reflect.KClass

/**
 * Applies the Visitor pattern to the annotated class.
 * Generates visit methods that allow adding new operations to an existing object structure
 * without modifying these objects.
 *
 * @property returnType Return type for visit methods. Default is [Unit].
 * @property visitFunctionPrefix Prefix for generated visit methods.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Visitor(
    val returnType: KClass<*> = Unit::class,
    val visitFunctionPrefix: String = ""
)