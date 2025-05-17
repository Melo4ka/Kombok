package ru.meldren.annotationpatterns

import kotlin.test.Test
import kotlin.test.assertEquals

class VisitorTest {

    @Visitor(Double::class, "visit")
    abstract class Shape

    class Circle : Shape() {
        val radius = 5.0
    }

    class Rectangle : Shape() {
        val width = 4.0
        val height = 6.0
    }

    class Triangle : Shape() {
        val base = 8.0
        val height = 10.0
    }

    class AreaCalculator : ShapeVisitor {

        override fun visitShape(shape: Shape) = 0.0

        override fun visitCircle(circle: Circle) = Math.PI * circle.radius * circle.radius

        override fun visitRectangle(rectangle: Rectangle) = rectangle.width * rectangle.height

        override fun visitTriangle(triangle: Triangle) = 0.5 * triangle.base * triangle.height
    }

    @Test
    fun `visitor should generate interface with visit methods for each subclass`() {
        val calculator = AreaCalculator()
        val circle = Circle()
        val rectangle = Rectangle()
        val triangle = Triangle()

        val circleArea = calculator.visitCircle(circle)
        val rectangleArea = calculator.visitRectangle(rectangle)
        val triangleArea = calculator.visitTriangle(triangle)

        assertEquals(Math.PI * 25.0, circleArea)
        assertEquals(24.0, rectangleArea)
        assertEquals(40.0, triangleArea)
    }

    @Test
    fun `visitor should handle abstract superclass`() {
        val calculator = AreaCalculator()
        val shape: Shape = Rectangle()

        val area = calculator.visitShape(shape)

        assertEquals(0.0, area)
    }
} 