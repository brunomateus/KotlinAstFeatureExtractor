package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class InlineFunctionDetector : FileAnalyzer() {

	override fun analyze(file: KtElement): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitNamedFunction(function: KtNamedFunction) {
				super.visitNamedFunction(function)

				if(function.hasModifier(KtTokens.INLINE_KEYWORD)){
					findings.add(
						Feature(id = "inline_func",
							entity = Entity.from(function))
					)
				}
			}

		})

		return findings.toList()
	}
}
