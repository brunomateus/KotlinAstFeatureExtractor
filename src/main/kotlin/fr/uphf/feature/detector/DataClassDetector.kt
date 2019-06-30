package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class DataClassDetector : FileAnalyzer() {

	val featureId = "data_class"

	override fun analyze(file: KtElement): List<Finding> {

		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitClass(klass: KtClass) {
				super.visitClass(klass)
				if (klass.isData()) {
					findings.add(Feature(id = featureId, entity = Entity.from(klass)))
				}
			}
		})

		return findings.toList()
	}

}
