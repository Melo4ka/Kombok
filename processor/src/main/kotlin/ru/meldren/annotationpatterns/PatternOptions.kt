package ru.meldren.annotationpatterns

import ru.meldren.annotationpatterns.util.Options

class PatternOptions(options: Map<String, String>) : Options(options) {

    val builder = BuilderOptions()
    val factory = FactoryOptions()
    val abstractFactory = AbstractFactoryOptions()
    val flyweight = FlyweightOptions()
    val nullObject = NullObjectOptions()
    val memento = MementoOptions()
    val visitor = VisitorOptions()

    inner class BuilderOptions {

        val alwaysGenerateVarargFunctions = option("builder-always-generate-vararg-functions", false)
        val defaultFunctionsPrefix = option("builder-default-functions-prefix", "")
    }

    inner class FactoryOptions {

        val defaultTypeEnumSuffix = option("factory-default-type-enum-suffix", "Type")
    }

    inner class AbstractFactoryOptions {

        val defaultFunctionPrefix = option("abstract-factory-default-function-prefix", "create")
    }

    inner class FlyweightOptions {

        val defaultSuffix = option("flyweight-default-suffix", "s")
    }

    inner class NullObjectOptions {

        val defaultPrefix = option("null-object-default-prefix", "Empty")
    }

    inner class MementoOptions {

        val defaultHistorySizePropertyName = option("memento-default-history-size-property-name", "size")
        val defaultSaveFunctionName = option("memento-default-save-function-name", "save")
        val defaultRestoreFunctionName = option("memento-default-restore-function-name", "restore")
    }

    inner class VisitorOptions {

        val defaultVisitFunctionPrefix = option("visitor-default-visit-function-prefix", "visit")
    }
}