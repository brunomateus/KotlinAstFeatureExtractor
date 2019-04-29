package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import fr.uphf.analyze.getASTasStringJson
import fr.uphf.analyze.printAST
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object AnnotationSpek : Spek({
    Feature("Kotlin Annotations") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var cls: ASTNode

        Scenario("Defining a multitarget annotation") {

            Given("A class that defines a multitarget annotation"){
                code = """
 @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
        AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented

annotation class Fancy
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val clsName = "Fancy"
            lateinit var modifierList: ASTNode
            Then("It should define an annotation class named $clsName"){
                val annotationClass = rootNode.getChild(2)

                assertThat(annotationClass.type).isEqualTo("KtClass")
                assertThat(annotationClass.label).isEqualTo(clsName)

                modifierList = annotationClass.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.children).hasSize(4)

                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("ModifierEntry")
                assertThat(modifier.label).isEqualTo("annotation")
            }


            val arguments = listOf("AnnotationTarget.CLASS", "AnnotationTarget.FUNCTION",
                "AnnotationTarget.VALUE_PARAMETER", "AnnotationTarget.EXPRESSION")

            And("It should hav as target these elements: $arguments"){
                val target = modifierList.getChild(1)
                assertThat(target.type).isEqualTo("KtAnnotationEntry")
                assertThat(target.label).isEqualTo("")

                assertThat(target.children).hasSize(2)

                assertThat(target.getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(target.getFirstChild().label).isEqualTo("Target")

                val argumentList = target.getChild(1)
                assertThat(argumentList.type).isEqualTo("KtValueArgumentList")
                assertThat(argumentList.label).isEqualTo("")

                assertThat(argumentList.children).hasSize(4)


                for(i in 0..3){
                    val arg = argumentList.getChild(i)
                    assertThat(arg.type).isEqualTo("KtValueArgument")
                    assertThat(arg.label).isEqualTo("")

                    val dotExpr = arg.getFirstChild()
                    assertThat(dotExpr.type).isEqualTo("KtDotQualifiedExpression")
                    assertThat(dotExpr.label).isEqualTo("")

                    val expr = arguments[i].split(".")
                    assertThat(dotExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                    assertThat(dotExpr.getFirstChild().label).isEqualTo(expr[0])

                    assertThat(dotExpr.getChild(1).type).isEqualTo("KtNameReferenceExpression")
                    assertThat(dotExpr.getChild(1).label).isEqualTo(expr[1])
                }
            }

            And("it should have a @Retention annontation with AnnotationRetention.SOURCE as argument"){
                val retention = modifierList.getChild(2)
                assertThat(retention.type).isEqualTo("KtAnnotationEntry")
                assertThat(retention.label).isEqualTo("")

                assertThat(retention.children).hasSize(2)

                assertThat(retention.getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(retention.getFirstChild().label).isEqualTo("Retention")

                val argumentList2 = retention.getChild(1)
                assertThat(argumentList2.type).isEqualTo("KtValueArgumentList")
                assertThat(argumentList2.label).isEqualTo("")

                assertThat(argumentList2.children).hasSize(1)

                assertThat(argumentList2.getFirstChild().type).isEqualTo("KtValueArgument")
                assertThat(argumentList2.getFirstChild().label).isEqualTo("")

                val dotExpr = argumentList2.getFirstChild().getFirstChild()
                assertThat(dotExpr.type).isEqualTo("KtDotQualifiedExpression")
                assertThat(dotExpr.label).isEqualTo("")

                assertThat(dotExpr.getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(dotExpr.getFirstChild().label).isEqualTo("AnnotationRetention")

                assertThat(dotExpr.getChild(1).type).isEqualTo("KtNameReferenceExpression")
                assertThat(dotExpr.getChild(1).label).isEqualTo("SOURCE")
            }

            And("it should have a @MustBeDocumented annontation without any argument"){
                val documented = modifierList.getChild(3)
                assertThat(documented.type).isEqualTo("KtAnnotationEntry")
                assertThat(documented.label).isEqualTo("")

                assertThat(documented.children).hasSize(1)

                assertThat(documented.getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(documented.getFirstChild().label).isEqualTo("MustBeDocumented")
            }

        }

        Scenario("Annotated file that uses other annotation") {

            Given("A annotated file that has a class with annotation that specify the target"){
                code = """
@file:JvmName("Foo")

	class Example(@field:Ann val foo,    // annotate Java field
              @get:Ann val bar,      // annotate Java getter
              @param:Ann val quux){}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("An annotation @File:JvmName should be retrieved"){
                val annotationList = rootNode.getFirstChild()
                assertThat(annotationList.type).isEqualTo("KtFileAnnotationList")
                assertThat(annotationList.label).isEqualTo("")

                val fileAnnotation = annotationList.getFirstChild()
                assertThat(fileAnnotation.type).isEqualTo("KtAnnotationEntry")
                assertThat(fileAnnotation.label).isEqualTo("")

                assertThat(fileAnnotation.getFirstChild().type).isEqualTo("KtAnnotationUseSiteTarget")
                assertThat(fileAnnotation.getFirstChild().label).isEqualTo("file")

                assertThat(fileAnnotation.getChild(1).type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(fileAnnotation.getChild(1).label).isEqualTo("JvmName")
            }

            val clsName = "Example"
            Then("It should define a class named $clsName"){
                cls = rootNode.getChild(3)

                assertThat(cls.type).isEqualTo("KtClass")
                assertThat(cls.label).isEqualTo(clsName)
            }

            lateinit var constructorParameterList: ASTNode
            And("this class should have a constructor with 4 parameters"){
                constructorParameterList = cls.getFirstChild().getFirstChild()

                assertThat(constructorParameterList.type).isEqualTo("KtParameterList")
                assertThat(constructorParameterList.label).isEqualTo("")

                assertThat(constructorParameterList.children).hasSize(3)
            }

            Then("The first parameter name foo should be read-only and annotated with @field:Ann"){
                val p1 = constructorParameterList.getFirstChild()
                assertThat(p1.type).isEqualTo("KtParameter")
                assertThat(p1.label).isEqualTo("foo") //val

                val keywordvv = p1.getFirstChild()

                val modifierList = p1.getChild(1)
                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("KtAnnotationEntry")
                assertThat(modifier.label).isEqualTo("")

                assertThat(modifier.getFirstChild().type).isEqualTo("KtAnnotationUseSiteTarget")
                assertThat(modifier.getFirstChild().label).isEqualTo("field")

                assertThat(modifier.getChild(1).type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(modifier.getChild(1).label).isEqualTo("Ann")
            }

            And("The first parameter name bar should be read-only and annotated with @get:Ann"){
                val p2 = constructorParameterList.getChild(1)
                assertThat(p2.type).isEqualTo("KtParameter")
                assertThat(p2.label).isEqualTo("bar")

                val modifierList = p2.getChild(1)
                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("KtAnnotationEntry")
                assertThat(modifier.label).isEqualTo("")

                assertThat(modifier.getFirstChild().type).isEqualTo("KtAnnotationUseSiteTarget")
                assertThat(modifier.getFirstChild().label).isEqualTo("get")

                assertThat(modifier.getChild(1).type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(modifier.getChild(1).label).isEqualTo("Ann")
            }

            And("The first parameter name quux should be read-only and annotated with @param:Ann"){
                val p3 = constructorParameterList.getChild(2)
                assertThat(p3.type).isEqualTo("KtParameter")
                assertThat(p3.label).isEqualTo("quux")

                val modifierList = p3.getChild(1)
                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("KtAnnotationEntry")
                assertThat(modifier.label).isEqualTo("")

                assertThat(modifier.getFirstChild().type).isEqualTo("KtAnnotationUseSiteTarget")
                assertThat(modifier.getFirstChild().label).isEqualTo("param")

                assertThat(modifier.getChild(1).type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(modifier.getChild(1).label).isEqualTo("Ann")
            }



        }

        Scenario("Multiple annotation applied on the same target") {

            Given("A class that has multiple annotation applied to the same target"){
                code = """

	class Example {
     @set:[Inject VisibleForTesting]
     var collaborator: Collaborator
	}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }


            val clsName = "Example"
            Then("It should define a class named $clsName"){
                cls = rootNode.getChild(2)

                assertThat(cls.type).isEqualTo("KtClass")
                assertThat(cls.label).isEqualTo(clsName)
            }

            lateinit var prop: ASTNode
            Then("It should have a read-property named collaborator"){
                val clsBody = cls.getFirstChild()

                assertThat(clsBody.type).isEqualTo("KtClassBody")
                assertThat(clsBody.label).isEqualTo("")

                prop = clsBody.getFirstChild()

                assertThat(prop.type).isEqualTo("KtProperty")
                assertThat(prop.label).isEqualTo("var collaborator")
            }

            And("This property should be annotated by two annotation, Inject and VisibleForTesting, with set as target"){
                val modifierList = prop.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.children).hasSize(1)

                val annotation = modifierList.getFirstChild()
                assertThat(annotation.type).isEqualTo("KtAnnotation")
                assertThat(annotation.label).isEqualTo("")

                assertThat(annotation.children).hasSize(3)

                assertThat(annotation.getFirstChild().type).isEqualTo("KtAnnotationUseSiteTarget")
                assertThat(annotation.getFirstChild().label).isEqualTo("set")

                assertThat(annotation.getChild(1).type).isEqualTo("KtAnnotationEntry")
                assertThat(annotation.getChild(1).label).isEqualTo("")

                assertThat(annotation.getChild(1).getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(annotation.getChild(1).getFirstChild().label).isEqualTo("Inject")

                assertThat(annotation.getChild(2).type).isEqualTo("KtAnnotationEntry")
                assertThat(annotation.getChild(2).label).isEqualTo("")

                assertThat(annotation.getChild(2).getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(annotation.getChild(2).getFirstChild().label).isEqualTo("VisibleForTesting")



            }

        }

        Scenario("One annotation applied in different elements") {

            Given("One annotation applied in different element of one class"){
                code = """

@Fancy public class Foo {
    @Fancy fun baz(@Fancy foo: Int): Int {
        return (@Fancy 1)
    }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }


            val clsName = "Foo"
            Then("It should define a public class named $clsName, whose has annotation @Fancy"){
                cls = rootNode.getChild(2)

                assertThat(cls.type).isEqualTo("KtClass")
                assertThat(cls.label).isEqualTo(clsName)

                val modifierList = cls.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.children)
                    .hasSize(2)

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("public")

                assertThat(modifierList.getChild(1).type).isEqualTo("KtAnnotationEntry")
                assertThat(modifierList.getChild(1).label).isEqualTo("")

                assertThat(modifierList.getChild(1).getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(modifierList.getChild(1).getFirstChild().label).isEqualTo("Fancy")
            }

            lateinit var func: ASTNode
            lateinit var parameterList: ASTNode
            Then("it should have method named baz that has one parameter, returns a Int and " +
                    "it has annotation @Fancy"){
                val body = cls.getChild(1)
                assertThat(body.type).isEqualTo("KtClassBody")
                assertThat(body.label).isEqualTo("")

                func = body.getFirstChild()
                assertThat(func.type).isEqualTo("KtNamedFunction")
                assertThat(func.label).isEqualTo("baz")

                val modifierList = func.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.children).hasSize(1)

                assertThat(modifierList.getFirstChild().type).isEqualTo("KtAnnotationEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("")

                assertThat(modifierList.getFirstChild().getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(modifierList.getFirstChild().getFirstChild().label).isEqualTo("Fancy")

                parameterList = func.getChild(1)
                assertThat(parameterList.children).hasSize(1)

                val returnType = func.getChild(2)
                assertThat(returnType.type).isEqualTo("KtTypeReference")
                assertThat(returnType.label).isEqualTo("")

                assertThat(returnType.getFirstChild().getFirstChild().type).isEqualTo("KtNameReferenceExpression")
                assertThat(returnType.getFirstChild().getFirstChild().label).isEqualTo("Int")
            }

            And("this method should have a parameter named foo annotated with @Fancy"){
                val parameter = parameterList.getFirstChild()
                assertThat(parameter.type).isEqualTo("KtParameter")
                assertThat(parameter.label).isEqualTo("foo")

                val modifierList = parameter.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.children).hasSize(1)

                assertThat(modifierList.getFirstChild().type).isEqualTo("KtAnnotationEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("")

                assertThat(modifierList.getFirstChild().getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(modifierList.getFirstChild().getFirstChild().label).isEqualTo("Fancy")
            }

            And("it should return an expression annotated with @Fancy"){
                val funcBody = func.getChild(3)
                val returnExp = funcBody.getFirstChild()
                assertThat(returnExp.type).isEqualTo("KtReturnExpression")
                assertThat(returnExp.label).isEqualTo("return")

                val parenExp = returnExp.getFirstChild()
                assertThat(parenExp.type).isEqualTo("KtParenthesizedExpression")
                assertThat(parenExp.label).isEqualTo("(@Fancy 1)")

                assertThat(parenExp.getFirstChild().type).isEqualTo("KtAnnotatedExpression")
                assertThat(parenExp.getFirstChild().label).isEqualTo("")

                assertThat(parenExp.getFirstChild().getFirstChild().type).isEqualTo("KtAnnotationEntry")
                assertThat(parenExp.getFirstChild().getFirstChild().label).isEqualTo("")

                assertThat(parenExp.getFirstChild().getFirstChild().getFirstChild().type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(parenExp.getFirstChild().getFirstChild().getFirstChild().label).isEqualTo("Fancy")
            }

        }


    }

})

