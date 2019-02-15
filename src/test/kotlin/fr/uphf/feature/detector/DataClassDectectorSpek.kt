package fr.uphf.kastree.json.test

import fr.uphf.analyze.DetectionResult
import fr.uphf.feature.detector.DataClassDetector
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object DataClassDectectorSpek : Spek({

    Feature("Data class detector") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("A data class is declared") {

            Given("A data class "){
                code = """
data class Point(val x: Int, val y: Int)
""".trimIndent()
            }

            lateinit var findings: List<Finding>
            When("the file is analyzed") {
                file = compileTo(code)
                val dataClassDetector = DataClassDetector()
                findings = dataClassDetector.analyze(file)
            }

            Then("it should report only one feature whose id is data_class") {

                val result = DetectionResult.from(findings)

                assertThat(result["data_class"]).hasSize(1)

            }

        }

    }



})

