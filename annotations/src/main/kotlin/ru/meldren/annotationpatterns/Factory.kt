package ru.meldren.annotationpatterns

/**
 * Applies the Factory Method pattern to the annotated class.
 * Creates a factory method that delegates object creation to subclasses.
 *
 * @property typeEnumSuffix Suffix for the name of the enum class to be created.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Factory(val typeEnumSuffix: String = "") {

    /**
     * Marks classes that are instances created by the factory.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Instance
}
