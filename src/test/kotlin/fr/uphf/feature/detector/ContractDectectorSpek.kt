package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ContractDectectorSpek : Spek({

    Feature("Contract") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Contract uses") {

            Given("Different contract are used"){
                code = """

import kotlin.contracts.contract

fun String?.isNullOrEmpty(): Boolean {
    contract {
        returns(false) implies (this@isNullOrEmpty != null)
    }
    return this == null || isEmpty()
}

fun require(condition: Boolean) {
    // This is a syntax form, which tells compiler:
    // "if this function returns successfully, then passed 'condition' is true"
    contract { returns() implies condition }
    if (!condition) throw IllegalArgumentException(...)
}

fun foo(s: String?) {
    require(s is String)
    // s is smartcasted to 'String' here, because otherwise
    // 'require' would have throw an exception
}

fun contract(){
	println("hello world")
}

fun main(){
    contract()
}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = ContractDetector(  )
                val findings = detector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 2
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report 2 uses of contract"){
                assertThat(result["contract"]).hasSize(2)
            }

        }

    }




})

