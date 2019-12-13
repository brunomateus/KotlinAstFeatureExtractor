package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class ContractDetector : FileAnalyzer() {

	override fun analyze(file: KtElement): List<Finding> {


        var import = false
        val findings = mutableListOf<Finding>()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitCallExpression(expression: KtCallExpression) {
				super.visitCallExpression(expression)
				if ((expression.calleeExpression?.text == "contract") and expression.lambdaArguments.isNotEmpty()){
                    findings.add(Feature(id = "contract", entity = Entity.from(expression)))
				}
			}

            override fun visitImportDirective(importDirective: KtImportDirective) {
                super.visitImportDirective(importDirective)
                if (importDirective.text.contains("kotlin.contracts")){
                    import = true
                }
            }

		})

        if (import and findings.isNotEmpty()) {
            return findings.toList()
        }

		return emptyList()
	}
}
