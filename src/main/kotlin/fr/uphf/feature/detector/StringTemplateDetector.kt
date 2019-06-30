package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class StringTemplateDetector : FileAnalyzer() {

	override fun analyze(file: KtElement): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
				super.visitStringTemplateExpression(expression)

				if(expression.entries.any { it is KtStringTemplateEntryWithExpression }){
					findings.add(
						Feature(id = "string_template",
							entity = Entity.from(expression))
					)
				}
			}

		})

		return findings.toList()
	}
}
