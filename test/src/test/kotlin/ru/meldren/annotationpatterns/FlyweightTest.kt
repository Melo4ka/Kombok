package ru.meldren.annotationpatterns

import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertSame

class FlyweightTest {

    class TestMap<K, V> : HashMap<K, V>() {

        override fun put(key: K, value: V) = throw Exception("Put was executed.")
    }

    @Flyweight(TestMap::class, "s")
    class TestFlyweightWithCustomMapObject(val key: Int)

    @Flyweight(suffix = "s")
    class TestFlyweightWithDefaultMapObject(val key: Int)

    @Test
    fun `flyweight should return same instance for same key`() {
        val first = TestFlyweightWithDefaultMapObjects(1)
        val second = TestFlyweightWithDefaultMapObjects(1)

        assertSame(first, second)
    }

    @Test
    fun `flyweight should use provided map implementation`() {
        assertFails { TestFlyweightWithCustomMapObjects(9) }
    }
}