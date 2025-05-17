package ru.meldren.annotationpatterns

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FactoryTest {

    @Factory
    abstract class TestFactoryObject<T : Any>(
        val stringValue: String = "",
        val intValue: Int = 0,
        val booleanValue: Boolean = false
    )

    @Factory.Instance
    class FactoryInstance(stringValue: String, intValue: Int, booleanValue: Boolean) :
        TestFactoryObject<Int>(stringValue, intValue, booleanValue)

    @Factory.Instance
    class OneMoreFactoryInstance(intValue: Int, booleanValue: Boolean) :
        TestFactoryObject<String>(intValue = intValue, booleanValue = booleanValue)

    @Factory.Instance
    class AnotherFactoryInstance(booleanValue: Boolean) :
        TestFactoryObject<Boolean>(booleanValue = booleanValue)

    @Test
    fun `factory should create type enum for each factory instance`() {
        assertEquals(3, TestFactoryObjectType.entries.size)
    }

    @Test
    fun `factory should provide type corresponding instance`() {
        assertIs<FactoryInstance>(TestFactoryObject(TestFactoryObjectType.FACTORY_INSTANCE))
        assertIs<OneMoreFactoryInstance>(TestFactoryObject(TestFactoryObjectType.ONE_MORE_FACTORY_INSTANCE))
        assertIs<AnotherFactoryInstance>(TestFactoryObject(TestFactoryObjectType.ANOTHER_FACTORY_INSTANCE))
    }

    @Test
    fun `factory should provide constructor parameters to instance`() {
        val instance = TestFactoryObject(
            TestFactoryObjectType.FACTORY_INSTANCE,
            "123",
            5,
            true
        )

        assertIs<FactoryInstance>(TestFactoryObject(TestFactoryObjectType.FACTORY_INSTANCE))
        assertEquals("123", instance.stringValue)
        assertEquals(5, instance.intValue)
        assertEquals(true, instance.booleanValue)
    }
}