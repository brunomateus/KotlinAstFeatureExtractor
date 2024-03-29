package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class SmartCastDetector :  FileAnalyzer(){
	override fun analyze(file: KtElement): List<Finding> {
		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitIfExpression(expression: KtIfExpression) {
				super.visitIfExpression(expression)
				if (expression.condition is KtIsExpression) {
					findings.add(
						Feature(id = "smart_cast",
							entity = Entity.from(expression))
					)
				}
			}

			override fun visitWhenConditionIsPattern(condition: KtWhenConditionIsPattern) {
				super.visitWhenConditionIsPattern(condition)
				findings.add(
					Feature(id = "smart_cast",
						entity = Entity.from(condition))
				)
			}
		})

		return findings.toList()
	}

}
