package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

class SafeOperartorDetector :  FileAnalyzer(){
	override fun analyze(file: KtElement): List<Finding> {
		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
				super.visitSafeQualifiedExpression(expression)
				findings.add(
					Feature(id = "safe_call",
						entity = Entity.from(expression))
				)
			}

			override fun visitPostfixExpression(expression: KtPostfixExpression) {
				super.visitPostfixExpression(expression)
				if(expression.operationToken == KtTokens.EXCLEXCL) {
					findings.add(
						Feature(id = "unsafe_call",
							entity = Entity.from(expression))
					)
				}
			}

		})

		return findings.toList()
	}

}
