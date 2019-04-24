package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class RangeExpressionDetector :  FileAnalyzer(){
	override fun analyze(file: KtElement): List<Finding> {
		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitBinaryExpression(expression: KtBinaryExpression) {
				super.visitBinaryExpression(expression)
				when(expression.operationReference.text){
					"..", "downTo", "until" ->
						findings.add(
							Feature(id = "range_expr",
								entity = Entity.from(expression))
						);
				}
			}
		})

		return findings.toList()
	}

}
