package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/**
 * @author Bruno Gois Mateus
 */
class ExtensionFunctionAndOverloadedOpDetector : FileAnalyzer() {

	override fun analyze(file: KtFile): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitNamedFunction(function: KtNamedFunction) {
				if(function.hasModifier(KtTokens.OPERATOR_KEYWORD)){
					findings.add(
						Feature(id = "overloaded_op",
							entity = Entity.from(function))
					)
				} else if(function.receiverTypeReference != null){
					findings.add(
						Feature(id = "extension_function",
							entity = Entity.from(function))
					)

				}
			}

		})

		return findings.toList()
	}
}