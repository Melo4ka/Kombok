package ru.meldren.annotationpatterns

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import ru.meldren.annotationpatterns.abstractfactory.AbstractFactoryVisitor
import ru.meldren.annotationpatterns.builder.BuilderVisitor
import ru.meldren.annotationpatterns.converter.ConverterVisitor
import ru.meldren.annotationpatterns.factory.FactoryVisitor
import ru.meldren.annotationpatterns.flyweight.FlyweightVisitor
import ru.meldren.annotationpatterns.memento.MementoVisitor
import ru.meldren.annotationpatterns.nullobject.NullObjectVisitor
import ru.meldren.annotationpatterns.prototype.PrototypeVisitor
import ru.meldren.annotationpatterns.templatemethod.TemplateMethodVisitor
import ru.meldren.annotationpatterns.visitor.VisitorVisitor

class PatternProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment) = PatternProcessor(
        PatternOptions(environment.options),
        environment.logger,
        environment.codeGenerator,
        createRegistry()
    )

    private fun createRegistry() = PatternRegistry().apply {
        register<Builder>(::BuilderVisitor)
        register<Factory>(::FactoryVisitor)
        register<Flyweight>(::FlyweightVisitor)
        register<Prototype>(::PrototypeVisitor)
        register<NullObject>(::NullObjectVisitor)
        register<Converter>(::ConverterVisitor)
        register<AbstractFactory>(::AbstractFactoryVisitor)
        register<TemplateMethod>(::TemplateMethodVisitor)
        register<Visitor>(::VisitorVisitor)
        register<Memento>(::MementoVisitor)
    }
}