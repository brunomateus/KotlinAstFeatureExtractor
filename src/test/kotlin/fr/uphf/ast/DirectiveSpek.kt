package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import fr.uphf.analyze.getASTasStringJson
import fr.uphf.analyze.printAST
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
                printAST(rootNode)
            }

            Then("It should contain a typealias NodeSet to Set<Network.Node>"){
                val alias1 = rootNode.getChild(2)

                assertThat(alias1.type).isEqualTo("KtTypeAlias")
                assertThat(alias1.label).isEqualTo("NodeSet")

                val typeReference1 = alias1.getFirstChild()

                assertThat(typeReference1.type).isEqualTo("KtTypeReference")
                assertThat(typeReference1.label).isEqualTo("")

                assertThat(typeReference1.getFirstChild().getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(typeReference1.getFirstChild().getFirstChild().label).isEqualTo("Set")

                val argumentList = typeReference1.getFirstChild().getChild(1)
                assertThat(argumentList.type).isEqualTo("KtTypeArgumentList")
                assertThat(argumentList.label).isEqualTo("")

                val typeProjection = argumentList.getFirstChild()
                assertThat(typeProjection.type).isEqualTo("KtTypeProjection")
                assertThat(typeProjection.label).isEqualTo("")

                assertThat(typeProjection.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(typeProjection.getFirstChild().label).isEqualTo("")

                val userType = typeProjection.getFirstChild().getFirstChild()
                assertThat(userType.type).isEqualTo("KtUserType")
                assertThat(userType.label).isEqualTo("")

                assertThat(userType.getFirstChild().type).isEqualTo("KtUserType")
                assertThat(userType.getFirstChild().label).isEqualTo("")

                assertThat(userType.getFirstChild().getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(userType.getFirstChild().getFirstChild().label).isEqualTo("Network")

                assertThat(userType.getChild(1).type).isEqualTo("KtNameReferenceExpression")
                assertThat(userType.getChild(1).label).isEqualTo("Node")
            }

            And("It should contain a typealias MyHandler to (Int, String, Any) -> Unit"){
                val alias2 = rootNode.getChild(3)

                assertThat(alias2.type).isEqualTo("KtTypeAlias")
                assertThat(alias2.label).isEqualTo("MyHandler")

                val typeReference2 = alias2.getFirstChild()

                assertThat(typeReference2.type).isEqualTo("KtTypeReference")
                assertThat(typeReference2.label).isEqualTo("")

                val funcType = typeReference2.getFirstChild()

                assertThat(funcType.type).isEqualTo("KtFunctionType")
                assertThat(funcType.label).isEqualTo("")
                assertThat(funcType.children).hasSize(2)

                val paramList = funcType.getFirstChild()
                assertThat(paramList.type).isEqualTo("KtParameterList")
                assertThat(paramList.label).isEqualTo("")
                assertThat(paramList.children).hasSize(3)

                val types = listOf("Int", "String", "Any")
                for(i in (0..2)){
                    val param = paramList.getChild(i)
                    assertThat(param.type).isEqualTo("KtParameter")
                    assertThat(param.label).isEqualTo("")

                    val typeRef = param.getFirstChild()
                    assertThat(typeRef.type).isEqualTo("KtTypeReference")
                    assertThat(typeRef.label).isEqualTo("")

                    val userType = typeRef.getFirstChild()
                    assertThat(userType.type).isEqualTo("KtUserType")
                    assertThat(userType.label).isEqualTo("")

                    val nameExpr = userType.getFirstChild()
                    assertThat(nameExpr.type).isEqualTo("KtNameReferenceExpression")
                    assertThat(nameExpr.label).isEqualTo(types[i])
                }

                val returnType = funcType.getChild(1)
                assertThat(returnType.type).isEqualTo("KtTypeReference")
                assertThat(returnType.label).isEqualTo("")

                val userType = returnType.getFirstChild()
                assertThat(userType.type).isEqualTo("KtUserType")
                assertThat(userType.label).isEqualTo("")

                val nameExpr = userType.getFirstChild()
                assertThat(nameExpr.type).isEqualTo("KtNameReferenceExpression")
                assertThat(nameExpr.label).isEqualTo("Unit")
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

