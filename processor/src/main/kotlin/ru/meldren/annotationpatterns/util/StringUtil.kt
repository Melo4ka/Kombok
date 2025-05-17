@file:Suppress("NOTHING_TO_INLINE")

package ru.meldren.annotationpatterns.util

inline fun String.toSnakeCase() = replace("([a-z])([A-Z])".toRegex(), "$1_$2")