package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class InlineKlassDetector : FileAnalyzer() {

	override fun analyze(file: KtElement): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitClass(klass: KtClass) {
				super.visitClass(klass)
				if(klass.hasModifier(KtTokens.INLINE_KEYWORD)){
					findings.add(
						Feature(id = "inline_klass",
							entity = Entity.from(klass))
					)
				}

			}

		})

		return findings.toList()
	}
}
