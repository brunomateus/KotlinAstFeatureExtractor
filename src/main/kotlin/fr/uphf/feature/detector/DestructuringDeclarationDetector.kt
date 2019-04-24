package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class DestructuringDeclarationDetector : FileAnalyzer() {

	override fun analyze(file: KtElement): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
				super.visitDestructuringDeclaration(destructuringDeclaration)
				findings.add(Feature(id = "destructuring_declaration",
					entity = Entity.from(destructuringDeclaration)))
			}

		})

		return findings.toList()
	}

}
