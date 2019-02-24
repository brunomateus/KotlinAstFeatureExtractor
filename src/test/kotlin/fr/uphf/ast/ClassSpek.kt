package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import fr.uphf.analyze.printAST
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ClassSpek : Spek({
    Feature("Kotlin class") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ktclass: ASTNode

        Scenario("Class with constructor") {

            Given("A class with a primary constructor"){
                code = """
class Greeter(val name: String) {
			fun greet() {
        		println("Hello, name");
    		}

    		public open fun pgreet() {
        		println("Hello, name");
    		}
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            Then("it should contain one class named Greeter") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo("Greeter")
            }

            And("it should have two children") {
                assertThat(ktclass.children).hasSize(2)
            }

            And("the first should be a KtPrimaryConstructor") {
                assertThat(ktclass.getFirstChild().type).isEqualTo("KtPrimaryConstructor")
                assertThat(ktclass.getFirstChild().label).isEqualTo("")
            }

            And("the last children should be the class body"){
                assertThat(ktclass.getChild(1).type).isEqualTo("KtClassBody")
                assertThat(ktclass.getChild(1).label).isEqualTo("")
            }
        }

        Scenario("Class that uses initializers") {

            Given("A class with a primary constructor and two init blocks"){
                code = """
class InitOrderDemo(name: String) {
    val firstProperty = "First property: ${'$'}name".also(::println)

    init {
        println("First initializer block that prints ${'$'}{name}")
    }

    val secondProperty = "Second property: ${'$'}{name.length}".also(::println)

    init {
        println("Second initializer block that prints ${'$'}{name.length}")
    }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val className = "InitOrderDemo"
            Then("it should contain one class named $className") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(className)
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            And("the first should be a KtPrimaryConstructor") {
                assertThat(ktclass.getFirstChild().type).isEqualTo("KtPrimaryConstructor")
                assertThat(ktclass.getFirstChild().label).isEqualTo("")
            }

            lateinit var classBody: ASTNode
            And("the last children should be the class body"){
                classBody = ktclass.getChild(1)
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")
            }

            And("The classBody should have 4 children"){
                assertThat(classBody.children).hasSize(4)
            }

            And("Its should contains one property read-only named firstProperty"){
                assertThat(classBody.getFirstChild().type).isEqualTo("KtProperty")
                assertThat(classBody.getFirstChild().label).isEqualTo("val firstProperty")
            }

            And("Its should contains other property read-only named secondProperty"){
                assertThat(classBody.getChild(2).type).isEqualTo("KtProperty")
                assertThat(classBody.getChild(2).label).isEqualTo("val secondProperty")
            }

            And("Its should contains two initializers block"){
                assertThat(classBody.getChild(1).type).isEqualTo("KtClassInitializer")
                assertThat(classBody.getChild(1).label).isEqualTo("")

                assertThat(classBody.getChild(3).type).isEqualTo("KtClassInitializer")
                assertThat(classBody.getChild(3).label).isEqualTo("")
            }

        }

        Scenario("Class with  more than one constructor") {

            Given("A class with a secondary constructor"){
                code = """
class Person(val name: String) {
		constructor(name: String, parent: Person) : this(name) {
			parent.children.add(this)
		}
	}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val className = "Person"
            Then("it should contain one class named $className") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(className)
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            And("the first should be a KtPrimaryConstructor") {
                assertThat(ktclass.getFirstChild().type).isEqualTo("KtPrimaryConstructor")
                assertThat(ktclass.getFirstChild().label).isEqualTo("")
            }

            lateinit var classBody: ASTNode
            And("the last children should be the class body"){
                classBody = ktclass.getChild(1)
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")
            }

            And( "its should contains the secondary constructor"){
                assertThat(classBody.getFirstChild().type).isEqualTo("KtSecondaryConstructor")
                assertThat(classBody.getFirstChild().label).isEqualTo("")
            }
        }

        Scenario("Class that define a property with custom property accessors"){

            Given("A class that define a property with custom property accessors"){
                code = """
class Bar {
	var stringRepresentation: String
		get() = this.toString()
    	set(value) {
    	    setDataFromString(value) // parses the string and assigns values to other properties
   	 }
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }

            val clsName = "Bar"
            Then("it should contain one class named $clsName") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)
            }

            val nChildren = 1
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            lateinit var property: ASTNode
            Then("A property named stringRepresentation, should be defined inside the class body"){
                property = ktclass.getFirstChild().getFirstChild()

                assertThat(property.type).isEqualTo("KtProperty")
                assertThat(property.label).isEqualTo("var stringRepresentation")
            }

            And("this property should define property accessors"){
                assertThat(property.getChild(1).type).isEqualTo("KtPropertyAccessor")
                assertThat(property.getChild(1).label).isEqualTo("get")

                assertThat(property.getChild(2).type).isEqualTo("KtPropertyAccessor")
                assertThat(property.getChild(2).label).isEqualTo("set")
            }
        }

    }

    Feature("Kotlin inheritance"){

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ktclass1: ASTNode
        lateinit var ktclass2: ASTNode

        Scenario("A class extends another class") {

            Given("A class extends another class that define a primary constructor"){
                code = """
open class Base(p: Int)

class Derived(p: Int) : Base(p)
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val cls1Name = "Base"
            val cls2Name = "Derived"
            Then("it should contain tow classes named $cls1Name and $cls2Name") {
                ktclass1 = rootNode.getChild(2)
                ktclass2 = rootNode.getChild(3)

                assertThat(ktclass1.type).isEqualTo("KtClass")
                assertThat(ktclass1.label).isEqualTo("Base")

                assertThat(ktclass2.type).isEqualTo("KtClass")
                assertThat(ktclass2.label).isEqualTo("Derived")
            }

            Then("The base class, $cls1Name, should be open"){
                val clsModifierList = ktclass1.getFirstChild()
                assertThat(clsModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(clsModifierList.label).isEqualTo("")

                assertThat(clsModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(clsModifierList.getFirstChild().label).isEqualTo("open")
            }

            Then("The $cls2Name should define a primary constructor"){
                assertThat(ktclass2.getFirstChild().type).isEqualTo("KtPrimaryConstructor")
                assertThat(ktclass2.getFirstChild().label).isEqualTo("")
            }

           And("it should call the constructor of the class $cls1Name "){
               val superList = ktclass2.getChild(1)
               assertThat(superList.type).isEqualTo("KtSuperTypeList")
               assertThat(superList.label).isEqualTo("")

               val superTypeEntry = superList.getFirstChild()
               assertThat(superTypeEntry.type).isEqualTo("KtSuperTypeCallEntry")
               assertThat(superTypeEntry.label).isEqualTo("")

               val consCallExpr = superTypeEntry.getFirstChild()
               assertThat(consCallExpr.type).isEqualTo("KtConstructorCalleeExpression")
               assertThat(consCallExpr.label).isEqualTo("")

               assertThat(consCallExpr.getFirstChild().type).isEqualTo("KtTypeReference")
               assertThat(consCallExpr.getFirstChild().label).isEqualTo("Base")
           }
        }

        Scenario("A class extends another class that does not has a primary constructor") {

            Given("A class extends another class that does not has a primary constructor"){
                code = """
class MyView : View {
    	constructor(ctx: Context) : super(ctx)
    	constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }

            val cls1Name = "MyView"
            val cls2Name = "View"
            Then("it should contain one class named $cls1Name") {
                ktclass1 = rootNode.getChild(2)

                assertThat(ktclass1.type).isEqualTo("KtClass")
                assertThat(ktclass1.label).isEqualTo("MyView")
            }

           And("it should has only the type reference of the base class $cls2Name"){
               val superList = ktclass1.getFirstChild()
               assertThat(superList.type).isEqualTo("KtSuperTypeList")
               assertThat(superList.label).isEqualTo("")

               val superTypeEntry = superList.getFirstChild()
               assertThat(superTypeEntry.type).isEqualTo("KtSuperTypeEntry")
               assertThat(superTypeEntry.label).isEqualTo("")

               assertThat(superTypeEntry.getFirstChild().type).isEqualTo("KtTypeReference")
               assertThat(superTypeEntry.getFirstChild().label).isEqualTo("View")
           }
        }

        Scenario("A class extends another class and overriding properties") {

            Given("A class extends another class and override one property"){
                code = """
open class Base(val name: String) {

	open val size: Int = name.length
}

class Derived(
    name: String,
    val lastName: String
) : Base(name.capitalize()) {

    override val size: Int = (super.size + lastName.length)
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val cls1Name = "Base"
            val cls2Name = "Derived"
            Then("it should contain tow classes named $cls1Name and $cls2Name") {
                ktclass1 = rootNode.getChild(2)
                ktclass2 = rootNode.getChild(3)

                assertThat(ktclass1.type).isEqualTo("KtClass")
                assertThat(ktclass1.label).isEqualTo("Base")

                assertThat(ktclass2.type).isEqualTo("KtClass")
                assertThat(ktclass2.label).isEqualTo("Derived")
            }

            Then("The base class, $cls1Name, should be open"){
                val clsModifierList = ktclass1.getFirstChild()
                assertThat(clsModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(clsModifierList.label).isEqualTo("")

                assertThat(clsModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(clsModifierList.getFirstChild().label).isEqualTo("open")
            }

            And("The base class, $cls1Name, defines a open property"){
                val classBody = ktclass1.getChild(2)
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")

                val prop = classBody.getFirstChild()
                assertThat(prop.type).isEqualTo("KtProperty")
                assertThat(prop.label).isEqualTo("val size")

                val modifierList = prop.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("ModifierEntry")
                assertThat(modifier.label).isEqualTo("open")
            }

            Then("The $cls2Name should define a primary constructor"){
                assertThat(ktclass2.getFirstChild().type).isEqualTo("KtPrimaryConstructor")
                assertThat(ktclass2.getFirstChild().label).isEqualTo("")
            }

            And("it should call the constructor of the class $cls1Name "){
                val superList = ktclass2.getChild(1)
                assertThat(superList.type).isEqualTo("KtSuperTypeList")
                assertThat(superList.label).isEqualTo("")

                val superTypeEntry = superList.getFirstChild()
                assertThat(superTypeEntry.type).isEqualTo("KtSuperTypeCallEntry")
                assertThat(superTypeEntry.label).isEqualTo("")

                val consCallExpr = superTypeEntry.getFirstChild()
                assertThat(consCallExpr.type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(consCallExpr.label).isEqualTo("")

                assertThat(consCallExpr.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(consCallExpr.getFirstChild().label).isEqualTo("Base")
            }

            And("The $cls2Name should override the property defined in $cls1Name"){
                val class2Body = ktclass2.getChild(2)
                assertThat(class2Body.type).isEqualTo("KtClassBody")
                assertThat(class2Body.label).isEqualTo("")

                assertThat(class2Body.getFirstChild().type).isEqualTo("KtProperty")
                assertThat(class2Body.getFirstChild().label).isEqualTo("val size")

                val funcModifierList = class2Body.getFirstChild()
                    .getFirstChild()
                assertThat(funcModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(funcModifierList.label).isEqualTo("")

                assertThat(funcModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(funcModifierList.getFirstChild().label).isEqualTo("override")
            }
        }

        Scenario("A class extends another class and overriding method") {

            Given("A class extends another class and override one method"){
                code = """
open class Teacher {
    // Must use "open" modifier to allow child classes to override it
    open fun teach() {
        println("Teaching...")
    }
}

class MathsTeacher : Teacher() {
    // Must use "override" modifier to override a base class function
    override fun teach() {
        println("Teaching Maths...")
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val cls1Name = "Teacher"
            val cls2Name = "MathsTeacher"
            Then("it should contain tow classes named $cls1Name and $cls2Name") {
                ktclass1 = rootNode.getChild(2)
                ktclass2 = rootNode.getChild(3)

                assertThat(ktclass1.type).isEqualTo("KtClass")
                assertThat(ktclass1.label).isEqualTo(cls1Name)

                assertThat(ktclass2.type).isEqualTo("KtClass")
                assertThat(ktclass2.label).isEqualTo(cls2Name)
            }

            Then("The base class, $cls1Name, should be open"){
                val clsModifierList = ktclass1.getFirstChild()
                assertThat(clsModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(clsModifierList.label).isEqualTo("")

                assertThat(clsModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(clsModifierList.getFirstChild().label).isEqualTo("open")
            }

            And("The base class, $cls1Name, defines a open method"){
                val classBody = ktclass1.getChild(1)
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")

                val method = classBody.getFirstChild()
                assertThat(method.type).isEqualTo("KtNamedFunction")
                assertThat(method.label).isEqualTo("teach")

                val modifierList = method.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("ModifierEntry")
                assertThat(modifier.label).isEqualTo("open")
            }

            Then("The $cls2Name should call the constructor of the class $cls1Name "){
                val superList = ktclass2.getFirstChild()
                assertThat(superList.type).isEqualTo("KtSuperTypeList")
                assertThat(superList.label).isEqualTo("")

                val superTypeEntry = superList.getFirstChild()
                assertThat(superTypeEntry.type).isEqualTo("KtSuperTypeCallEntry")
                assertThat(superTypeEntry.label).isEqualTo("")

                val consCallExpr = superTypeEntry.getFirstChild()
                assertThat(consCallExpr.type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(consCallExpr.label).isEqualTo("")

                assertThat(consCallExpr.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(consCallExpr.getFirstChild().label).isEqualTo("Teacher")
            }

            And("The $cls2Name should override the method defined in $cls1Name"){
                val class2Body = ktclass2.getChild(1)
                assertThat(class2Body.type).isEqualTo("KtClassBody")
                assertThat(class2Body.label).isEqualTo("")

                assertThat(class2Body.getFirstChild().type).isEqualTo("KtNamedFunction")
                assertThat(class2Body.getFirstChild().label).isEqualTo("teach")

                val funcModifierList = class2Body.getFirstChild()
                    .getFirstChild()
                assertThat(funcModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(funcModifierList.label).isEqualTo("")

                assertThat(funcModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(funcModifierList.getFirstChild().label).isEqualTo("override")
            }
        }

        Scenario("An abstract class extends a concrete class") {

            Given("A abstract class that extends a concrete class and override a method"){
                code = """
open class Base {
		open fun f() {}
}

abstract class Derived : Base() {
		override abstract fun f()
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val cls1Name = "Base"
            val cls2Name = "Derived"
            Then("it should contain tow classes named $cls1Name and $cls2Name") {
                ktclass1 = rootNode.getChild(2)
                ktclass2 = rootNode.getChild(3)

                assertThat(ktclass1.type).isEqualTo("KtClass")
                assertThat(ktclass1.label).isEqualTo(cls1Name)

                assertThat(ktclass2.type).isEqualTo("KtClass")
                assertThat(ktclass2.label).isEqualTo(cls2Name)
            }

            Then("The base class, $cls1Name, should be open"){
                val clsModifierList = ktclass1.getFirstChild()
                assertThat(clsModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(clsModifierList.label).isEqualTo("")

                assertThat(clsModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(clsModifierList.getFirstChild().label).isEqualTo("open")
            }

            And("The base class, $cls1Name, defines a open method"){
                val classBody = ktclass1.getChild(1)
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")

                val method = classBody.getFirstChild()
                assertThat(method.type).isEqualTo("KtNamedFunction")
                assertThat(method.label).isEqualTo("f")

                val modifierList = method.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("ModifierEntry")
                assertThat(modifier.label).isEqualTo("open")
            }

            Then("The $cls2Name should have the modifier abstract"){
                val modifierList = ktclass2.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("abstract")
            }

            And("it should call the constructor of the class $cls1Name "){
                val superList = ktclass2.getChild(1)
                assertThat(superList.type).isEqualTo("KtSuperTypeList")
                assertThat(superList.label).isEqualTo("")

                val superTypeEntry = superList.getFirstChild()
                assertThat(superTypeEntry.type).isEqualTo("KtSuperTypeCallEntry")
                assertThat(superTypeEntry.label).isEqualTo("")

                val consCallExpr = superTypeEntry.getFirstChild()
                assertThat(consCallExpr.type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(consCallExpr.label).isEqualTo("")

                assertThat(consCallExpr.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(consCallExpr.getFirstChild().label).isEqualTo(cls1Name)
            }

            And("The $cls2Name should override the method defined in $cls1Name making it abstract"){
                val class2Body = ktclass2.getChild(2)
                assertThat(class2Body.type).isEqualTo("KtClassBody")
                assertThat(class2Body.label).isEqualTo("")

                assertThat(class2Body.getFirstChild().type).isEqualTo("KtNamedFunction")
                assertThat(class2Body.getFirstChild().label).isEqualTo("f")

                val funcModifierList = class2Body.getFirstChild()
                    .getFirstChild()
                assertThat(funcModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(funcModifierList.label).isEqualTo("")

                assertThat(funcModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(funcModifierList.getFirstChild().label).isEqualTo("override")

                assertThat(funcModifierList.getChild(1).type).isEqualTo("ModifierEntry")
                assertThat(funcModifierList.getChild(1).label).isEqualTo("abstract")
            }
        }

        Scenario("Calling the superclass implementation") {

            Given("A class that call its superclass insinde a method and inside a property acessor"){
                code = """
open class Foo {
    open fun f() { println("Foo.f()") }
    open val x: Int get() = 1
}

class Bar : Foo() {
    override fun f() {
        super.f()
        println("Bar.f()")
    }
    override val x: Int get() = super.x + 1
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val cls1Name = "Foo"
            val cls2Name = "Bar"
            Then("it should contain tow classes named $cls1Name and $cls2Name") {
                ktclass1 = rootNode.getChild(2)
                ktclass2 = rootNode.getChild(3)

                assertThat(ktclass1.type).isEqualTo("KtClass")
                assertThat(ktclass1.label).isEqualTo(cls1Name)

                assertThat(ktclass2.type).isEqualTo("KtClass")
                assertThat(ktclass2.label).isEqualTo(cls2Name)
            }

            Then("The base class, $cls1Name, should be open"){
                val clsModifierList = ktclass1.getFirstChild()
                assertThat(clsModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(clsModifierList.label).isEqualTo("")

                assertThat(clsModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(clsModifierList.getFirstChild().label).isEqualTo("open")
            }

            lateinit var classBody: ASTNode
            And("The base class, $cls1Name, defines a open method"){
                classBody = ktclass1.getChild(1)
                assertThat(classBody.type).isEqualTo("KtClassBody")
                assertThat(classBody.label).isEqualTo("")

                val method = classBody.getFirstChild()
                assertThat(method.type).isEqualTo("KtNamedFunction")
                assertThat(method.label).isEqualTo("f")

                val modifierList = method.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("ModifierEntry")
                assertThat(modifier.label).isEqualTo("open")
            }

            And("The base class, $cls1Name, defines a open property"){
                val method = classBody.getChild(1)
                assertThat(method.type).isEqualTo("KtProperty")
                assertThat(method.label).isEqualTo("val x")

                val modifierList = method.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                val modifier = modifierList.getFirstChild()
                assertThat(modifier.type).isEqualTo("ModifierEntry")
                assertThat(modifier.label).isEqualTo("open")
            }


            Then("it should call the constructor of the class $cls1Name "){
                val superList = ktclass2.getFirstChild()
                assertThat(superList.type).isEqualTo("KtSuperTypeList")
                assertThat(superList.label).isEqualTo("")

                val superTypeEntry = superList.getFirstChild()
                assertThat(superTypeEntry.type).isEqualTo("KtSuperTypeCallEntry")
                assertThat(superTypeEntry.label).isEqualTo("")

                val consCallExpr = superTypeEntry.getFirstChild()
                assertThat(consCallExpr.type).isEqualTo("KtConstructorCalleeExpression")
                assertThat(consCallExpr.label).isEqualTo("")

                assertThat(consCallExpr.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(consCallExpr.getFirstChild().label).isEqualTo(cls1Name)
            }

            lateinit var class2Body: ASTNode
            And("The $cls2Name should override the method defined in $cls1Name"){
                class2Body = ktclass2.getChild(1)
                assertThat(class2Body.type).isEqualTo("KtClassBody")
                assertThat(class2Body.label).isEqualTo("")

                assertThat(class2Body.getFirstChild().type).isEqualTo("KtNamedFunction")
                assertThat(class2Body.getFirstChild().label).isEqualTo("f")

                val funcModifierList = class2Body.getFirstChild().getFirstChild()

                assertThat(funcModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(funcModifierList.label).isEqualTo("")

                assertThat(funcModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(funcModifierList.getFirstChild().label).isEqualTo("override")

            }

            And("The new method implementation should call the superclass implementation"){
                val superExpression = class2Body.getFirstChild().getChild(2).getFirstChild().getFirstChild()
                assertThat(superExpression.type).isEqualTo("KtSuperExpression")
                assertThat(superExpression.label).isEqualTo("super")
            }

            And("The $cls2Name should override the property defined in $cls1Name"){
                assertThat(class2Body.type).isEqualTo("KtClassBody")
                assertThat(class2Body.label).isEqualTo("")

                assertThat(class2Body.getChild(1).type).isEqualTo("KtProperty")
                assertThat(class2Body.getChild(1).label).isEqualTo("val x")

                val funcModifierList = class2Body.getChild(1).getFirstChild()

                assertThat(funcModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(funcModifierList.label).isEqualTo("")

                assertThat(funcModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(funcModifierList.getFirstChild().label).isEqualTo("override")

            }

            And("The new get acessor implementation should call the superclass implementation"){
                val propertyAccessor = class2Body.getChild(1).getChild(2)

                assertThat(propertyAccessor.type).isEqualTo("KtPropertyAccessor")
                assertThat(propertyAccessor.label).isEqualTo("get")

                val getSuperExpression = propertyAccessor.getFirstChild().getFirstChild().getFirstChild()
                assertThat(getSuperExpression.type).isEqualTo("KtSuperExpression")
                assertThat(getSuperExpression.label).isEqualTo("super")
            }
        }
    }

    Feature("Kotlin delegation"){
        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ktclass: ASTNode

        Scenario("A class with delegated properties"){

            Given("A class that delegates one property"){
                code = """
class Example {
    var p: String by Delegate()
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val clsName = "Example"
            Then("it should contain one class named $clsName") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)
            }

            val nChildren = 1
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            lateinit var property: ASTNode
            Then("A property named p, should be defined inside the class body"){
                property = ktclass.getFirstChild().getFirstChild()

                assertThat(property.type).isEqualTo("KtProperty")
                assertThat(property.label).isEqualTo("var p")
            }

            And("this property is delegated"){
                assertThat(property.getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(property.getFirstChild().label).isEqualTo("String")

                val delegation = property.getChild(1)
                assertThat(delegation.type).isEqualTo("KtPropertyDelegate")
                assertThat(delegation.label).isEqualTo("")

                assertThat(delegation.getFirstChild().type).isEqualTo("KtCallExpression")
                assertThat(delegation.getFirstChild().label).isEqualTo("Delegate")

            }
        }

        Scenario("A class with delegated properties"){

            Given("A class uses the delegation pattern"){
                code = """
interface Base {
    fun print()
}

class BaseImpl(val x: Int) : Base {
    override fun print() { print(x) }
}

class Derived(b: Base) : Base by b
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                printAST(rootNode)
            }

            val clsName = "Derived"
            Then("it should contain one class named $clsName") {
                ktclass = rootNode.getChild(4)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            And("delegates some implementation to other class"){
                val superTypeEntry = ktclass.getChild(1)

                assertThat(superTypeEntry.getFirstChild().type).isEqualTo("KtDelegatedSuperTypeEntry")
                assertThat(superTypeEntry.getFirstChild().label).isEqualTo("")

                assertThat(superTypeEntry.getFirstChild().getFirstChild().type).isEqualTo("KtTypeReference")
                assertThat(superTypeEntry.getFirstChild().getFirstChild().label).isEqualTo("Base")
            }


        }
    }

    Feature("Enum class"){
        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var ktclass: ASTNode

        Scenario("A enum that has only constants"){

            Given("A enum class that defines only constants"){
                code = """
enum class Direction {
    NORTH, SOUTH, WEST, EAST
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val clsName = "Direction"
            Then("it should contain one class named $clsName and the modifier enum") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)

                val modifierList = ktclass.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("enum")
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            And("it should have four constants defined, NORTH, SOUTH, WEST, EAST, respectively"){
                val classBody = ktclass.getChild(1)

                assertThat(classBody.children).hasSize(4)

                assertThat(classBody.getFirstChild().type).isEqualTo("KtEnumEntry")
                assertThat(classBody.getFirstChild().label).isEqualTo("NORTH")

                assertThat(classBody.getChild(1).type).isEqualTo("KtEnumEntry")
                assertThat(classBody.getChild(1).label).isEqualTo("SOUTH")

                assertThat(classBody.getChild(2).type).isEqualTo("KtEnumEntry")
                assertThat(classBody.getChild(2).label).isEqualTo("WEST")

                assertThat(classBody.getChild(3).type).isEqualTo("KtEnumEntry")
                assertThat(classBody.getChild(3).label).isEqualTo("EAST")

            }
        }

        Scenario("A class with anonymous class"){

            Given("A enum that has a abstract method"){
                code = """
enum class ProtocolState {
    WAITING {
        override fun signal() = TALKING
    },

    TALKING {
        override fun signal() = WAITING
    };

    abstract fun signal(): ProtocolState
}
""".trimIndent()
            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
            }

            val clsName = "ProtocolState"
            Then("it should contain one class named $clsName and the modifier enum") {
                ktclass = rootNode.getChild(2)
                assertThat(ktclass.type).isEqualTo("KtClass")
                assertThat(ktclass.label).isEqualTo(clsName)

                val modifierList = ktclass.getFirstChild()
                assertThat(modifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(modifierList.label).isEqualTo("")

                assertThat(modifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(modifierList.getFirstChild().label).isEqualTo("enum")
            }

            val nChildren = 2
            And("it should have $nChildren children") {
                assertThat(ktclass.children).hasSize(nChildren)
            }

            lateinit var classBody: ASTNode
            Then("It should has a abstract method named signal"){
                classBody = ktclass.getChild(1)
                assertThat(classBody.children).hasSize(3)

                assertThat(classBody.getChild(2).type).isEqualTo("KtNamedFunction")
                assertThat(classBody.getChild(2).label).isEqualTo("signal")

                val funcModifierList = classBody.getChild(2).getFirstChild()
                assertThat(funcModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(funcModifierList.label).isEqualTo("")

                assertThat(funcModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(funcModifierList.getFirstChild().label).isEqualTo("abstract")
            }

            lateinit var const1: ASTNode
            lateinit var const2: ASTNode
            And("it should have two constants defined, WAITIING and TALKING, respectively"){

                const1 = classBody.getFirstChild()
                assertThat(const1.type).isEqualTo("KtEnumEntry")
                assertThat(const1.label).isEqualTo("WAITING")

                assertThat(const1.getFirstChild().type).isEqualTo("KtClassBody")
                assertThat(const1.getFirstChild().label).isEqualTo("")

                const2 = classBody.getChild(1)
                assertThat(const2.type).isEqualTo("KtEnumEntry")
                assertThat(const2.label).isEqualTo("TALKING")

                assertThat(const2.getFirstChild().type).isEqualTo("KtClassBody")
                assertThat(const2.getFirstChild().label).isEqualTo("")

            }

            And("Both constants should overrid the method signal"){

                val firstContFunc = const1.getFirstChild().getFirstChild()
                assertThat(firstContFunc.type).isEqualTo("KtNamedFunction")
                assertThat(firstContFunc.label).isEqualTo("signal")

                val firstConstFuncModifierList = firstContFunc.getFirstChild()

                assertThat(firstConstFuncModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(firstConstFuncModifierList.label).isEqualTo("")

                assertThat(firstConstFuncModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(firstConstFuncModifierList.getFirstChild().label).isEqualTo("override")


                val secConstFunc = const2.getFirstChild().getFirstChild()
                assertThat(secConstFunc.type).isEqualTo("KtNamedFunction")
                assertThat(secConstFunc.label).isEqualTo("signal")

                val secConstFuncModifierList = secConstFunc.getFirstChild()

                assertThat(secConstFuncModifierList.type).isEqualTo("KtDeclarationModifierList")
                assertThat(secConstFuncModifierList.label).isEqualTo("")

                assertThat(secConstFuncModifierList.getFirstChild().type).isEqualTo("ModifierEntry")
                assertThat(secConstFuncModifierList.getFirstChild().label).isEqualTo("override")
            }
        }
    }
})

