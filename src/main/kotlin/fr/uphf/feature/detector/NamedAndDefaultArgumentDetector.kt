package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class NamedAndDefaultArgumentDetector : FileAnalyzer() {
	override fun analyze(file: KtElement): List<Finding> {
		var findings = emptyList<Finding>().toMutableList()

		file.accept(object : KtTreeVisitorVoid() {

			override fun visitParameterList(ktlist: KtParameterList) {
				super.visitParameterList(ktlist)
				if (ktlist.parameters.any { it.hasDefaultValue() }) {
					findings.add(Feature(id = "func_with_default_value",
							entity = Entity.from(ktlist.parent))
					)
				}
			}

			override fun visitValueArgumentList(list: KtValueArgumentList) {
				super.visitValueArgumentList(list)

				if(list.parent !is KtAnnotationEntry) {

					if(list.arguments.any{it.isNamed()}){
						findings.add(Feature(id = "func_call_with_named_arg",
							entity = Entity.from(list.parent))
						)
					}

				}

			}



		})

		return findings.toList()
	}

}
