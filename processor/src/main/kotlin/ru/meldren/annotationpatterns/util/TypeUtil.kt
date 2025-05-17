@file:Suppress("NOTHING_TO_INLINE")

package ru.meldren.annotationpatterns.util

import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.symbol.impl.kotlin.KSValueParameterImpl
import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

val UUID = ClassName("java.util", "UUID")
val DATE = ClassName("java.util", "Date")

val KSValueParameter.defaultValue
    get() = (this as? KSValueParameterImpl)?.ktParameter?.defaultValue?.text

val KSType.name
    get() = declaration.qualifiedName!!.asString()

val Nullability.symbol
    get() = if (this == Nullability.NULLABLE) "?" else ""

val ClassName.defaultPrimitiveValue
    get() = when (this) {
        INT -> "0"
        LONG -> "0L"
        FLOAT -> "0.0f"
        DOUBLE -> "0.0"
        BOOLEAN -> "false"
        STRING -> "\"\""
        else -> null
    }

val TypeName.rawType get() = if (this is ParameterizedTypeName) rawType else (this as ClassName)

inline fun <A : Annotation> KSAnnotated.getAnnotations(annotationClass: KClass<A>) = annotations.filter {
    it.shortName.getShortName() == annotationClass.simpleName &&
            it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationClass.qualifiedName
}.toList()

inline fun <A : Annotation> KSAnnotated.getAnnotation(clazz: KClass<A>) = getAnnotations(clazz).firstOrNull()

inline fun <A : Annotation> KSAnnotated.hasAnnotation(clazz: KClass<A>) = getAnnotations(clazz).isNotEmpty()

inline fun <reified A : Annotation> KSAnnotated.getAnnotations() = getAnnotations(A::class)

inline fun <reified A : Annotation> KSAnnotated.getAnnotation() = getAnnotation(A::class)

inline fun <reified A : Annotation> KSAnnotated.hasAnnotation() = hasAnnotation(A::class)

inline operator fun <reified T> KSAnnotation.get(
    property: KProperty<T>,
    parser: (Any) -> T = { it as T }
) = arguments
    .first { property.name == it.name!!.getShortName() }
    .value!!.let(parser)

inline operator fun KSAnnotation.get(property: KProperty<KClass<*>>) =
    get(property) { (it as KSType).declaration } as KSClassDeclaration

@Suppress("UNCHECKED_CAST")
inline operator fun KSAnnotation.get(property: KProperty<Array<KClass<*>>>) =
    get(property) { (it as List<KSType>).map(KSType::declaration) } as List<KSClassDeclaration>

