package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtWhenExpression

class WhenExpressionDetector :  FileAnalyzer(){
	override fun analyze(file: KtFile): List<Finding> {
		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitWhenExpression(expression: KtWhenExpression) {
				super.visitWhenExpression(expression)
				findings.add(
					Feature(id = "when_expr",
						entity = Entity.from(expression))
				)
			}
		})

		return findings.toList()
	}

}
