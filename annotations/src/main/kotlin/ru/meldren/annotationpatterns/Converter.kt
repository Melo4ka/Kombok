package ru.meldren.annotationpatterns

import kotlin.reflect.KClass

/**
 * Applies the Converter pattern to the annotated class.
 * Generates methods for converting objects from one type to another.
 *
 * @property targetClass Target class for conversion.
 * @property to Flag indicating whether to create a conversion method "to" the target type.
 * @property from Flag indicating whether to create a conversion method "from" the target type.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Converter(
    val targetClass: KClass<out Any>,
    val to: Boolean = true,
    val from: Boolean = true
) {

    /**
     * Sets field name mapping during conversion.
     *
     * @property mapping Name of the field to which the annotated parameter will be mapped.
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Name(val mapping: String)

    /**
     * Defines conversion types for the annotated parameter or type.
     *
     * @property to Expression for conversion to the target type.
     * @property from Expression for conversion from the target type.
     */
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type(
        val to: String,
        val from: String
    )
}