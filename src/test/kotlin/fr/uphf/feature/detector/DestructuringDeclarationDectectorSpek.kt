package fr.uphf.feature.detector

import fr.uphf.analyze.compileTo
import fr.uphf.analyze.getResult
import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object DestructuringDeclarationDectectorSpek : Spek({

    Feature("Destructuring Declaration") {

        lateinit var file: KtFile
        lateinit var code: String

        Scenario("Using destructuring declaration") {

            Given("Two variable whose rhs of assignment is a destructuring declaration"){
                code = """
class Point(private val x: Int = 0, private val y: Int = 10) {

			operator fun plus(p: Point) : Point {
				return Point(x + p.x, y + p.y)
			}

			operator fun component1(){
				x
			}

			operator fun component2(){
				y
			}
		}

		data class Point3D(val x: Int, val y: Int, val z: Int)

		fun main() {
			val (x, y) = Point()
			val (a, b, c) = Point3D(1, 1, 1)

            val map = mutableMapOf<Int,String>() 
			map.put(1,"Ishita") 
			map.put(2,"Kamal") 
			map.put(3,"Kanika")
			// Destructuring a map entry into key and values 
			val newmap = map.mapValues { (key,value) -> "Hello" }
		}
""".trimIndent()
            }

            lateinit var result: Map<String, List<Finding>>
            When("the file is analyzed") {
                file = compileTo(code)
                val destructuringDeclarationDetector = DestructuringDeclarationDetector()
                val findings = destructuringDeclarationDetector.analyze(file)
                result = getResult(findings)

            }

            val nFinding = 3
            Then("it should report $nFinding findings") {
                assertThat(result.map { it.value.size }.sum()).isEqualTo(nFinding)
            }

            And("two uses of super delegation"){
                assertThat(result["destructuring_declaration" ]).hasSize(nFinding)
            }


        }

    }



})

