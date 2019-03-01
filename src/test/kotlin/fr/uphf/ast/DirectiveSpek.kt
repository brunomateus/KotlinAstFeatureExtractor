package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import fr.uphf.analyze.getASTasStringJson
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object DirectiveSpek : Spek({
    Feature("Type Alias") {

        lateinit var rootNode: ASTNode
        lateinit var code: String

        Scenario("Type alias") {

            Given("New names for existing types"){
                code = """
 typealias NodeSet = Set<Network.Node>
 typealias MyHandler = (Int, String, Any) -> Unit
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("It should contain a typealias NodeSet to Set<Network.Node>"){
                val alias1 = rootNode.getChild(2)

                assertThat(alias1.type).isEqualTo("KtTypeAlias")
                assertThat(alias1.label).isEqualTo("NodeSet")

                val typeReference1 = alias1.getFirstChild()

                assertThat(typeReference1.type).isEqualTo("KtTypeReference")
                assertThat(typeReference1.label).isEqualTo("Set<Network.Node>")
            }

            And("It should contain a typealias MyHandler to (Int, String, Any) -> Unit"){
                val alias2 = rootNode.getChild(3)

                assertThat(alias2.type).isEqualTo("KtTypeAlias")
                assertThat(alias2.label).isEqualTo("MyHandler")

                val typeReference2 = alias2.getFirstChild()

                assertThat(typeReference2.type).isEqualTo("KtTypeReference")
                assertThat(typeReference2.label).isEqualTo("(Int, String, Any) -> Unit")
            }

        }


    }

    Feature("Package declaration and import") {

        lateinit var rootNode: ASTNode
        lateinit var code: String

        Scenario("Package declaration and imports") {

            Given("A class with package declaration and some imports"){
                code = """
package fr.uphf

import com.beust.klaxon.JsonBase
import com.beust.klaxon.Klaxon
import com.beust.klaxon.*
import kotlin.test.assertNotNull

typealias NodeSet = Set<Network.Node>
typealias MyHandler = (Int, String, Any) -> Unit
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                println(getASTasStringJson(rootNode))
            }

            Then("It should contain a package definiton for the package fr.uphf"){
                assertThat(rootNode.getFirstChild().type).isEqualTo("KtPackageDirective")
                assertThat(rootNode.getFirstChild().label).isEqualTo("fr.uphf")
            }

            val imports = listOf("com.beust.klaxon.JsonBase",
                "com.beust.klaxon.Klaxon",
                "com.beust.klaxon.*",
                "kotlin.test.assertNotNull")
            And("It should contain a list imports $imports"){
                val importList = rootNode.getChild(1)
                assertThat(importList.type).isEqualTo("KtImportList")
                assertThat(importList.label).isEqualTo("")

                for(i in 0..3){
                    assertThat(importList.getChild(i).type).isEqualTo("KtImportDirective")
                    assertThat(importList.getChild(i).label).isEqualTo(imports[i])
                }
            }

        }


    }

})

