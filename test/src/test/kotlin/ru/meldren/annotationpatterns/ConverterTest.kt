package ru.meldren.annotationpatterns

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class ConverterTest {

    enum class FirstEnum {

        A,
        B,
        C;
    }

    enum class SecondEnum {

        A,
        B,
        C;
    }

    data class PersonProfile(
        val firstName: String,
        val familyName: String,
        val age: Int
    )

    data class PersonDto(
        val firstName: String,
        val familyName: String,
        val age: Int,
        val married: String?,
        val enum: SecondEnum,
        val iterable: Set<String>,
        val list: List<Int>,
        val map: Map<String?, String>,
        val nestedMap: MutableMap<Map<String, String>, Int>
    )

    @Converter(PersonDto::class)
    @Converter(PersonProfile::class, from = false)
    data class Person(
        val firstName: String,
        @Converter.Name("familyName")
        val lastName: String,
        val age: String,
        @Converter.Type("%N.toString()", "%N.toBooleanStrict()")
        val married: Boolean?,
        val enum: FirstEnum,
        val iterable: Iterable<String>,
        val list: List<String>,
        val map: Map<@Converter.Type("(%N / 2).toString()", "%N.toInt() * 2") Int?, String>,
        val nestedMap: Map<Map<Int, String>, String>
    )

    @Test
    fun `class should be converted to target class with as many provided properties as possible`() {
        val person = Person(
            firstName = "Alice",
            lastName = "Wonderland",
            age = "28",
            married = null,
            enum = FirstEnum.A,
            iterable = emptySet(),
            list = emptyList(),
            map = emptyMap(),
            nestedMap = mutableMapOf()
        )
        val personProfile = person.toPersonProfile()

        assertEquals(person.firstName, personProfile.firstName)
        assertEquals(person.lastName, personProfile.familyName)
        assertEquals(person.age, personProfile.age.toString())
    }

    @Test
    fun `default and custom converters should be used and handle nullability`() {
        val person = Person(
            firstName = "Scarlet",
            lastName = "Witch",
            age = "36",
            married = true,
            enum = FirstEnum.A,
            iterable = setOf("item1", "item2"),
            list = listOf("1", "2", "3"),
            map = mapOf(null to "one", 2 to "two"),
            nestedMap = mapOf(mapOf(1 to "inner") to "777")
        )

        assertEquals(person, person.toPersonDto().toPerson())
    }
}