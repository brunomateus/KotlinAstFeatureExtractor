package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object TailrecFunctionDectectorSpek : Spek({

    Feature("Tailrec function") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Tailrec function uses") {

            Given("One tailrec function declared"){
                code = """

    val eps = 1E-10 // "good enough", could be 10^-15
	
	tailrec fun findFixPoint(x: Double = 1.0): Double = if (Math.abs(x - Math.cos(x)) < eps) x else findFixPoint(Math.cos(x))
			
    fun findFixPointNonRecursive(): Double {
        var x = 1.0
        while (true) {
            val y = Math.cos(x)
            if (Math.abs(x - y) < eps) return x
            x = Math.cos(x)
        }
    }

""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = TailrecFunctionDetector()
                val findings = detector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 1
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report 1 uses of tailrec function"){
                assertThat(result["tailrec_func"]).hasSize(1)
            }

        }

    }

})

