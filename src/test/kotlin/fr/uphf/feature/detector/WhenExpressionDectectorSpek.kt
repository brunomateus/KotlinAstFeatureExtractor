package fr.uphf.feature.detector

import fr.uphf.analyze.DetectionResult
import fr.uphf.analyze.compileTo
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object WhenExpressionDectectorSpek : Spek({

    Feature("When Expressions") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("When in different situations") {

            Given("Three function using when expressions"){
                code = """
        fun test(x: Int) {
			when (x) {
				1 -> print("x == 1")
				2 -> print("x == 2")
				else -> { // Note the block
					print("x is neither 1 nor 2")
			}
		}

		fun test2(x: Int) {
			when (x) {
				0, 1 -> print("x == 0 or x == 1")
				else -> print("otherwise")
			}
		}

		fun test3(x: Int) {
			when (x) {
				in 1..10 -> print("x is in the range")
				!in 10..20 -> print("x is outside the range")
				else -> print("none of the above")
			}
		}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = WhenExpressionDetector()
                val findings = detector.analyze(file)
                result = DetectionResult.from(findings)

            }

            val nFinding = 3
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report $nFinding uses of when expressions"){
                assertThat(result["when_expr" ]).hasSize(nFinding)
            }


        }

    }




})

