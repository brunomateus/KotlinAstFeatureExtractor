package fr.uphf.ast

import fr.uphf.kastree.json.test.getASTasJson
import fr.uphf.kastree.json.test.printAST
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object FunctionSpek : Spek({
    Feature("Kotlin Function") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var namedFunctionNode: ASTNode

        Scenario("Very simple function") {

            Given("A function without parameter and return "){
                code = """
fun test() {
		println("Hello " + args[0] + "!")
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named test") {
                namedFunctionNode = rootNode.getChild(2)
                assertThat(namedFunctionNode.type).isEqualTo("KtNamedFunction")
                assertThat(namedFunctionNode.label).isEqualTo("test")
            }

            And("it should have two children") {
                assertThat(namedFunctionNode.children).hasSize(2)
            }

            And("the first should be the parameter list and should be empty") {
                val namedFunctionParameterList = namedFunctionNode.getFirstChild()
                assertThat(namedFunctionParameterList.type).isEqualTo("KtParameterList")
                assertThat(namedFunctionParameterList.label).isEqualTo("")

                assertThat(namedFunctionParameterList.children).isEmpty()
            }

            And("the other children should be the function body"){
                assertThat(namedFunctionNode.getChild(1).type).isEqualTo("KtBlockExpression")
                assertThat(namedFunctionNode.getChild(1).label).isEqualTo("")
            }
        }

        Scenario("Simple function with parameter") {

            Given("A function with one parameter and no return"){
                code = """
fun main(args: Array<String>) {
    if (args.size == 0) {
        println("Please provide a name as a command-line argument")
        return
    }
    println("Hello " + args[0] + "!")
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named main") {
                namedFunctionNode = rootNode.getChild(2)
                assertThat(namedFunctionNode.type).isEqualTo("KtNamedFunction")
                assertThat(namedFunctionNode.label).isEqualTo("main")
            }

            And("it should have two children") {
                assertThat(namedFunctionNode.children).hasSize(2)
            }

            lateinit var namedFunctionParameterList: ASTNode
            And("the first should be the parameter list and should have size 1") {
                namedFunctionParameterList = namedFunctionNode.getFirstChild()
                assertThat(namedFunctionParameterList.type).isEqualTo("KtParameterList")
                assertThat(namedFunctionParameterList.label).isEqualTo("")

                assertThat(namedFunctionParameterList.children).hasSize(1)
            }

            And("the parameterList should contain one parameter named args"){
                assertThat(namedFunctionParameterList.getFirstChild().type).isEqualTo(("KtParameter"))
                assertThat(namedFunctionParameterList.getFirstChild().label).isEqualTo("args")
            }

            And("the other children should be the function body"){
                assertThat(namedFunctionNode.getChild(1).type).isEqualTo("KtBlockExpression")
                assertThat(namedFunctionNode.getChild(1).label).isEqualTo("")
            }
        }

        Scenario("Function with parameter with default value") {

            Given("A function more than one parameter with default value"){
                code = """
fun read(b: Array<Byte>, off: Int = 0, len: Int = b.size) {
     	println(off)
		println(len)
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named main") {
                namedFunctionNode = rootNode.getChild(2)
                assertThat(namedFunctionNode.type).isEqualTo("KtNamedFunction")
                assertThat(namedFunctionNode.label).isEqualTo("read")
            }

            And("it should have two children") {
                assertThat(namedFunctionNode.children).hasSize(2)
            }

            lateinit var namedFunctionParameterList: ASTNode
            And("the first should be the parameter list and should have size 3") {
                namedFunctionParameterList = namedFunctionNode.getFirstChild()
                assertThat(namedFunctionParameterList.type).isEqualTo("KtParameterList")
                assertThat(namedFunctionParameterList.label).isEqualTo("")

                assertThat(namedFunctionParameterList.children).hasSize(3)
            }

            And("the first parameter is named b, it is an Array<Byte>"){
                val p1 = namedFunctionParameterList.getFirstChild()
                assertThat(p1.type).isEqualTo("KtParameter")
                assertThat(p1.label).isEqualTo("b")

                assertThat(p1.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(p1.getFirstChild().label).isEqualTo("Array<Byte>")
            }

            And("the second parameter is named off, it is an Int and it has default value 0"){
                val p2 = namedFunctionParameterList.getChild(1)
                assertThat(p2.type).isEqualTo("KtParameter")
                assertThat(p2.label).isEqualTo("off")

                assertThat(p2.children)
                    .hasSize(2)

                assertThat(p2.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(p2.getFirstChild().label).isEqualTo("Int")

                assertThat(p2.getChild(1).type).isEqualTo("KtConstantExpression")
                assertThat(p2.getChild(1).label).isEqualTo("0")
            }

            And("the third parameter is named len, it is an Int and it has default value providad by an Expression"){
                val p3 = namedFunctionParameterList.getChild(2)
                assertThat(p3.type).isEqualTo("KtParameter")
                assertThat(p3.label).isEqualTo("len")

                assertThat(p3.children).hasSize(2)

                assertThat(p3.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(p3.getFirstChild().label).isEqualTo("Int")

                assertThat(p3.getChild(1).type).isEqualTo("KtDotQualifiedExpression")
                assertThat(p3.getChild(1).label).isEqualTo("")
            }

            And("the other children should be the function body"){
                assertThat(namedFunctionNode.getChild(1).type).isEqualTo("KtBlockExpression")
                assertThat(namedFunctionNode.getChild(1).label).isEqualTo("")
            }
        }

        Scenario("Function call with named args") {

            Given("A function more than one parameter with default value," +
                    " and function call with named args"){
                code = """
    fun reformat(str: String,
             normalizeCase: Boolean = true,
             upperCaseFirstLetter: Boolean = true,
             divideByCamelHumps: Boolean = false,
             wordSeparator: Char = ' ') { }

	fun main(args: Array<String>) {
		reformat(str, wordSeparator = '_')
	}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named main") {
                namedFunctionNode = rootNode.getChild(3)
                assertThat(namedFunctionNode.type).isEqualTo("KtNamedFunction")
                assertThat(namedFunctionNode.label).isEqualTo("main")
            }

            And("it should have two children") {
                assertThat(namedFunctionNode.children).hasSize(2)
            }

            lateinit var funcCall: ASTNode
            And("the first should be a function call") {
                val funcBody = namedFunctionNode.getChild(1)
                assertThat(funcBody.type).isEqualTo("KtBlockExpression")
                assertThat(funcBody.label).isEqualTo("")

                funcCall = funcBody.getFirstChild()
                assertThat(funcCall.type).isEqualTo("KtCallExpression")
                assertThat(funcCall.label).isEqualTo("reformat")
            }

            And("the function call should have two children"){
                assertThat(funcCall.children).hasSize(2)
            }

            lateinit var parameterList: ASTNode
            And("th first children should be a KtValueArgumentList with size 2"){
                parameterList = funcCall.getChild(1)
                assertThat(parameterList.type).isEqualTo("KtValueArgumentList")
                assertThat(parameterList.label).isEqualTo("")
                assertThat(parameterList.children).hasSize(2)
            }


            And("Its first argument should  be the variable str"){
                assertThat(parameterList.getFirstChild().type).isEqualTo("KtValueArgument")
                assertThat(parameterList.getFirstChild().label).isEqualTo("str")
            }

            And("Its second argument should be named as wordseparator and it should have a value '_'"){
                assertThat(parameterList.getChild(1).type).isEqualTo("KtValueArgument")
                assertThat(parameterList.getChild(1).label).isEqualTo("'_'")

                assertThat(parameterList.getChild(1).getFirstChild().type).isEqualTo("KtValueArgumentName")
                assertThat(parameterList.getChild(1).getFirstChild().label).isEqualTo("wordSeparator")
            }

        }

        Scenario("Function with variable number of args") {

            Given("A function a variable number of args with generic argument and return type"){
                code = """
    fun <T> asList(vararg ts: T): List<T> {
        val result = ArrayList<T>()
        for (t in ts) // ts is an Array
            result.add(t)
        return result
    }
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }

            Then("it should contain one function named asList") {
                namedFunctionNode = rootNode.getChild(2)
                assertThat(namedFunctionNode.type).isEqualTo("KtNamedFunction")
                assertThat(namedFunctionNode.label).isEqualTo("asList")
            }

            And("it should have four children") {
                assertThat(namedFunctionNode.children).hasSize(4)
            }

            lateinit var typeParameterList: ASTNode
            And("the first children should by a KtTypeParameterList"){
                typeParameterList = namedFunctionNode.getFirstChild()
                assertThat(typeParameterList.type).isEqualTo("KtTypeParameterList")
                assertThat(typeParameterList.label).isEqualTo("")

            }

            And("it should contain one child whose type is KtTypeParameter and whose label should be T"){
                assertThat(typeParameterList.getFirstChild().type).isEqualTo("KtTypeParameter")
                assertThat(typeParameterList.getFirstChild().label).isEqualTo("T")
            }


            lateinit var parameterList: ASTNode
            And("the second children should by a KtParameterList"){
                parameterList = namedFunctionNode.getChild(1)
                assertThat(parameterList.type).isEqualTo("KtParameterList")
                assertThat(parameterList.label).isEqualTo("")

            }

            lateinit var parameter: ASTNode
            And("Its first parameter should  be named ts"){
                parameter = parameterList.getFirstChild()
                assertThat(parameter.type).isEqualTo("KtParameter")
                assertThat(parameter.label).isEqualTo("ts")
            }


            And("It should have a modifier vararg"){
                val modifierList = parameter.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("vararg")
            }

            And("The third children of the named function should be the return type, List<T>"){
                val returnType = namedFunctionNode.getChild(2)
                assertThat(returnType.type).isEqualTo("KtTypeReference")
                assertThat(returnType.label).isEqualTo("List<T>")
            }

        }


    }

    Feature("Kotlin Extension Function") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var namedFunctionNode: ASTNode

        Scenario("Function with variable number of args") {

            Given("A Extension function with a regular notation"){
                code = """
    fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
		val tmp = this[index1] // 'this' corresponds to the list
		this[index1] = this[index2]
		this[index2] = tmp
	}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named swap") {
                namedFunctionNode = rootNode.getChild(2)
                assertThat(namedFunctionNode.type).isEqualTo("KtNamedFunction")
                assertThat(namedFunctionNode.label).isEqualTo("swap")
            }

            And("it should have four children") {
                assertThat(namedFunctionNode.children).hasSize(4)
            }

            lateinit var typeParameterList: ASTNode
            And("the first children should by a KtTypeParameterList"){
                typeParameterList = namedFunctionNode.getFirstChild()
                assertThat(typeParameterList.type).isEqualTo("KtTypeParameterList")
                assertThat(typeParameterList.label).isEqualTo("")

            }

            And("it should contain one child whose type is KtTypeParameter and whose label should be T"){
                assertThat(typeParameterList.getFirstChild().type).isEqualTo("KtTypeParameter")
                assertThat(typeParameterList.getFirstChild().label).isEqualTo("T")
            }

            And("the second child should be A KtReceiverType, MutableList<T>"){
                val receiverType = namedFunctionNode.getChild(1)
                assertThat(receiverType.type).isEqualTo("KtTypeReference")
                assertThat(receiverType.label).isEqualTo("MutableList<T>")
            }


            And("the second children should by a KtParameterList with size two"){
                val parameterList = namedFunctionNode.getChild(2)
                assertThat(parameterList.type).isEqualTo("KtParameterList")
                assertThat(parameterList.label).isEqualTo("")

                assertThat(parameterList.children).hasSize(2)

            }

        }

        Scenario("Function with variable number of args") {

            Given("A infix extension function"){
                code = """
    infix fun Int.shl(x: Int): Int {
    }
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one function named shl") {
                namedFunctionNode = rootNode.getChild(2)
                assertThat(namedFunctionNode.type).isEqualTo("KtNamedFunction")
                assertThat(namedFunctionNode.label).isEqualTo("shl")
            }

            And("it should have five children") {
                assertThat(namedFunctionNode.children).hasSize(5)
            }

            And("the first children should by a KtModifierList and should contain the infix modifier"){
                val modifierList = namedFunctionNode.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("infix")
            }

            And("the second children should by a KtTypeReference, Int"){
                val receiverType = namedFunctionNode.getChild(1)
                assertThat(receiverType.type).isEqualTo("KtTypeReference")
                assertThat(receiverType.label).isEqualTo("Int")

            }

            And("the third children should by a KtParameterList"){
                val parameterList = namedFunctionNode.getChild(2)
                assertThat(parameterList.type).isEqualTo("KtParameterList")
                assertThat(parameterList.label).isEqualTo("")
            }

            And("the fourth children should by a KtTypeReference which correspond to the return type, Int"){
                val returnType = namedFunctionNode.getChild(3)
                assertThat(returnType.type).isEqualTo("KtTypeReference")
                assertThat(returnType.label).isEqualTo("Int")
            }

            And("The last children should be the function body, KtBlockExpression"){
                val funcBody = namedFunctionNode.getChild(4)
                assertThat(funcBody.type).isEqualTo("KtBlockExpression")
                assertThat(funcBody.label).isEqualTo("")
            }


        }


    }

})

