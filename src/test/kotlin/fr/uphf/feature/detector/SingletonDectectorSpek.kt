package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object CompanionAndSingletonDectectorSpek : Spek({

    Feature("Object declaration") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Object declaration uses") {

            Given("Singleton and Combanion objects declared"){
                code = """
class MyClass {
    companion object Factory {
        fun create(): MyClass = MyClass()
    }
	
	class MyClass2 {
		object Factory {
        	fun create(): MyClass = MyClass()
    }

    fun countClicks(window: JComponent) {
		var clickCount = 0
		var enterCount = 0

		window.addMouseListener(object : MouseAdapter() {
			override fun mouseClicked(e: MouseEvent) {
				clickCount++
			}

			override fun mouseEntered(e: MouseEvent) {
				enterCount++
			}
		})
	}
}

object DataProviderManager {
    fun registerDataProvider(provider: DataProvider) {
        print("nothing")
    }


}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val detector = SingletonDetector()
                val findings = detector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 2
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("report $nFinding uses of singletons"){
                assertThat(result["singleton" ]).hasSize(nFinding)
            }

        }

    }




})

