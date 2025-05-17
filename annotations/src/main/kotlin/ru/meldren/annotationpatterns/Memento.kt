package ru.meldren.annotationpatterns

import java.util.*
import kotlin.reflect.KClass

/**
 * Applies the Memento pattern to the annotated class.
 * Allows saving and restoring previous states of an object without revealing details of its implementation.
 *
 * @property historyClass Class for storing state history. Default is [ArrayList].
 * @property historySizePropertyName Name of the property containing history size.
 * @property saveFunctionName Name of the function to save state.
 * @property restoreFunctionName Name of the function to restore state.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Memento(
    val historyClass: KClass<out MutableList<*>> = ArrayList::class,
    val historySizePropertyName: String = "",
    val saveFunctionName: String = "",
    val restoreFunctionName: String = ""
)