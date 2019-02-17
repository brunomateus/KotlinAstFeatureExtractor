package fr.uphf.feature.detector

import fr.uphf.analyze.DetectionResult
import fr.uphf.feature.detector.DestructuringDeclarationDetector
import fr.uphf.feature.detector.ExtensionFunctionAndOverloadedOpDetector
import fr.uphf.kastree.json.test.compileTo
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ExtensionFuncAndOverloadedOpDectectorSpek : Spek({

    Feature("Overloaded Operator") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Overloading operators") {

            Given("Two function whose overload operators"){
                code = """
        data class Point(val x: Int, val y: Int)

		operator fun Point.unaryMinus() = Point(-x, -y)

		data class Counter(val dayIndex: Int) {
			operator fun plus(increment: Int): Counter {
				return Counter(dayIndex + increment)
			}
		}

		fun teste(){
		}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = ExtensionFunctionAndOverloadedOpDetector()
                val findings = detector.analyze(file)
                result = DetectionResult.from(findings)

            }

            val nFinding = 2
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report two uses of operators overloaded"){
                assertThat(result["overloaded_op" ]).hasSize(2)
            }


        }

    }

    Feature("Extension functions") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Creating extension function") {

            Given("Three extension functions"){
                code = """
		fun MutableList<Int>.swap(index1: Int, index2: Int) {
			val tmp = this[index1] // 'this' corresponds to the list
			this[index1] = this[index2]
			this[index2] = tmp
		}

		fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
			val tmp = this[index1] // 'this' corresponds to the list
			this[index1] = this[index2]
			this[index2] = tmp
		}

		fun Any?.toString(): String {
			if (this == null) return "null"
			// after the null check, 'this' is autocast to a non-null type, so the toString() below
			// resolves to the member function of the Any class
			return toString()
		}

		fun teste(){
		}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = ExtensionFunctionAndOverloadedOpDetector()
                val findings = detector.analyze(file)
                result = DetectionResult.from(findings)

            }

            val nFinding = 3
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("two uses of extension function"){
                assertThat(result["extension_function" ]).hasSize(3)
            }


        }

    }



})

