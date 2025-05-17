package ru.meldren.annotationpatterns

import kotlin.reflect.KClass

/**
 * Applies the Abstract Factory pattern to the annotated class.
 * Generates an interface for creating families of related objects without specifying their concrete classes.
 *
 * @property factoryItems Array of classes for which factory methods will be created.
 * @property functionPrefix Prefix for factory methods.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AbstractFactory(
    val factoryItems: Array<KClass<*>>,
    val functionPrefix: String = ""
)