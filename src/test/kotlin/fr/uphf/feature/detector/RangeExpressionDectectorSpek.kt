package fr.uphf.feature.detector

import fr.uphf.analyze.DetectionResult
import fr.uphf.analyze.compileTo
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object RangeExpressionDectectorSpek : Spek({

    Feature("Range Expressions") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Range Expressions") {

            Given("Different ways of using range expressions"){
                code = """
        fun main() {

                val i = 3

				if (i in 1..10) { // equivalent of 1 <= i && i <= 10
					println(i)
				}

				for (i in 1..4) print(i)

				for (i in 4..1) print(i)

				val numbers: MutableList<Int> = mutableListOf(1, 2, 3)
				for (i in numbers) print(i)

				for (i in 4 downTo 1) print(i)

				for (i in 1..4 step 2) print(i)

				for (i in 1 until 10) {
					// i in [1, 10), 10 is excluded
					println(i)
				}

				(2..4).forEach{
					println(it)
				}

				fun bestSizeMatch(target: Int) = when (target) {
					in 0..256 -> urlThumb
					in 256..800 -> urlSmall
					in 800..2048 -> urlRegular
					else -> urlRaw
				}

			}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = RangeExpressionDetector()
                val findings = detector.analyze(file)
                result = DetectionResult.from(findings)

            }

            val nFinding = 10
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report $nFinding uses of smart cast"){
                assertThat(result["range_expr" ]).hasSize(nFinding)

            }


        }

    }




})

