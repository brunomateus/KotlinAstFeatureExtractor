package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object DelegationDectectorSpek : Spek({

    Feature("Delegation") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("The two possibility of delegation is used") {

            Given("Two classes that uses property delegation and other class that" +
                    "uses super delegation "){
                code = """
class User {
			var name: String by Delegates.observable("<no name>") {
				prop, old, new ->
				println("")
			}
		}
		interface Base {
			fun print()
		}

		class BaseImpl(val x: Int) : Base {
			override fun print() { print(x) }
		}

		class Derived(b: Base) : Base by b

		fun main(args: Array<String>) {
			val b = BaseImpl(10)
			Derived(b).print()
		}

		fun example(computeFoo: () -> Foo) {
			val memoizedFoo by lazy(computeFoo)

			if (someCondition && memoizedFoo.isValid()) {
				memoizedFoo.doSomething()
			}
		}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val delegationDetector = DelegationDetector()
                val findings = delegationDetector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 3
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("one of use of super delegation"){
                assertThat(result["super_delegation" ]).hasSize(1)
            }

            And("two of uses of super delegation"){
                assertThat(result["property_delegation"]).hasSize(2)
            }

        }

    }



})

