package ru.meldren.annotationpatterns

/**
 * Applies the Builder pattern to the annotated class.
 * Creates methods for step-by-step construction of complex objects.
 *
 * @property builderFunctionsPrefix Prefix for builder methods.
 * @property builderFunction Flag indicating whether to create a top-level builder function.
 * @property builderFunctionName Name of the builder function if [builderFunction] is true.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Builder(
    val builderFunctionsPrefix: String = "",
    val builderFunction: Boolean = false,
    val builderFunctionName: String = ""
) {

    /**
     * When this annotation is applied, an additional builder function will be generated
     * that accepts a variable number of arguments for the annotated parameter.
     * This improves usability when passing multiple values, especially for collections or arrays.
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Vararg
}