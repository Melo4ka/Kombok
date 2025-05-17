package ru.meldren.annotationpatterns

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class NullObjectTest {

    private val nullObject = EmptyTestNullObject()

    interface TestParameterizedObject<T, V> {

        fun f(value: T): V
    }

    @NullObject
    interface TestNullObject : TestParameterizedObject<String, Int> {

        fun a() = ":>"

        fun b(): Int

        fun c(): String?

        fun d(): Date

        fun e(value: Int)
    }

    @Test
    fun `implemented function should not be reimplemented in null object`() {
        assertEquals(":>", nullObject.a())
    }

    @Test
    fun `function with primitive return value should return default value`() {
        assertEquals(0, nullObject.b())
    }

    @Test
    fun `function nullable return value should return null`() {
        assertNull(nullObject.c())
    }

    @Test
    fun `function with object return value should throw NotImplementedError`() {
        assertFailsWith<NotImplementedError> { nullObject.d() }
    }

    @Test
    fun `function without return value should be empty`() {
        assertDoesNotThrow { nullObject.e(42) }
    }

    @Test
    fun `function from parameterized supertype should be implemented`() {
        assertEquals(0, nullObject.f("test"))
    }
}