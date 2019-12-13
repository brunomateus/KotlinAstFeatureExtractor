package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getASTasStringJson
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object CoroutineDectectorSpek : Spek({

    Feature("Coroutine") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Coroutine uses") {

            Given("Different coroutines are used"){
                code = """
import kotlinx.coroutines.*

fun launch(whatever: String){
	print(whatever)
}

fun main() = runBlocking { // this: CoroutineScope
    launch { 
        delay(200L)
        println("Task from runBlocking")
    }
    
    coroutineScope { // Creates a coroutine scope
        launch {
            delay(500L) 
            println("Task from nested launch")
        }
    
        delay(100L)
        println("Task from coroutine scope") // This line will be printed before the nested launch
    }
    
    println("Coroutine scope is over") // This line is not printed until the nested launch completes
    
    launch("hello world")
    
}


""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = CoroutineDetector()
                val findings = detector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 2
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report 2 uses of coroutine"){
                assertThat(result["coroutine"]).hasSize(2)
            }


        }

    }




})

