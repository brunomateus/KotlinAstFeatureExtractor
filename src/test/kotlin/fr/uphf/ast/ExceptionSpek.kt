package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ExceptionSpek : Spek({
    Feature("Kotlin Handling Exceptions") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var main: ASTNode

        Scenario("Catching exceptions") {

            Given("A try catch block with different exceptions "){
                code = """
fun main(args: Array<String>) {
    try {
        var a = 0
        var x = 7 / a

    } catch (e: ArithmeticException) {
        println("Arthimetic Exception")
    } catch (e: Exception) {
        println("Exception occured. To print stacktrace use e")
    } finally {
        println("Finally. It's over")
    }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named main") {
                main = rootNode.getChild(2)
                assertThat(main.type).isEqualTo("KtNamedFunction")
                assertThat(main.label).isEqualTo("main")
            }

            And("it should have two children") {
                assertThat(main.children).hasSize(2)
            }


            lateinit var tryExpression: ASTNode
            Then("it should contains try/catch block") {
                val block = main.getChild(1)
                assertThat(block.type).isEqualTo("KtBlockExpression")
                assertThat(block.label).isEqualTo("")


                tryExpression = block.getFirstChild()
                assertThat(tryExpression.type).isEqualTo("KtTryExpression")
                assertThat(tryExpression.label).isEqualTo("try")

                assertThat(tryExpression.children).hasSize(4)
            }

            And("it should handle two types of exceptions, ArithmeticException and Exception"){
                assertThat(tryExpression.getFirstChild().type).isEqualTo("KtBlockExpression")
                assertThat(tryExpression.getFirstChild().label).isEqualTo("")

                assertThat(tryExpression.getChild(1).type).isEqualTo("KtCatchClause")
                assertThat(tryExpression.getChild(1).label).isEqualTo("catch")
                assertThat(tryExpression.getChild(1).label).isEqualTo("catch")

                assertThat(tryExpression.getChild(2).type).isEqualTo("KtCatchClause")
                assertThat(tryExpression.getChild(2).label).isEqualTo("catch")
            }

            And("it should have a finally block"){
                assertThat(tryExpression.getChild(3).type).isEqualTo("KtFinallySection")
                assertThat(tryExpression.getChild(3).label).isEqualTo("finally")
            }
        }

        Scenario("Throwing exceptions") {

            Given("A try catch block with different exceptions "){
                code = """
fun fail(message: String): Nothing {
    	throw IllegalArgumentException(message)
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named fail") {
                main = rootNode.getChild(2)
                assertThat(main.type).isEqualTo("KtNamedFunction")
                assertThat(main.label).isEqualTo("fail")
            }

            And("it should have three children") {
                assertThat(main.children).hasSize(3)
            }


            Then("it should throws an IllegalArgumentException") {
                val block = main.getChild(2)
                assertThat(block.type).isEqualTo("KtBlockExpression")
                assertThat(block.label).isEqualTo("")

                val throwExpr = block.getFirstChild()
                assertThat(throwExpr.type).isEqualTo("KtThrowExpression")
                assertThat(throwExpr.label).isEqualTo("throw")

                assertThat(throwExpr.getFirstChild().type).isEqualTo("KtCallExpression")
                assertThat(throwExpr.getFirstChild().label).isEqualTo("IllegalArgumentException")
            }

        }

    }


})

