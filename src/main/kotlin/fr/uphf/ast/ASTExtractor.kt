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
		var skipChildren = false

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

			is KtAnnotationEntry -> ""
			is KtConstructorCalleeExpression -> {
				skipChildren = true
				element.text
			}
			is KtAnnotationUseSiteTarget -> element.text
			is KtAnnotation -> ""
			is KtParameter -> if (element.valOrVarKeyword != null) "${element.valOrVarKeyword?.text} ${element.name}" else element.name
			is KtLambdaArgument -> ""

			//KtDeclaration
			is KtEnumEntry -> element.name
			is KtClass -> {
				if(element.isInterface()){
					val modifiers = listOf(ASTNode(type="ModifierEntry", label="interface"))
					val modifierList = ASTNode(type="KtDeclarationModifierList", label="", children=modifiers.toMutableList())
					granChildren.add(modifierList)
				}
				element.name
			}
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
			is KtClassLiteralExpression -> ""
			is KtParenthesizedExpression -> element.text
			is KtStringTemplateExpression -> ""
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
			is KtCallExpression -> ""
			is KtArrayAccessExpression -> ""
			// KtExpression
			is KtImportDirective -> {
				skipChildren = true
				element.importedReference?.text + if(element.isAllUnder) ".*" else ""
			}
			is KtPrimaryConstructor -> ""
			is KtPropertyAccessor -> if (element.isGetter) "get" else if (element.isSetter) "set" else ""
			is KtPackageDirective -> {
				skipChildren = true
				element.packageNameExpression?.text
			}
			is KtTypeProjection -> ""
			is KtTypeReference -> ""
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
			is KtLiteralStringTemplateEntry -> element.text
			is KtStringTemplateEntry -> ""
			is KtBlockStringTemplateEntry -> ""
			is KtSuperExpression -> "super"
			is KtCatchClause -> "catch"
			is KtNullableType -> ""
			is KtUserType -> ""
			is KtTypeConstraint -> ""
			is KtTypeParameter -> ""
			is KtTypeArgumentList -> ""
			//is KtValueArgument -> if (element.getArgumentExpression() is KtLambdaExpression) "" else element.getArgumentExpression()?.text//named ?
			is KtValueArgument -> ""
			is KtValueArgumentName -> element.asName.asString() //TODO Do not visit its child
			is KtValueArgumentList -> ""
			is KtWhenConditionWithExpression -> element.text
			is KtWhenConditionInRange -> element.text
			is KtWhenConditionIsPattern -> element.text
			is KtWhenEntry -> if (element.isElse) "else" else ""

			is KtOperationExpression -> element.text
			is KtNameReferenceExpression -> element.text

			else -> ""
		}

		val childNode = ASTNode(type=element.javaClass.simpleName, label=label ?: "", children = granChildren)
		currentAstNode.addChild(childNode)
		val previousAstNode = currentAstNode
		currentAstNode = childNode
		if(!skipChildren) super.visitKtElement(element)
		currentAstNode = previousAstNode
	}

}
