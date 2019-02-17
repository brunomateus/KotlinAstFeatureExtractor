package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class LambdaDetector :  FileAnalyzer(){
	override fun analyze(file: KtFile): List<Finding> {
		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {


			override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
				super.visitLambdaExpression(lambdaExpression)
				findings.add(
					Feature(id = "lambda",
						entity = Entity.from(lambdaExpression))
				)
			}
		})

		return findings.toList()
	}

}
