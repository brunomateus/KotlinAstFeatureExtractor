package fr.uphf.feature.detector

import fr.uphf.analyze.DetectionResult
import fr.uphf.feature.detector.DestructuringDeclarationDetector
import fr.uphf.feature.detector.ExtensionFunctionAndOverloadedOpDetector
import fr.uphf.feature.detector.LambdaDetector
import fr.uphf.feature.detector.NamedAndDefaultArgumentDetector
import fr.uphf.kastree.json.test.compileTo
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object NamedAndDefaultArgumentDetectorSpek : Spek({

    Feature("Parameter with Default Value and Named args") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Default value and named args") {

            Given("Different ways of default value and one call with named args"){
                code = """
        data class Person(firstName: String = "UNKNOWN", age: Int = 0)

		fun displayGreeting(message: String, name: String = "Guest") {
    		println("Hello")
		}


		fun main(){
			fun arithmeticSeriesSum(a: Int = 1, n: Int, d: Int = 1): Int {
				return n/2 * (2*a + (n-1)*d)
			}

			arithmeticSeriesSum(a=3, n=10)
		}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = NamedAndDefaultArgumentDetector()
                val findings = detector.analyze(file)
                result = DetectionResult.from(findings)

            }

            val nFinding = 11
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            Then("it should report 3 function that has parameter with default value"){
                assertThat(result["func_with_default_value" ]).hasSize(3)
            }

            And("it should report 5 parameters with default value"){
                assertThat(result["parameter_with_default_value" ]).hasSize(5)
            }

            Then("it should report 1 function call that has named args"){
                assertThat(result["func_call_with_named_arg" ]).hasSize(1)
            }

            And("it should 2 named args"){
                assertThat(result["named_arg" ]).hasSize(2)
            }




        }

    }




})

