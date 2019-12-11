package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getASTasStringJson
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object SmartCastDectectorSpek : Spek({

    Feature("Smart cast") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Smart cast uses") {

            Given("Different ways of using smart cast"){
                code = """
            fun demo(x: Any) {
				if (x is String) {
					print(x.length) // x is automatically cast to String
				}
			}

			fun demo2(obj: Any) {
				if (obj is String) {
					print(obj.length)
				}

				if (obj !is String) {
					print("Not a String")
				}
				else {
					print(obj.length)
				}

                val z = obj is String
			}

			fun smartWhen(x: Any) {
				when (x) {
					is Int -> print(x + 1)
					is String -> print(x.length + 1)
					is IntArray -> print(x.sum())
				}
			}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = SmartCastDetector()
                val findings = detector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 6
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report $nFinding uses of smart cast"){
                assertThat(result["smart_cast" ]).hasSize(nFinding)
            }


        }

    }




})

