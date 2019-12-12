package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object InlineKlassDectectorSpek : Spek({

    Feature("Inline klass") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Inline Klass uses") {

            Given("Different contract are used"){
                code = """
inline class Name(val s: String)

inline fun <reified T> TreeNode.findParentOfType(): T? {
    var p = parent
    while (p != null && p !is T) {
        p = p.parent
    }
    return p as T?
}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = InlineKlassDetector()
                val findings = detector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 1
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report 1 uses of inline klass"){
                assertThat(result["inline_klass"]).hasSize(1)
            }

        }

    }




})

