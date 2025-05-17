package ru.meldren.annotationpatterns.prototype

typealias PrototypeCopyFunction = Pair<String, Any>

val PrototypeCopyFunction.accessor get() = first
val PrototypeCopyFunction.argument get() = second