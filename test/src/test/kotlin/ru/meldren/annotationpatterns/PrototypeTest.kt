package ru.meldren.annotationpatterns

import org.junit.jupiter.api.assertAll
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class PrototypeTest {

    @Prototype
    class PrototypeObject(val intValue: Int)

    data class DataObject(val charValue: Char = 'a')

    class CloneableObject(val stringValue: String = "Abracadabra") : Cloneable {

        public override fun clone() = CloneableObject(stringValue)
    }

    class NonCloneableObject : Cloneable {

        override fun clone() = TODO()
    }

    @Prototype
    class TestPrototypeObject<T>(
        val intValue: Int = 0,
        val `package`: String = "ru.meldren",
        val genericValue: T? = null,
        val prototypeObject: PrototypeObject = PrototypeObject(0),
        val dataObject: DataObject = DataObject(),
        val cloneableObject: CloneableObject? = CloneableObject(),
        val nonCloneableObject: NonCloneableObject = NonCloneableObject()
    )

    @Test
    fun `copy and deep copy should return different object`() {
        val original = TestPrototypeObject<Any>()
        val copied = original.copy()
        val deepCopied = original.deepCopy()

        assertNotSame(original, copied)
        assertNotSame(original, deepCopied)
    }

    @Test
    fun `copy and deep copy should correctly copy primitive`() {
        val original = TestPrototypeObject<Any>()
        val copied = original.copy(intValue = 5)
        val deepCopied = original.deepCopy(intValue = 9)

        assertEquals(5, copied.intValue)
        assertEquals(9, deepCopied.intValue)
    }

    @Test
    fun `copy and deep copy should correctly copy generic values`() {
        val copied = TestPrototypeObject(genericValue = "No").copy(genericValue = "Yes")
        val deepCopied = TestPrototypeObject(genericValue = "Nothing").deepCopy(genericValue = "Something")

        assertEquals("Yes", copied.genericValue)
        assertEquals("Something", deepCopied.genericValue)
    }

    @Test
    fun `deepCopy should call available copy implementations`() {
        val original = TestPrototypeObject<Any>()
        val deepCopied = original.deepCopy()

        assertNotSame(original.prototypeObject, deepCopied.prototypeObject)
        assertNotSame(original.dataObject, deepCopied.dataObject)
        assertNotSame(original.cloneableObject, deepCopied.cloneableObject)
        assertSame(original.nonCloneableObject, deepCopied.nonCloneableObject)
    }
}
