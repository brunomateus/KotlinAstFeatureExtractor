package fr.uphf.analyze

import com.beust.klaxon.JsonBase
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import fr.uphf.ast.ASTExtractor
import fr.uphf.ast.ASTNode
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile

fun printAST(node: ASTNode) {
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

fun getASTasJson(code: String, filename: String = "temp.kt"): ASTNode {
    val ktFile = compileTo(code, filename)
    val parser = ASTExtractor()
    return parser.getASTInJSON(ktFile)
}

fun compileTo(code: String, filename: String="temp.kt") =
    PsiManager.getInstance(proj).findFile(LightVirtualFile(filename, KotlinFileType.INSTANCE, code)) as KtFile