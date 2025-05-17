package ru.meldren.annotationpatterns.util

open class Options(private val options: Map<String, String>) {

    protected fun <T> option(key: String, defaultValue: T, parser: (String) -> T) =
        options[key]?.let(parser) ?: defaultValue

    protected fun option(key: String, defaultValue: String) = option(key, defaultValue) { it }

    protected fun option(key: String, defaultValue: Boolean) = option(key, defaultValue, String::toBoolean)
}