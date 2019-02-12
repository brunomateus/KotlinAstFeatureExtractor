package fr.uphf.ast

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class ASTExtractor : KtTreeVisitorVoid() {

	lateinit var rootNode: ASTNode
	lateinit var currentAstNode: ASTNode

	fun getASTInJSON(file: KtFile): ASTNode {

		rootNode = ASTNode(type=file.javaClass.simpleName,
				label="")

		currentAstNode = rootNode
		visitKtFile(file)
		return rootNode
	}


	override fun visitKtElement(element: KtElement) {

		val granChildren = emptyList<ASTNode>().toMutableList()
		val label = when(element) {
			is KtConstructorDelegationReferenceExpression -> element.text
			is KtConstructorDelegationCall -> ""
			is KtClassBody -> ""
			is KtClassInitializer -> ""
			is KtSuperTypeList -> ""
			is KtParameterList -> ""
			is KtImportList -> ""
			is KtDelegatedSuperTypeEntry -> ""
			is KtPropertyDelegate -> ""

			//is KtContainerNode -> "" // should we keep empty
			is KtContainerNodeForControlStructureBody -> ""
			is KtPostfixExpression -> ""
			is KtPrefixExpression -> ""

			is KtAnnotationEntry -> "@${if (element.useSiteTarget != null) element.useSiteTarget?.text + ":" else ""}${element.typeReference?.text}"
			is KtAnnotationUseSiteTarget -> element.text
			is KtAnnotation -> element.text
			is KtParameter -> if (element.valOrVarKeyword != null) "${element.valOrVarKeyword?.text} ${element.name}" else element.name
			is KtLambdaArgument -> ""

			//KtDeclaration
			is KtEnumEntry -> element.name
			is KtClass -> element.name
			is KtObjectDeclaration -> element.name
			is KtAnonymousInitializer -> element.text //TODO
			is KtNamedFunction -> element.name
			is KtDestructuringDeclaration -> ""
			is KtProperty -> "${element.valOrVarKeyword.text} ${element.name}" // should we inform if it is val or var ? if yes, how ?
			is KtTypeAlias -> element.name
			is KtSecondaryConstructor -> ""
			// end KtDeclaration
			//KtExpression
			is KtIfExpression -> "if"
			is KtTryExpression -> "try"
			is KtForExpression -> "for"
			is KtWhileExpression -> "while"
			is KtDoWhileExpression -> "do-while"
			is KtFinallySection -> "finally"
			is KtBinaryExpression -> ""
			is KtQualifiedExpression -> ""
			is KtUnaryExpression -> element.text //TODO
			is KtIsExpression -> ""
			is KtCallableReferenceExpression -> element.text
			is KtClassLiteralExpression -> element.text //TODO
			is KtParenthesizedExpression -> element.text
			is KtStringTemplateExpression -> element.text
			is KtConstantExpression -> element.text
			is KtBlockExpression -> ""
			is KtFunctionLiteral -> ""
			is KtLambdaExpression -> ""
			is KtThisExpression -> element.text
			is KtWhenExpression -> "when"
			is KtObjectLiteralExpression -> element.text //TODO
			is KtThrowExpression -> "throw"
			is KtReturnExpression -> "return"
			is KtContinueExpression -> "continue"
			is KtBreakExpression -> "break"
			is KtCollectionLiteralExpression -> element.text //TODO
			is KtSimpleNameExpression -> element.text
			is KtLabeledExpression -> element.getLabelName()
			is KtLabelReferenceExpression -> element.getReferencedName()
			is KtAnnotatedExpression -> ""
			is KtCallExpression -> element.calleeExpression?.text
			is KtArrayAccessExpression -> ""
			// KtExpression
			is KtImportDirective -> element.importedReference?.text
			is KtPrimaryConstructor -> ""
			is KtPropertyAccessor -> if (element.isGetter) "get" else if (element.isSetter) "set" else ""
			is KtPackageDirective -> element.packageNameExpression?.text
			is KtModifierListOwner -> element.text //TODO: Should come after PrimaryContructor and PropertyAccessor
			is KtDeclarationModifierList -> {
				var modifiersList = element.text

				for (annotation in element.annotations){
					modifiersList = modifiersList.replace(annotation.text,  "")
				}

				for (annotation in element.annotationEntries){
					modifiersList = modifiersList.replace(annotation.text,  "")
				}

				if(modifiersList.isNotBlank()){

					for(modifier in modifiersList.trim().split(" ")){
						granChildren.add(ASTNode(type="ModifierEntry", label=modifier))
					}
				}
				""
			}
			is KtModifierList -> element.text

			is KtSuperTypeCallEntry -> ""
			is KtDestructuringDeclarationEntry -> element.text
			is KtStringTemplateEntry -> element.text //TODO
			is KtSuperExpression -> "super"
			is KtCatchClause -> "catch"
			is KtTypeProjection -> element.text //TODO
			is KtTypeConstraint -> element.text //TODO
			is KtTypeParameter -> element.text //TODO
			is KtTypeArgumentList -> element.text //TODO
			is KtTypeReference -> element.text //TODO
			is KtTypeElement -> element.text //TODO
			is KtValueArgument -> if (element.getArgumentExpression() is KtLambdaExpression) "" else element.getArgumentExpression()?.text//named ?
			is KtValueArgumentName -> element.asName.asString() //TODO Do not visit its child
			is KtValueArgumentList -> ""
			is KtWhenConditionWithExpression -> element.text
			is KtWhenConditionInRange -> element.text
			is KtWhenConditionIsPattern -> element.text
			is KtWhenEntry -> if (element.isElse) "else" else ""

			else -> ""
		}

		val childNode = ASTNode(type=element.javaClass.simpleName, label=label ?: "", children = granChildren)
		currentAstNode.addChild(childNode)
		val previousAstNode = currentAstNode
		currentAstNode = childNode
		super.visitKtElement(element)
		currentAstNode = previousAstNode
	}

}
