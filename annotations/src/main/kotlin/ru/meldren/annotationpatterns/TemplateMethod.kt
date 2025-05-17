package ru.meldren.annotationpatterns

/**
 * Applies the Template Method pattern to the annotated class.
 * Generates a template method that defines the skeleton of an algorithm,
 * delegating specific steps to subclasses.
 *
 * @property functionName Name of the template method to be generated.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class TemplateMethod(val functionName: String) {

    /**
     * Marks methods that should be excluded from the template method.
     */
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Ignore
}