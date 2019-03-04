package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ExpressionSpek : Spek({
    Feature("Kotlin Expressions") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var cls: ASTNode

        Scenario("Binary, Unary expressions") {

            Given("A try catch block with different exceptions "){
                code = """
fun main(args: Array<String>) {
    val x = 0
    val y = ++x
    val z = y--
    val w = x + y

    val a = true
    val b = !a
}
""".trimIndent()
            }

            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named main") {
                cls = rootNode.getChild(2)
                assertThat(cls.type).isEqualTo("KtNamedFunction")
                assertThat(cls.label).isEqualTo("main")
            }

            And("it should have two children") {
                assertThat(cls.children).hasSize(2)
            }

            lateinit var mainBody: ASTNode
            Then("it should contains a property y that is receives the value of PrefixExpression using the operator ++") {
                mainBody = cls.getChild(1)

                val y = mainBody.getChild(1)

                assertThat(y.type).isEqualTo("KtProperty")

                val prefixOperation = y.getFirstChild()
                assertThat(prefixOperation.type).isEqualTo("KtPrefixExpression")
                assertThat(prefixOperation.label).isEqualTo("")

                assertThat(prefixOperation.getFirstChild().type).isEqualTo("KtOperationReferenceExpression")
                assertThat(prefixOperation.getFirstChild().label).isEqualTo("++")

                assertThat(prefixOperation.getChild(1).type).isEqualTo("KtNameReferenceExpression")
                assertThat(prefixOperation.getChild(1).label).isEqualTo("x")
            }

            And("it should contains a property z that is receives the value of PostfixExpression using the operator --") {
                mainBody = cls.getChild(1)

                val z = mainBody.getChild(2)

                assertThat(z.type).isEqualTo("KtProperty")

                val postfixOperation = z.getFirstChild()
                assertThat(postfixOperation.type).isEqualTo("KtPostfixExpression")
                assertThat(postfixOperation.label).isEqualTo("")

                assertThat(postfixOperation.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(postfixOperation.getFirstChild().label).isEqualTo("y")

                assertThat(postfixOperation.getChild(1).type).isEqualTo("KtOperationReferenceExpression")
                assertThat(postfixOperation.getChild(1).label).isEqualTo("--")
            }

            And("it should contains a property w that is receives the value of a BinaryExpression with the operator +") {
                val w = mainBody.getChild(3)

                assertThat(w.type).isEqualTo("KtProperty")

                val binaryExpr = w.getFirstChild()
                assertThat(binaryExpr.type).isEqualTo("KtBinaryExpression")
                assertThat(binaryExpr.label).isEqualTo("")

                assertThat(binaryExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(binaryExpr.getFirstChild().label).isEqualTo("x")

                assertThat(binaryExpr.getChild(1).type).isEqualTo("KtOperationReferenceExpression")
                assertThat(binaryExpr.getChild(1).label).isEqualTo("+")

                assertThat(binaryExpr.getChild(2).type).isEqualTo("KtNameReferenceExpression")
                assertThat(binaryExpr.getChild(2).label).isEqualTo("y")
            }

            And("it should contains a property B that is receives the value of PrefixExpression using the operator !") {
                val b = mainBody.getChild(5)

                assertThat(b.type).isEqualTo("KtProperty")

                val prefixOperation = b.getFirstChild()
                assertThat(prefixOperation.type).isEqualTo("KtPrefixExpression")
                assertThat(prefixOperation.label).isEqualTo("")

                assertThat(prefixOperation.getFirstChild().type).isEqualTo("KtOperationReferenceExpression")
                assertThat(prefixOperation.getFirstChild().label).isEqualTo("!")

                assertThat(prefixOperation.getChild(1).type).isEqualTo("KtNameReferenceExpression")
                assertThat(prefixOperation.getChild(1).label).isEqualTo("a")
            }


        }

        Scenario("Is expressions") {

            Given("A try catch block with different exceptions "){
                code = """
fun main(args: Array<String>) {
    val x = 0
    if (x is Int) {
    	println(\"\"\" x y z ${'$'}\{x} ${'$'}\{y} ${'$'}\{z}\"\"\")
    }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named main") {
                cls = rootNode.getChild(2)
                assertThat(cls.type).isEqualTo("KtNamedFunction")
                assertThat(cls.label).isEqualTo("main")
            }

            And("it should have two children") {
                assertThat(cls.children).hasSize(2)
            }

            lateinit var mainBody: ASTNode
            Then("it should contains a KtIfExpression with condition being a KtIsExpression") {
                mainBody = cls.getChild(1)

                val ifExpr = mainBody.getChild(1)

                assertThat(ifExpr.type).isEqualTo("KtIfExpression")

                val condition = ifExpr.getFirstChild().getFirstChild()

                assertThat(condition.type).isEqualTo("KtIsExpression")
                assertThat(condition.label).isEqualTo("")

                assertThat(condition.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(condition.getFirstChild().label).isEqualTo("x")

                assertThat(condition.getChild(1).type).isEqualTo("KtOperationReferenceExpression")
                assertThat(condition.getChild(1).label).isEqualTo("is")

                val typeReference = condition.getChild(2)
                assertThat(typeReference.type).isEqualTo("KtTypeReference")
                assertThat(typeReference.label).isEqualTo("")

                assertThat(typeReference.getFirstChild().getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(typeReference.getFirstChild().getFirstChild().label).isEqualTo("Int")
            }

        }

        Scenario("This expressions") {

            Given("Different ways of using this "){
                code = """
class A { // implicit label @A
	inner class B { // implicit label @B
		fun Int.foo() { // implicit label @foo
			val a = this@A // A's this
			val b = this@B // B's this

			val c = this // foo()'s receiver, an Int

			val funLit = lambda@ fun String.() {
				val d = this // funLit's receiver
			}
		}
	}
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val clsName= "A"
            Then("it should contain one class named $clsName") {
                cls = rootNode.getChild(2)
                assertThat(cls.type).isEqualTo("KtClass")
                assertThat(cls.label).isEqualTo(clsName)
            }

            And("it should have one children") {
                assertThat(cls.children).hasSize(1)
            }

            lateinit var clsBody: ASTNode
            lateinit var innerCls: ASTNode
            Then("it should contains a inner class named B") {
                clsBody = cls.getFirstChild()

                assertThat(clsBody.type).isEqualTo("KtClassBody")
                assertThat(clsBody.label).isEqualTo("")

                innerCls = clsBody.getFirstChild()

                assertThat(innerCls.type).isEqualTo("KtClass")
                assertThat(innerCls.label).isEqualTo("B")

                val modifierList = innerCls.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.children).hasSize(1)

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("inner")
            }

            lateinit var funcBody: ASTNode
            And("The inner class should have a named function call foo"){
                val func = innerCls.getChild(1).getFirstChild()
                funcBody = func.getChild(2)

                assertThat(func.type).isEqualTo("KtNamedFunction")
                assertThat(func.label).isEqualTo("foo")

                assertThat(funcBody.type).isEqualTo("KtBlockExpression")
                assertThat(funcBody.label).isEqualTo("")
            }

            And("A property read-only a should receive a KtThisExpression that references the class A"){
                val property = funcBody.getFirstChild()

                assertThat(property.type).isEqualTo("KtProperty")
                assertThat(property.label).isEqualTo("val a")

                val thisExpr = property.getFirstChild()
                assertThat(thisExpr.type).isEqualTo("KtThisExpression")
                assertThat(thisExpr.label).isEqualTo("this@A")

                assertThat(thisExpr.children).hasSize(2)

                assertThat(thisExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(thisExpr.getFirstChild().label).isEqualTo("this")

                assertThat(thisExpr.getChild(1).type).isEqualTo("KtContainerNode")
                assertThat(thisExpr.getChild(1).label).isEqualTo("")

                assertThat(thisExpr.getChild(1).getFirstChild().type).isEqualTo("KtLabelReferenceExpression")
                assertThat(thisExpr.getChild(1).getFirstChild().label).isEqualTo("@A")
            }

            And("A property read-only b should receive a KtThisExpression that references the class B"){
                val property = funcBody.getChild(1)

                assertThat(property.type).isEqualTo("KtProperty")
                assertThat(property.label).isEqualTo("val b")

                val thisExpr = property.getFirstChild()
                assertThat(thisExpr.type).isEqualTo("KtThisExpression")
                assertThat(thisExpr.label).isEqualTo("this@B")

                assertThat(thisExpr.children).hasSize(2)

                assertThat(thisExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(thisExpr.getFirstChild().label).isEqualTo("this")

                assertThat(thisExpr.getChild(1).type).isEqualTo("KtContainerNode")
                assertThat(thisExpr.getChild(1).label).isEqualTo("")

                assertThat(thisExpr.getChild(1).getFirstChild().type).isEqualTo("KtLabelReferenceExpression")
                assertThat(thisExpr.getChild(1).getFirstChild().label).isEqualTo("@B")
            }

            And("A property read-only a should receive a KtThisExpression that references the receiver Int"){
                val property = funcBody.getChild(2)

                assertThat(property.type).isEqualTo("KtProperty")
                assertThat(property.label).isEqualTo("val c")

                val thisExpr = property.getFirstChild()
                assertThat(thisExpr.type).isEqualTo("KtThisExpression")
                assertThat(thisExpr.label).isEqualTo("this")

                assertThat(thisExpr.children).hasSize(1)

                assertThat(thisExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(thisExpr.getFirstChild().label).isEqualTo("this")
            }

            And("A property read-only funLit that uses a KtLabeledExpression"){
                val property = funcBody.getChild(3)

                assertThat(property.type).isEqualTo("KtProperty")
                assertThat(property.label).isEqualTo("val funLit")

                val thisExpr = property.getFirstChild()
                assertThat(thisExpr.type).isEqualTo("KtLabeledExpression")
                assertThat(thisExpr.label).isEqualTo("lambda")

                assertThat(thisExpr.children).hasSize(2)

                assertThat(thisExpr.getFirstChild().type).isEqualTo("KtContainerNode")
                assertThat(thisExpr.getFirstChild().label).isEqualTo("")

                assertThat(thisExpr.getFirstChild().getFirstChild().type).isEqualTo("KtLabelReferenceExpression")
                assertThat(thisExpr.getFirstChild().getFirstChild().label).isEqualTo("lambda@")
            }

        }


    }


})

