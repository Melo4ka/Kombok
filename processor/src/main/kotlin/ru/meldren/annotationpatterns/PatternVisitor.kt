package ru.meldren.annotationpatterns

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import kotlin.reflect.KClass

open class PatternVisitor(
    private val annotationClass: KClass<out Annotation>,
    protected val options: PatternOptions,
    protected val logger: KSPLogger,
    protected val resolver: Resolver
) : KSVisitor<KSAnnotation, PatternCodeGenerator?> {

    override fun visitAnnotated(annotated: KSAnnotated, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitAnnotation(annotation: KSAnnotation, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitCallableReference(reference: KSCallableReference, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitClassifierReference(reference: KSClassifierReference, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitDeclaration(declaration: KSDeclaration, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitDeclarationContainer(
        declarationContainer: KSDeclarationContainer,
        data: KSAnnotation
    ): PatternCodeGenerator? = null

    override fun visitDefNonNullReference(reference: KSDefNonNullReference, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitDynamicReference(reference: KSDynamicReference, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitFile(file: KSFile, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitModifierListOwner(modifierListOwner: KSModifierListOwner, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitNode(node: KSNode, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitParenthesizedReference(reference: KSParenthesizedReference, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitPropertyAccessor(accessor: KSPropertyAccessor, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitPropertyGetter(getter: KSPropertyGetter, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitPropertySetter(setter: KSPropertySetter, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitReferenceElement(element: KSReferenceElement, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitTypeAlias(typeAlias: KSTypeAlias, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitTypeReference(typeReference: KSTypeReference, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitValueArgument(valueArgument: KSValueArgument, data: KSAnnotation): PatternCodeGenerator? = null

    override fun visitValueParameter(valueParameter: KSValueParameter, data: KSAnnotation): PatternCodeGenerator? = null

    protected fun error(message: String, symbol: KSNode) = logger.error(
        message.replace("@", "@${annotationClass.simpleName}"),
        symbol
    )
}