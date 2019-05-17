package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import fr.uphf.analyze.printAST
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object StringTemplateSpek : Spek({
    Feature("Kotlin String template") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ktclass: ASTNode

        Scenario("String Block Template") {

            Given("String Block Template"){
                code = StringTemplateSpek::class.java.getResource("/test.tk").readText()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }

            val clsName = "KotlinGreetingJoiner"
            Then("it should contain one class named $clsName") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            lateinit var func: ASTNode
            And("the class should define one property and two methods"){
                val classBody = ktclass.getChild(1)
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")

                assertThat(classBody.getFirstChild().type).isEqualTo("KtProperty")
                assertThat(classBody.getFirstChild().label).isEqualTo("names")

                val propertyKeyword = classBody.getFirstChild().getFirstChild()
                assertThat(propertyKeyword.type).isEqualTo("KtPropertyKeyword")
                assertThat(propertyKeyword.label).isEqualTo("val")


                assertThat(classBody.getChild(1).type).isEqualTo("KtNamedFunction")
                assertThat(classBody.getChild(1).label).isEqualTo("addName")

                func = classBody.getChild(2)
                assertThat(func.type).isEqualTo("KtNamedFunction")
                assertThat(func.label).isEqualTo("getJoinedGreeting")
            }

            And("the methods getJoinedGreeting should return a template string"){

                val body = func.getChild(2)
                assertThat(body.type).isEqualTo("KtBlockExpression")
                assertThat(body.label).isEqualTo("")

                val returnExpr = body.getFirstChild()
                assertThat(returnExpr.type).isEqualTo("KtReturnExpression")
                assertThat(returnExpr.label).isEqualTo("return")

                val stringTempExpr = returnExpr.getFirstChild()
                assertThat(stringTempExpr.type).isEqualTo("KtStringTemplateExpression")
                assertThat(stringTempExpr.label).isEqualTo("")

                assertThat(stringTempExpr.children).hasSize(3)

                var blockTempString = stringTempExpr.getFirstChild()
                assertThat(blockTempString.type).isEqualTo("KtBlockStringTemplateEntry")
                assertThat(blockTempString.label).isEqualTo("")

                var dotQualExpr = blockTempString.getFirstChild()
                assertThat(dotQualExpr.type).isEqualTo("KtDotQualifiedExpression")
                assertThat(dotQualExpr.label).isEqualTo("")

                assertThat(dotQualExpr.children).hasSize(2)

                assertThat(dotQualExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(dotQualExpr.getFirstChild().label).isEqualTo("greeter")

                assertThat(dotQualExpr.getChild(1).type).isEqualTo("KtCallExpression")
                assertThat(dotQualExpr.getChild(1).label).isEqualTo("")

                assertThat(dotQualExpr.getChild(1).getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(dotQualExpr.getChild(1).getFirstChild().label).isEqualTo("getGreeting")

                var argumentList = dotQualExpr.getChild(1).getChild(1)
                assertThat(argumentList.type).isEqualTo("KtValueArgumentList")
                assertThat(argumentList.label).isEqualTo("")

                assertThat(argumentList.children).hasSize(0)

                assertThat(stringTempExpr.getChild(1).type).isEqualTo("KtLiteralStringTemplateEntry")
                assertThat(stringTempExpr.getChild(1).label).isEqualTo(" ")

                blockTempString = stringTempExpr.getChild(2)
                assertThat(blockTempString.type).isEqualTo("KtBlockStringTemplateEntry")
                assertThat(blockTempString.label).isEqualTo("")

                dotQualExpr = blockTempString.getFirstChild()
                assertThat(dotQualExpr.type).isEqualTo("KtDotQualifiedExpression")
                assertThat(dotQualExpr.label).isEqualTo("")

                assertThat(dotQualExpr.children).hasSize(2)

                assertThat(dotQualExpr.getFirstChild().type).isEqualTo("KtDotQualifiedExpression")
                assertThat(dotQualExpr.getFirstChild().label).isEqualTo("")

                assertThat(dotQualExpr.getFirstChild().getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(dotQualExpr.getFirstChild().getFirstChild().label).isEqualTo("names")

                var callExpr = dotQualExpr.getFirstChild().getChild(1)
                assertThat(callExpr.type).isEqualTo("KtCallExpression")
                assertThat(callExpr.label).isEqualTo("")

                assertThat(callExpr.children).hasSize(2)

                assertThat(callExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(callExpr.getFirstChild().label).isEqualTo("filterNotNull")

                assertThat(callExpr.getChild(1).type).isEqualTo("KtValueArgumentList")
                assertThat(callExpr.getChild(1).label).isEqualTo("")


                callExpr = dotQualExpr.getChild(1)
                assertThat(callExpr.type).isEqualTo("KtCallExpression")
                assertThat(callExpr.label).isEqualTo("")

                assertThat(callExpr.children).hasSize(2)

                assertThat(callExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(callExpr.getFirstChild().label).isEqualTo("joinToString")

                argumentList = callExpr.getChild(1)
                assertThat(argumentList.type).isEqualTo("KtValueArgumentList")
                assertThat(argumentList.label).isEqualTo("")

                assertThat(argumentList.children).hasSize(1)

                val argument = argumentList.getFirstChild()
                assertThat(argument.type).isEqualTo("KtValueArgument")
                assertThat(argument.label).isEqualTo("")

                assertThat(argument.getFirstChild().type).isEqualTo("KtValueArgumentName")
                assertThat(argument.getFirstChild().label).isEqualTo("separator")

                assertThat(argument.getChild(1).type).isEqualTo("KtStringTemplateExpression")
                assertThat(argument.getChild(1).label).isEqualTo("")

                assertThat(argument.getChild(1).getFirstChild().type).isEqualTo("KtLiteralStringTemplateEntry")
                assertThat(argument.getChild(1).getFirstChild().label).isEqualTo(" and ")









            }
        }

    }

})

