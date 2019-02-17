package fr.uphf.ast

import fr.uphf.kastree.json.test.getASTasJson
import fr.uphf.kastree.json.test.printAST
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ModifiersSpek : Spek({
    Feature("Kotlin modifiers") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ktclass1: ASTNode
        lateinit var ktclass2: ASTNode

        Scenario("Different modifiers combined") {

            Given("A class and interface with multiple modifiers combined"){

                code = """
sealed class Base
interface Contract {
	fun bar()
}
open class Outer : Base(), Contract{
    private val a = 1
    protected open val b = 2
    internal val c = 3
    var d = 4  // public by default

    protected inner class Nested {
        public val e: Int = 5
    }

    override fun bar() {
        // body
    }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }

            val clsName = "Base"
            Then("it should contain one interface named $clsName with the modifier open") {
                ktclass1 = rootNode.getChild(2)
                assertThat(ktclass1.type).isEqualTo("KtClass")
                assertThat(ktclass1.label).isEqualTo(clsName)

                val modifierList = ktclass1.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("sealed")
            }

            val cls2Name = "Outer"
            Then("it should contain one interface named $cls2Name with the modifier open") {
                ktclass2 = rootNode.getChild(4)
                assertThat(ktclass2.type).isEqualTo("KtClass")
                assertThat(ktclass2.label).isEqualTo(cls2Name)

                val modifierList = ktclass2.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("open")
            }

            val nChildren = 3
            And("it should have $nChildren children") {
                assertThat(ktclass2.children).hasSize(nChildren)
            }

            lateinit var classBody: ASTNode

            Then("The first property, it is private read-only and it is named a"){
                classBody = ktclass2.getChild(2)

                val prop = classBody.getFirstChild()
                assertThat(prop.type).isEqualTo("KtProperty")
                assertThat(prop.label).isEqualTo("val a")

                val modifierList = prop.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("private")
            }

            And("The second property, it is open protected read-only and it is named b"){
                val prop = classBody.getChild(1)
                assertThat(prop.type).isEqualTo("KtProperty")
                assertThat(prop.label).isEqualTo("val b")

                val modifierList = prop.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("protected")

                assertThat(modifierList.getChild(1).type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getChild(1).label).isEqualTo("open")
            }

            And("The third property, it is  internal read-only and it is named c"){
                val prop = classBody.getChild(2)
                assertThat(prop.type).isEqualTo("KtProperty")
                assertThat(prop.label).isEqualTo("val c")

                val modifierList = prop.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("internal")
            }

            And("The fourth property, it is  public(default) and it is named d"){
                val fourthProp = classBody.getChild(3)
                assertThat(fourthProp.type).isEqualTo("KtProperty")
                assertThat(fourthProp.label).isEqualTo("var d")
            }

            And("There is a protected inner class, named Nested, defined"){
                val nested = classBody.getChild(4)
                assertThat(nested.type).isEqualTo("KtClass")
                assertThat(nested.label).isEqualTo("Nested")

                val nestedModifierList = nested.getFirstChild()
                assertThat(nestedModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(nestedModifierList.label).isEqualTo("")

                assertThat(nestedModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(nestedModifierList.getFirstChild().label).isEqualTo("protected")

                assertThat(nestedModifierList.getChild(1).type).isEqualTo("ModifierEntry")
                assertThat(nestedModifierList.getChild(1).label).isEqualTo("inner")
            }

        }

    }
})

