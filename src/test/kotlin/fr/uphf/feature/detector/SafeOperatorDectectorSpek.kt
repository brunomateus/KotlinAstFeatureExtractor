package fr.uphf.feature.detector

import fr.uphf.analyze.DetectionResult
import fr.uphf.analyze.compileTo
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object SafeOperatorDectectorSpek : Spek({

    Feature("Safe and unsafe operator") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Safe and unsafe operator") {

            Given("Different ways of using safe and unsafe operator"){
                code = """
        fun main() {

			var b: String? = "abc"
			b = null // ok
			print(b)

			val listWithNulls: List<String?> = listOf("Kotlin", null)
			for (item in listWithNulls) {
				item?.let { println(it) } // prints A and ignores null
			}

			val l = b?.length ?: -1

			val k = b!!.length

            val z = b!!
		}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = SafeOperartorDetector()
                val findings = detector.analyze(file)
                result = DetectionResult.from(findings)

            }

            val nFinding = 4
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report 2 uses of safe call"){
                assertThat(result["safe_call" ]).hasSize(2)
            }

            And("report 1 uses of unsafe call"){
                assertThat(result["unsafe_call" ]).hasSize(2)
            }


        }

    }




})

