package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class TypeAliasDetector : FileAnalyzer() {

	override fun analyze(file: KtElement): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitTypeAlias(typeAlias: KtTypeAlias) {
				super.visitTypeAlias(typeAlias)
				findings.add(
					Feature(id = "type_alias",
						entity = Entity.from(typeAlias))
				)
			}

		})

		return findings.toList()
	}
}
