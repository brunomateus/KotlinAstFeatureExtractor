package fr.uphf.feature.detector

import fr.uphf.analyze.FileAnalyzer
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

/**
 * @author Bruno Gois Mateus
 */
class CoroutineDetector : FileAnalyzer() {

	override fun analyze(file: KtElement): List<Finding> {

		var import = false
		var keyword = false

		file.accept(object : KtTreeVisitorVoid() {

			private val coroutinesKeywords = setOf<String>("launch", "async", "produce", "runBlocking",
				"GlobalScope", "CoroutineScope", "coroutineContext", "ensureActive", "cancel",
				"yield", "delay", "withContext", "withTimeout", "withTimeoutOrNull", "awaitAll", "joinAll",
				"receive", "receiveOrNull", "lock", "send")

			override fun visitImportDirective(importDirective: KtImportDirective) {
				super.visitImportDirective(importDirective)
				if (importDirective.text.contains("kotlinx.coroutines")){
					import = true
				}
			}

			override fun visitCallExpression(expression: KtCallExpression) {
				super.visitCallExpression(expression)
				if (coroutinesKeywords.contains(expression.calleeExpression?.text)){
					keyword = true
				}

			}
		})

		if (import and keyword) {
			return listOf(Feature(id = "coroutine", entity = Entity.from(file)))
		}
		return emptyList()
	}
}
