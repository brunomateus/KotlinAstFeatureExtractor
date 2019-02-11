package fr.uphf.kastree.json.test

import com.beust.klaxon.JsonBase
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import fr.uphf.ast.ASTExtractor
import fr.uphf.ast.ASTNode
import org.assertj.core.api.Assertions.*
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.io.File

object ControFlowSpek : Spek({
    Feature("For statement") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var forExpression: ASTNode

        Scenario("Simple for each") {

            Given("A for stmt with only variable declaration"){
                code = """
fun main(args: Array<String>) {
    for (name in args)
        println("Hello, name!")
}""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one ForExpression") {
                forExpression = rootNode.getChild(2).getChild(1).getFirstChild()
                assertThat(forExpression.type).isEqualTo("KtForExpression")
                assertThat(forExpression.label).isEqualTo("for")
            }

            And("the forExpression should have three children") {
                assertThat(forExpression.children).hasSize(3)
            }

            And("the first children should be the parameter name") {
                assertThat(forExpression.getFirstChild().type).isEqualTo("KtParameter")
                assertThat(forExpression.getFirstChild().label).isEqualTo("name")
            }

            And("the other two children should have empty labels"){
                assertThat(forExpression.getChild(1).type).isEqualTo("KtContainerNode")
                assertThat(forExpression.getChild(1).label).isEqualTo("")

                assertThat(forExpression.getChild(2).type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(forExpression.getChild(2).label).isEqualTo("")
            }
        }

        Scenario("For each with RangeExpressions") {

            Given("A for stmt with RangeExpressions"){
                code = """
	fun main(args: Array<String>) {
		for (i in 1..3) {
			println(i)
		}
}""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one ForExpression") {
                forExpression = rootNode.getChild(2).getChild(1).getFirstChild()
                assertThat(forExpression.type).isEqualTo("KtForExpression")
                assertThat(forExpression.label).isEqualTo("for")
            }

            And("the forExpression should have three children") {
                assertThat(forExpression.children).hasSize(3)
            }

            And("the first children should be the parameter i") {
                assertThat(forExpression.getFirstChild().type).isEqualTo("KtParameter")
                assertThat(forExpression.getFirstChild().label).isEqualTo("i")
            }

            And("the container node should have a binaryExpression with the operator '..'"){
                assertThat(forExpression.getChild(1).type).isEqualTo("KtContainerNode")
                assertThat(forExpression.getChild(1).label).isEqualTo("")

                val binaryExpression = forExpression.getChild(1).getFirstChild()

                assertThat(binaryExpression.type).isEqualTo("KtBinaryExpression")
                assertThat(binaryExpression.label).isEqualTo("")

                assertThat(binaryExpression.getFirstChild().type).isEqualTo("KtConstantExpression")
                assertThat(binaryExpression.getFirstChild().label).isEqualTo("1")

                assertThat(binaryExpression.getChild(1).type).isEqualTo("KtOperationReferenceExpression")
                assertThat(binaryExpression.getChild(1).label).isEqualTo("..")

                assertThat(binaryExpression.getChild(2).type).isEqualTo("KtConstantExpression")
                assertThat(binaryExpression.getChild(2).label).isEqualTo("3")
            }

            And("the for's body should have empty label"){
                assertThat(forExpression.getChild(2).type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(forExpression.getChild(2).label).isEqualTo("")
            }

            Given("A for stmt with Range and Sequence"){
                code = """
	fun main(args: Array<String>) {
		for (i in 6 downTo 0 step 2) {
			println(i)
		}
}""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one ForExpression") {
                forExpression = rootNode.getChild(2).getChild(1).getFirstChild()
                assertThat(forExpression.type).isEqualTo("KtForExpression")
                assertThat(forExpression.label).isEqualTo("for")
            }

            And("the forExpression should have three children") {
                assertThat(forExpression.children).hasSize(3)
            }

            And("the first children should be the parameter i") {
                assertThat(forExpression.getFirstChild().type).isEqualTo("KtParameter")
                assertThat(forExpression.getFirstChild().label).isEqualTo("i")
            }

            And("the container node should have two binaryExpressions "){
                assertThat(forExpression.getChild(1).type).isEqualTo("KtContainerNode")
                assertThat(forExpression.getChild(1).label).isEqualTo("")

                val firstBinaryExpr = forExpression.getChild(1).getFirstChild()

                assertThat(firstBinaryExpr.type).isEqualTo("KtBinaryExpression")
                assertThat(firstBinaryExpr.label).isEqualTo("")

                assertThat(firstBinaryExpr.getChild(1).type).isEqualTo("KtOperationReferenceExpression")
                assertThat(firstBinaryExpr.getChild(1).label).isEqualTo("step")

                val secBinaryExpr = firstBinaryExpr.getFirstChild()
                assertThat(secBinaryExpr.getFirstChild().type).isEqualTo("KtConstantExpression")
                assertThat(secBinaryExpr.getFirstChild().label).isEqualTo("6")

                assertThat(secBinaryExpr.getChild(1).type).isEqualTo("KtOperationReferenceExpression")
                assertThat(secBinaryExpr.getChild(1).label).isEqualTo("downTo")

                assertThat(secBinaryExpr.getChild(2).type).isEqualTo("KtConstantExpression")
                assertThat(secBinaryExpr.getChild(2).label).isEqualTo("0")
            }

            And("the for's body should have empty label"){
                assertThat(forExpression.getChild(2).type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(forExpression.getChild(2).label).isEqualTo("")
            }

        }

        Scenario("For with multiple variable declarations") {

            Given("A for stmt with DestructuringDeclaration"){
                code = """
	fun main(args: Array<String>) {
		for ((index, value) in array.withIndex()) {
		println("the element at index is value")
	}
}""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one ForExpression") {
                forExpression = rootNode.getChild(2).getChild(1).getFirstChild()
                assertThat(forExpression.type).isEqualTo("KtForExpression")
                assertThat(forExpression.label).isEqualTo("for")
            }

            And("the forExpression should have three children") {
                assertThat(forExpression.children).hasSize(3)
            }

            And("the first children should be the parameter with no name") {
                assertThat(forExpression.getFirstChild().type).isEqualTo("KtParameter")
                assertThat(forExpression.getFirstChild().label).isEqualTo("")
            }

            And("the parameter should be a DestructuringDeclaration with two entries"){
                val dest = forExpression.getFirstChild().getFirstChild()
                assertThat(dest.type).isEqualTo("KtDestructuringDeclaration")
                assertThat(dest.label).isEqualTo("")

                assertThat(dest.getFirstChild().type).isEqualTo("KtDestructuringDeclarationEntry")
                assertThat(dest.getFirstChild().label).isEqualTo("index")

                assertThat(dest.getChild(1).type).isEqualTo("KtDestructuringDeclarationEntry")
                assertThat(dest.getChild(1).label).isEqualTo("value")
            }

            And("the other two children should have empty labels"){
                assertThat(forExpression.getChild(1).type).isEqualTo("KtContainerNode")
                assertThat(forExpression.getChild(1).label).isEqualTo("")

                assertThat(forExpression.getChild(2).type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(forExpression.getChild(2).label).isEqualTo("")
            }
        }
    }

    Feature("while statement") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var whileLoop: ASTNode

        Scenario("Regular While") {

            Given("A regular while with break"){
                code = """"
fun main(args: Array<String>) {
	var x = args.size
    while (x > 0) {
    	x--
    	if (x == 5) break
	}
}""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one WhileExpression") {
                whileLoop = rootNode.getChild(2).getChild(1).getChild(1)
                assertThat(whileLoop.type).isEqualTo("KtWhileExpression")
                assertThat(whileLoop.label).isEqualTo("while")
            }

            And("the WhileExpression should have two children") {
                assertThat(whileLoop.children).hasSize(2)
            }

            lateinit var whileBody: ASTNode

            And("the two children should have empty labels"){
                assertThat(whileLoop.getFirstChild().type).isEqualTo("KtContainerNode")
                assertThat(whileLoop.getFirstChild().label).isEqualTo("")

                whileBody = whileLoop.getChild(1)

                assertThat(whileBody.type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(whileBody.label).isEqualTo("")
            }

            And("The break expression should have a label break"){
                val breakExpression = whileBody.getFirstChild().getChild(1).getChild(1).getFirstChild()
                assertThat(breakExpression.type).isEqualTo("KtBreakExpression")
                assertThat(breakExpression.label).isEqualTo("break")
            }
        }

        Scenario("Do While") {

            Given("A do while with continue"){
                code = """"
fun main(args: Array<String>) {
	var x = args.size
    do {
    	if (x % 2 == 0) continue
    	x--
	} while (x > 0)
}""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one DoWhileExpression") {
                whileLoop = rootNode.getChild(2).getChild(1).getChild(1)
                assertThat(whileLoop.type).isEqualTo("KtDoWhileExpression")
                assertThat(whileLoop.label).isEqualTo("do-while")
            }

            And("the DoWhileExpression should have two children") {
                assertThat(whileLoop.children).hasSize(2)
            }

            lateinit var doWhileBody: ASTNode

            And("the two children should have empty labels"){
                doWhileBody = whileLoop.getFirstChild()

                assertThat(doWhileBody.type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(doWhileBody.label).isEqualTo("")


                assertThat(whileLoop.getChild(1).type).isEqualTo("KtContainerNode")
                assertThat(whileLoop.getChild(1).label).isEqualTo("")
            }

            And("The continue expression should have a label continue"){
                val continueExpression = doWhileBody.getFirstChild().getFirstChild().getChild(1).getFirstChild()
                assertThat(continueExpression.type).isEqualTo("KtContinueExpression")
                assertThat(continueExpression.label).isEqualTo("continue")
            }
        }

    }

    Feature("When statement") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var whenExpression: ASTNode

        Scenario("Condition with Expressions") {

            Given("A when that uses conditions with expressions") {
                code = """"
fun main(args: Array<String>) {
		val language = if (args.size == 0) "EN" else args[0]
		when (language) {
			"EN" -> "Hello!"
			"FR" -> "Salut!"
			"IT" -> "Ciao!"
			else -> "Sorry, I can't greet you in language yet"
		}
	}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one WhenExpression") {
                whenExpression = rootNode.getChild(2).getChild(1).getChild(1)
                assertThat(whenExpression.type).isEqualTo("KtWhenExpression")
                assertThat(whenExpression.label).isEqualTo("when")

            }

            And("the whenExpression should have five children") {
                assertThat(whenExpression.children).hasSize(5)
            }

            And("the first childen should correpond to the variable been evaluated"){
                assertThat(whenExpression.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(whenExpression.getFirstChild().label).isEqualTo("language")
            }

            And("Each children, except the last one, should be a KtWhenEntry with two children each") {
                val conditions = listOf("\"EN\"", "\"FR\"", "\"IT\"")
                for (i in 1..3) {
                    val whenEntry = whenExpression.getChild(i)
                    assertThat(whenEntry.type).isEqualTo("KtWhenEntry")
                    assertThat(whenEntry.label).isEqualTo("")

                    assertThat(whenEntry.children)
                        .hasSize(2)

                    assertThat(whenEntry.getFirstChild().type).isEqualTo("KtWhenConditionWithExpression")
                    assertThat(whenEntry.getFirstChild().label).isEqualTo(conditions[i - 1])
                }
            }

            And("The last children should be a Else") {
                assertThat(whenExpression.getChild(4).type).isEqualTo("KtWhenEntry")
                assertThat(whenExpression.getChild(4).label).isEqualTo("else")
            }
        }

        Scenario("Range conditions"){

            Given("A when that uses range conditions"){
                code = """"
fun main(args: Array<String>) {
		when (x) {
			in 1..10 -> print("x is in the range")
			in validNumbers -> print("x is valid")
			!in 10..20 -> print("x is outside the range")
			else -> print("none of the above")
		}
	}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one WhenExpression") {
                whenExpression = rootNode.getChild(2).getChild(1).getFirstChild()
                assertThat(whenExpression.type).isEqualTo("KtWhenExpression")
                assertThat(whenExpression.label).isEqualTo("when")

            }

            And("the whenExpression should have five children") {
                assertThat(whenExpression.children).hasSize(5)
            }

            And("the first childen should correpond to the variable been evaluated"){
                assertThat(whenExpression.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(whenExpression.getFirstChild().label).isEqualTo("x")
            }

            And("Each children, except the first and the last one, should be a KtWhenEntry with two children each"){
                val conditions = listOf("in 1..10", "in validNumbers", "!in 10..20")
                for (i in 1..3) {
                    val whenEntry = whenExpression.getChild(i)
                    assertThat(whenEntry.type).isEqualTo("KtWhenEntry")
                    assertThat(whenEntry.label).isEqualTo("")

                    assertThat(whenEntry.children)
                        .hasSize(2)

                    assertThat(whenEntry.getFirstChild().type).isEqualTo("KtWhenConditionInRange")
                    assertThat(whenEntry.getFirstChild().label).isEqualTo(conditions[i - 1])
                }
            }

            And("The last children should be a Else"){
                assertThat(whenExpression.getChild(4).type).isEqualTo("KtWhenEntry")
                assertThat(whenExpression.getChild(4).label).isEqualTo("else")
            }

        }

        Scenario("Using the 'is' pattern"){

            Given("A when that uses the pattern 'is'"){
                code = """
fun hasPrefix(x: Any) = when(x) {
    		is String -> x.startsWith("prefix")
			else -> false
}

""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one WhenExpression") {
                whenExpression = rootNode.getChild(2).getChild(1)
                assertThat(whenExpression.type).isEqualTo("KtWhenExpression")
                assertThat(whenExpression.label).isEqualTo("when")

            }

            And("the whenExpression should have three children") {
                assertThat(whenExpression.children).hasSize(3)
            }

            And("The WhenEntry should have a KtWhenConditionIsPattern"){
                val whenEntry = whenExpression.getChild(1)
                assertThat(whenEntry.type).isEqualTo("KtWhenEntry")
                assertThat(whenEntry.label).isEqualTo("")

                assertThat(whenEntry.children).hasSize(2)

                assertThat(whenEntry.getFirstChild().type).isEqualTo("KtWhenConditionIsPattern")
                assertThat(whenEntry.getFirstChild().label).isEqualTo("is String")
            }

            And("The last children should be a Else"){
                assertThat(whenExpression.getChild(2).type).isEqualTo("KtWhenEntry")
                assertThat(whenExpression.getChild(2).label).isEqualTo("else")
            }

        }

    }

    Feature("If statement") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ifExpression: ASTNode

        Scenario("If without else") {

            Given("A if with only one branch") {
                code = """"
fun main(args: Array<String>) {
			val a = 5
			var b = 7
			var max = a
			if (a < b) max = b
		}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one IfExpression") {
                ifExpression = rootNode.getChild(2).getChild(1).getChild(3)
                assertThat(ifExpression.type).isEqualTo("KtIfExpression")
                assertThat(ifExpression.label).isEqualTo("if")

            }

            And("the whenExpression should have two children") {
                assertThat(ifExpression.children).hasSize(2)
            }

            And("The KtContainerNode should contain a binaryExpression"){
                assertThat(ifExpression.getFirstChild().type).isEqualTo("KtContainerNode")
                assertThat(ifExpression.getFirstChild().label).isEqualTo("")

                assertThat(ifExpression.getFirstChild().getFirstChild().type).isEqualTo("KtBinaryExpression")
                assertThat(ifExpression.getFirstChild().getFirstChild().label).isEqualTo("")
            }

            And("The KtContainerNodeForControlStructureBody should have a empty label"){
                assertThat(ifExpression.getChild(1).type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(ifExpression.getChild(1).label).isEqualTo("")
            }
        }

        Scenario("If with else") {

            Given("A if with else") {
                code = """
fun main(args: Array<String>) {
			val a = 5
			var b = 7
			var max = 0
			if (a < b) max = b else max = a
		}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one IfExpression") {
                ifExpression = rootNode.getChild(2).getChild(1).getChild(3)
                assertThat(ifExpression.type).isEqualTo("KtIfExpression")
                assertThat(ifExpression.label).isEqualTo("if")

            }

            And("the whenExpression should have three children") {
                assertThat(ifExpression.children).hasSize(3)
            }

            And("The KtContainerNode should contain a binaryExpression"){
                assertThat(ifExpression.getFirstChild().type).isEqualTo("KtContainerNode")
                assertThat(ifExpression.getFirstChild().label).isEqualTo("")

                assertThat(ifExpression.getFirstChild().getFirstChild().type).isEqualTo("KtBinaryExpression")
                assertThat(ifExpression.getFirstChild().getFirstChild().label).isEqualTo("")
            }

            And("It should contain two KtContainerNodeForControlStructureBody with empty labels"){
                assertThat(ifExpression.getChild(1).type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(ifExpression.getChild(1).label).isEqualTo("")

                assertThat(ifExpression.getChild(2).type).isEqualTo("KtContainerNodeForControlStructureBody")
                assertThat(ifExpression.getChild(2).label).isEqualTo("")
            }
        }

    }

})

private fun printAST(node: ASTNode) {
    val builder = StringBuilder(Klaxon().toJsonString(node))
    val content = (Parser().parse(builder) as JsonBase).toJsonString(true)
    println(content)
}

val proj by lazy {
    KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(),
        CompilerConfiguration(),
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    ).project
}

private fun getASTasJson(code: String): ASTNode {
    val ktFile = PsiManager.getInstance(proj).findFile(LightVirtualFile("temp.kt", KotlinFileType.INSTANCE, code)) as KtFile
    val parser = ASTExtractor()
    return parser.getASTInJSON(ktFile)
}