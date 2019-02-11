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
            afterGroup {
                printAST(rootNode)
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