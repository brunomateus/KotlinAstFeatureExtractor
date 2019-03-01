package fr.uphf.ast

import fr.uphf.analyze.getASTasJson
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.io.File
import java.nio.charset.Charset

object LeafSpek : Spek({
    Feature("Kotlin Annotations") {

        lateinit var rootNode: ASTNode
        lateinit var code: String
        lateinit var cls: ASTNode

        Scenario("Defining a multitarget annotation") {

            Given("A class that defines a multitarget annotation"){
                code = LeafSpek::class.java.getResource("/test.tk").readText()

            }
            When("the AST is retrieved") {
                rootNode = getASTasJson(code)
                print(rootNode.leafNode())
            }


        }



    }

})

