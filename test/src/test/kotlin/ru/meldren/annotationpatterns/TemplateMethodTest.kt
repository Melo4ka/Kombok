package ru.meldren.annotationpatterns

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TemplateMethodTest {

    @TemplateMethod("solve")
    class Puzzle {

        val operations = mutableListOf<String>()

        fun step1() {
            operations.add("step1")
        }

        fun step2() {
            operations.add("step2")
        }

        @TemplateMethod.Ignore
        fun ignoredStep() {
            operations.add("ignoredStep")
        }

        fun step3() {
            operations.add("step3")
        }
    }

    @Test
    fun `template method should execute all non-ignored steps in order`() {
        val puzzle = Puzzle()
        puzzle.solve()

        val operations = puzzle.operations
        assertEquals(3, operations.size)
        assertEquals("step1", operations[0])
        assertEquals("step2", operations[1])
        assertEquals("step3", operations[2])
    }

    @Test
    fun `template method should not call ignored methods`() {
        val puzzle = Puzzle()
        puzzle.solve()

        assertFalse(puzzle.operations.contains("ignoredStep"))
    }

    @Test
    fun `template method can be called multiple times`() {
        val puzzle = Puzzle()
        puzzle.solve()
        puzzle.solve()

        val operations = puzzle.operations
        assertEquals(6, operations.size)
        assertEquals("step1", operations[0])
        assertEquals("step2", operations[1])
        assertEquals("step3", operations[2])
        assertEquals("step1", operations[3])
        assertEquals("step2", operations[4])
        assertEquals("step3", operations[5])
    }
} 