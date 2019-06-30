package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class SingletonDetector :  FileAnalyzer(){
	override fun analyze(file: KtElement): List<Finding> {
		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
				super.visitObjectDeclaration(declaration)


				if (declaration.parent !is KtObjectLiteralExpression && !declaration.isCompanion()){
					findings.add(
						Feature(id = "singleton",
							entity = Entity.from(declaration))
					)
				}
			}

		})

		return findings.toList()
	}

}
