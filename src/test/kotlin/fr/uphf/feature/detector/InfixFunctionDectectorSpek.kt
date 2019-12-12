package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object InfixFunctionDectectorSpek : Spek({

    Feature("Infix function") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Infix Klass uses") {

            Given("One infix function"){
                code = """
infix fun Int.add(b : Int) : Int = this + b

fun main(){

	 val y = 10 add 20        
}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = InfixFunctionDetector()
                val findings = detector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 1
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report 1 uses of infix function"){
                assertThat(result["infix_func"]).hasSize(1)
            }

        }

    }




})

