package ru.meldren.annotationpatterns

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AbstractFactoryTest {

    interface Button {

        fun render(): String
    }

    interface CheckBox {

        fun render(): String
    }

    class WindowsButton : Button {

        override fun render() = "Windows Button"
    }

    class WindowsCheckBox : CheckBox {

        override fun render() = "Windows CheckBox"
    }

    class MacOSButton : Button {

        override fun render() = "MacOS Button"
    }

    class MacOSCheckBox : CheckBox {

        override fun render() = "MacOS CheckBox"
    }

    @AbstractFactory([Button::class, CheckBox::class], "create")
    interface GUI

    class WindowsFactory : GUIFactory {

        override fun createButton() = WindowsButton()

        override fun createCheckBox() = WindowsCheckBox()
    }

    class MacOSFactory : GUIFactory {

        override fun createButton() = MacOSButton()

        override fun createCheckBox() = MacOSCheckBox()
    }

    class Application(private val factory: GUIFactory) {

        fun createUI(): Pair<Button, CheckBox> {
            val button = factory.createButton()
            val checkbox = factory.createCheckBox()
            return button to checkbox
        }
    }

    @Test
    fun `abstract factory should generate interface with factory methods`() {
        val windowsFactory = WindowsFactory()

        val button = windowsFactory.createButton()
        val checkbox = windowsFactory.createCheckBox()

        assertIs<WindowsButton>(button)
        assertIs<WindowsCheckBox>(checkbox)
        assertEquals("Windows Button", button.render())
        assertEquals("Windows CheckBox", checkbox.render())
    }

    @Test
    fun `abstract factory should create family of related objects`() {
        val windowsFactory = WindowsFactory()
        val macFactory = MacOSFactory()

        val windowsApp = Application(windowsFactory)
        val macApp = Application(macFactory)

        val (windowsButton, windowsCheckbox) = windowsApp.createUI()
        val (macButton, macCheckbox) = macApp.createUI()

        assertEquals("Windows Button", windowsButton.render())
        assertEquals("Windows CheckBox", windowsCheckbox.render())

        assertEquals("MacOS Button", macButton.render())
        assertEquals("MacOS CheckBox", macCheckbox.render())
    }
} 