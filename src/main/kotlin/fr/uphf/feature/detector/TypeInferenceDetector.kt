package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class TypeInferenceDetector :  FileAnalyzer(){
	override fun analyze(file: KtElement): List<Finding> {
		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {


			override fun visitProperty(property: KtProperty) {
				super.visitProperty(property)

				if(property.typeReference == null && property.hasInitializer()) {
					findings.add(
						Feature(id = "inference",
							entity = Entity.from(property))
					)
				}
			}

			override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
				super.visitDestructuringDeclaration(destructuringDeclaration)

				destructuringDeclaration.entries.forEach {
					if(it.typeReference == null){
						findings.add(
							Feature(id = "inference",
								entity = Entity.from(it))
						)
					}
				}
			}
		})

		return findings.toList()
	}

}
