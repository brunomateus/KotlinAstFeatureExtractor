package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import fr.uphf.analyze.printAST
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ObjectSpek : Spek({
    Feature("Kotlin object") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ktclass: ASTNode

        Scenario("Regular object") {

            Given("A regular object declared"){
                code = """
object DefaultListener : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {  }

    override fun mouseEntered(e: MouseEvent) {  }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val clsName = "DefaultListener"
            Then("it should contain one class named $clsName") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtObjectDeclaration")
                assertThat(ktclass.label).isEqualTo(clsName)
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            Then("it should call the constructor of the class MouseAdapter ") {
                val superList = ktclass.getFirstChild()
                assertThat(superList.type).isEqualTo("KtSuperTypeList")
                assertThat(superList.label).isEqualTo("")

                val superTypeEntry = superList.getFirstChild()
                assertThat(superTypeEntry.type).isEqualTo("KtSuperTypeCallEntry")
                assertThat(superTypeEntry.label).isEqualTo("")

                val consCallExpr = superTypeEntry.getFirstChild()
                assertThat(consCallExpr.type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(consCallExpr.label).isEqualTo("MouseAdapter")

//                assertThat(consCallExpr.getFirstChild().type).isEqualTo("KtTypeReference")
//                assertThat(consCallExpr.getFirstChild().label).isEqualTo("MouseAdapter")
            }

            And("the last children should be the class body with to methods"){
                val classBody = ktclass.getChild(1)
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")

                assertThat(classBody.getFirstChild().type).isEqualTo("KtNamedFunction")
                assertThat(classBody.getFirstChild().label).isEqualTo("mouseClicked")

                assertThat(classBody.getChild(1).type).isEqualTo("KtNamedFunction")
                assertThat(classBody.getChild(1).label).isEqualTo("mouseEntered")
            }
        }

        Scenario("Companion object") {

            Given("A clas that has companion object declared"){
                code = """
class MyClass {
    companion object Factory {
        fun create(): MyClass = MyClass()
    }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }

            val clsName = "MyClass"
            Then("it should contain one class named $clsName") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)
            }

            val nChildren = 1
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            And("shoudl have a companion object, named Factory, declared"){
                val classBody = ktclass.getFirstChild()
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")

                assertThat(classBody.getFirstChild().type).isEqualTo("KtObjectDeclaration")
                assertThat(classBody.getFirstChild().label).isEqualTo("Factory")

                val objModifierList = classBody.getFirstChild().getFirstChild()

                assertThat(objModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(objModifierList.label).isEqualTo("")

                assertThat(objModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(objModifierList.getFirstChild().label).isEqualTo("companion")
            }
        }

    }

})

