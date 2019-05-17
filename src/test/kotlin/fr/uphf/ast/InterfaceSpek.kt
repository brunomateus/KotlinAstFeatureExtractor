package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import fr.uphf.analyze.printAST
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object InterfaceSpek : Spek({
    Feature("Kotlin interfaces") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ktclass: ASTNode

        Scenario("Interface with methods only") {

            Given("A interface with two methods and only one implemented"){
                code = """
interface MyInterface {
    fun bar()
    fun foo() {
      // optional body
    }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }

            val clsName = "MyInterface"
            Then("it should contain one interface named $clsName with the modifier interface") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)

                val modifierList = ktclass.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("interface")
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            lateinit var classBody: ASTNode

            Then("The first method, named bar, does not have implementation(body)"){
                classBody = ktclass.getChild(1)

                assertThat(classBody.getFirstChild().type).isEqualTo("KtNamedFunction")
                assertThat(classBody.getFirstChild().label).isEqualTo("bar")

                assertThat(classBody.getFirstChild().children).hasSize(1)
            }

            Then("The second method, named foo, have implementation(body)") {
                assertThat(classBody.getChild(1).type).isEqualTo("KtNamedFunction")
                assertThat(classBody.getChild(1).label).isEqualTo("foo")

                assertThat(classBody.getChild(1).children).hasSize(2)
            }
        }

        Scenario("Interface with properties defined ") {

            Given("A interface with two properties and one method defined"){
                code = """
interface MyInterface {
    val prop: Int // abstract

    val propertyWithImplementation: String
        get() = "foo"

    fun foo() {
        print(prop)
    }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val clsName = "MyInterface"
            Then("it should contain one class named $clsName") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            lateinit var classBody: ASTNode

            Then("The first property, named prop is abstract"){
                classBody = ktclass.getChild(1)

                assertThat(classBody.getFirstChild().type).isEqualTo("KtProperty")
                assertThat(classBody.getFirstChild().label).isEqualTo("prop")

                val propertyKeyword = classBody.getFirstChild().getFirstChild()
                assertThat(propertyKeyword.type).isEqualTo("KtPropertyKeyword")
                assertThat(propertyKeyword.label).isEqualTo("val")

                assertThat(classBody.getFirstChild().children).hasSize(2)
            }

            And("The second property, named propertyWithImplementation, has a get accessor defined") {
                val property = classBody.getChild(1)
                assertThat(property.type).isEqualTo("KtProperty")
                assertThat(property.label).isEqualTo("propertyWithImplementation")

                val propertyKeyword = property.getFirstChild()
                assertThat(propertyKeyword.type).isEqualTo("KtPropertyKeyword")
                assertThat(propertyKeyword.label).isEqualTo("val")


                assertThat(property.children).hasSize(3)

                val propertyAccessor = property.getChild(2)
                assertThat(propertyAccessor.type).isEqualTo("KtPropertyAccessor")
                assertThat(propertyAccessor.label).isEqualTo("get")
            }

            And("The method named foo is defined"){
                val namedFunc = classBody.getChild(2)
                assertThat(namedFunc.type).isEqualTo("KtNamedFunction")
                assertThat(namedFunc.label).isEqualTo("foo")

                assertThat(namedFunc.children).hasSize(2)
            }
        }


    }




})

