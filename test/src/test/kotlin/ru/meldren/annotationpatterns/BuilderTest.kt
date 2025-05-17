package ru.meldren.annotationpatterns

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import java.time.DayOfWeek
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class BuilderTest {

    data class TestObject(val stringValue: String)

    @Builder("with", builderFunction = true)
    data class TestBuilderObject(
        val stringValue: String,
        val stringValueWithDefault: String = "Hi",
        val shortNullableValue: Short?,
        val shortNullableValueWithDefault: Short? = 7,
        val intValueWithDefaultReference: Int = stringValue.length + 10,
        val objectValue: TestObject,
        val objectValueWithDefault: TestObject = BuilderTest.TestObject("iH"),
        @Builder.Vararg
        val listValueWithDefault: MutableList<String> = mutableListOf(),
        @Builder.Vararg
        val mapValue: Map<DayOfWeek, Int>
    )

    @Test
    fun `builder should throw exception if property is not initialized and has no default value`() {
        assertFailsWith<IllegalStateException> { testBuilderObject {} }
    }

    @Test
    fun `builder should correctly initialize properties`() {
        val test = testBuilderObject {
            withStringValue("Hello")
            withStringValueWithDefault("Bye")
            withShortNullableValue(42)
            withShortNullableValueWithDefault(null)
            withObjectValue(TestObject("CustomObject"))
            withObjectValueWithDefault(TestObject("DefaultObject"))
            withListValueWithDefault("first", "second", "third")
            withMapValue(DayOfWeek.TUESDAY to 5, DayOfWeek.FRIDAY to 10)
        }

        assertEquals(
            test, TestBuilderObject(
                "Hello",
                "Bye",
                42,
                null,
                15 /* "Hello".length + 10 = 5 + 10 */,
                TestObject("CustomObject"),
                TestObject("DefaultObject"),
                mutableListOf("first", "second", "third"),
                mapOf(DayOfWeek.TUESDAY to 5, DayOfWeek.FRIDAY to 10)
            )
        )
    }
}