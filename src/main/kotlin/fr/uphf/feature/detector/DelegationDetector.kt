package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class DelegationDetector : FileAnalyzer() {

	override fun analyze(file: KtFile): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitDelegatedSuperTypeEntry(specifier: KtDelegatedSuperTypeEntry) {
				super.visitDelegatedSuperTypeEntry(specifier)
				findings.add(Feature(id = "super_delegation", entity = Entity.from(specifier)))
			}

			override fun visitPropertyDelegate(delegate: KtPropertyDelegate) {
				super.visitPropertyDelegate(delegate)
				findings.add(Feature(id = "property_delegation", entity = Entity.from(delegate)))
			}

		})

		return findings.toList()
	}

}
