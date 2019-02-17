package fr.uphf.kastree.json.test

import fr.uphf.analyze.DetectionResult
import fr.uphf.feature.detector.DestructuringDeclarationDetector
import fr.uphf.feature.detector.ExtensionFunctionAndOverloadedOpDetector
import fr.uphf.feature.detector.LambdaDetector
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object LambdaDectectorSpek : Spek({

    Feature("Lambda Expressions") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Lambda in different situations") {

            Given("Different ways of using lambda functions"){
                code = """
        fun containsEven(collection: Collection<Int>): Boolean = collection.any { x -> x % 2 == 0 }

			val calculateGrade = { grade : Int ->
				when(grade) {
					in 0..40 -> "Fail"
					in 41..70 -> "Pass"
					in 71..100 -> "Distinction"
					else -> false
				}
			}

			fun teste2() {
				val more : (String, Int) -> String = { str, int -> str + int }

				val that : Int -> Int = { three -> three }

				val noReturn : Int -> Unit = { num -> println(num) }


			}


			fun teste3() {
				val array = arrayOf(1, 2, 3, 4, 5, 6)
				array.forEach { println(it * 4) }
			}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = LambdaDetector()
                val findings = detector.analyze(file)
                result = DetectionResult.from(findings)

            }

            val nFinding = 6
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report $nFinding uses of lambdas"){
                assertThat(result["lambda" ]).hasSize(nFinding)
            }


        }

    }




})

