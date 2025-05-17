package ru.meldren.annotationpatterns

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MementoTest {

    @Memento(
        saveFunctionName = "saveState",
        restoreFunctionName = "restoreState",
        historySizePropertyName = "historySize"
    )
    data class TextEditor(
        var content: String = "",
        var cursorPosition: Int = 0
    ) {

        fun setText(text: String) {
            content = text
            cursorPosition = text.length
        }
    }

    @Test
    fun `memento should save editor state`() {
        val textEditor = TextEditor()
        val caretaker = TextEditorCaretaker()

        textEditor.setText("Hello, World!")
        caretaker.saveState(textEditor.copy())

        assertEquals(1, caretaker.historySize)
    }

    @Test
    fun `memento should restore previous editor state`() {
        val textEditor = TextEditor()
        val caretaker = TextEditorCaretaker()

        textEditor.setText("Hello, World!")
        caretaker.saveState(textEditor.copy())

        textEditor.setText("Modified content")
        caretaker.saveState(textEditor.copy())

        textEditor.setText("Unsaved changes")

        val expectedRestoredTextEditor = TextEditor()
        expectedRestoredTextEditor.setText("Modified content")

        val restored = caretaker.restoreState()

        assertEquals("Modified content", restored?.content)
        assertEquals(restored, expectedRestoredTextEditor)
    }

    @Test
    fun `memento should track history size`() {
        val textEditor = TextEditor()
        val caretaker = TextEditorCaretaker()

        assertEquals(0, caretaker.historySize)

        textEditor.setText("First state")
        caretaker.saveState(textEditor)
        assertEquals(1, caretaker.historySize)

        textEditor.setText("Second state")
        caretaker.saveState(textEditor)
        assertEquals(2, caretaker.historySize)

        caretaker.restoreState(0)
        assertEquals(1, caretaker.historySize)
    }
} 