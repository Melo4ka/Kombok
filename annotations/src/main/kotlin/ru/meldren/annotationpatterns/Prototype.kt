package ru.meldren.annotationpatterns

/**
 * Applies the Prototype pattern to the annotated class.
 * Generates methods for cloning objects based on existing instances.
 *
 * @property copy Flag indicating whether to create a shallow copy method.
 * @property deepCopy Flag indicating whether to create a deep copy method.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Prototype(
    val copy: Boolean = true,
    val deepCopy: Boolean = true
)
