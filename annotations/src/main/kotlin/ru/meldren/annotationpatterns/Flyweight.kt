package ru.meldren.annotationpatterns

import java.util.HashMap
import kotlin.reflect.KClass

/**
 * Applies the Flyweight pattern to the annotated class.
 * Generates a cache for reusing immutable objects,
 * minimizing memory consumption.
 *
 * @property mapClass Map class to use as a cache. Default is [HashMap].
 * @property suffix Suffix for the generated object.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Flyweight(
    val mapClass: KClass<out Map<*, *>> = HashMap::class,
    val suffix: String = ""
)
