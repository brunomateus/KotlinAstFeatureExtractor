package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class ContractDetector : FileAnalyzer() {

	override fun analyze(file: KtElement): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitReferenceExpression(expression: KtReferenceExpression) {
				super.visitReferenceExpression(expression)
				if (expression.text == "contract"){
					findings.add(
						Feature(id = "contract",
							entity = Entity.from(expression))
					)
				}
			}

		})

		return findings.toList()
	}
}
