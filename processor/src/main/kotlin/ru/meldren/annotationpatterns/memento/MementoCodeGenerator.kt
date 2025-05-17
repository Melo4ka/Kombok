package ru.meldren.annotationpatterns.memento

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import ru.meldren.annotationpatterns.PatternCodeGenerator
import ru.meldren.annotationpatterns.util.addReturnStatement

class MementoCodeGenerator(
    private val className: ClassName,
    historyType: ClassName,
    private val historySizePropertyName: String,
    private val saveFunctionName: String,
    private val restoreFunctionName: String
) : PatternCodeGenerator {

    private val caretakerClassName = ClassName(className.packageName, "${className.simpleName}Caretaker")
    private val historyType = historyType.parameterizedBy(className)

    override fun generateFile() = FileSpec.builder(caretakerClassName)
        .addType(generateCaretakerClass())

    private fun generateCaretakerClass() = TypeSpec.classBuilder(caretakerClassName)
        .addProperty(generateCaretakerHistoryProperty())
        .addProperty(generateCaretakerHistorySizeProperty())
        .addFunction(generateCaretakerSaveFunction())
        .addFunction(generateCaretakerRestoreFunction())
        .addFunction(generateCaretakerRestoreLastFunction())
        .build()

    private fun generateCaretakerHistoryProperty() = PropertySpec.builder(CARETAKER_HISTORY_PROPERTY_NAME, historyType)
        .addModifiers(KModifier.PRIVATE)
        .initializer("%T()", historyType)
        .build()

    private fun generateCaretakerHistorySizeProperty() = PropertySpec.builder(historySizePropertyName, INT)
        .getter(
            FunSpec.getterBuilder()
                .addReturnStatement("%N.size", CARETAKER_HISTORY_PROPERTY_NAME)
                .build()
        )
        .build()

    private fun generateCaretakerSaveFunction() = FunSpec.builder(saveFunctionName)
        .addParameter(CARETAKER_SAVE_FUNCTION_PROPERTY_NAME, className)
        .addStatement("%N += %N", CARETAKER_HISTORY_PROPERTY_NAME, CARETAKER_SAVE_FUNCTION_PROPERTY_NAME)
        .build()

    private fun generateCaretakerRestoreFunction() = FunSpec.builder(restoreFunctionName)
        .addParameter(CARETAKER_RESTORE_FUNCTION_PROPERTY_NAME, INT)
        .returns(className.copy(nullable = true))
        .addReturnStatement("%N.removeAt(%N)", CARETAKER_HISTORY_PROPERTY_NAME, CARETAKER_RESTORE_FUNCTION_PROPERTY_NAME)
        .build()

    private fun generateCaretakerRestoreLastFunction() = FunSpec.builder(restoreFunctionName)
        .returns(className.copy(nullable = true))
        .addReturnStatement("%N.removeLastOrNull()", CARETAKER_HISTORY_PROPERTY_NAME)
        .build()

    companion object {

        private const val CARETAKER_HISTORY_PROPERTY_NAME = "history"
        private const val CARETAKER_SAVE_FUNCTION_PROPERTY_NAME = "data" //TODO check
        private const val CARETAKER_RESTORE_FUNCTION_PROPERTY_NAME = "index"
    }
} 