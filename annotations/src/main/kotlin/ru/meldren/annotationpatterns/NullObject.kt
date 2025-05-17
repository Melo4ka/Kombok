package ru.meldren.annotationpatterns

/**
 * Applies the Null Object pattern to the annotated class.
 * Creates a null object that provides default behavior in the absence of a real object.
 *
 * @property prefix Prefix for the name of the null object to be created.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class NullObject(val prefix: String = "")