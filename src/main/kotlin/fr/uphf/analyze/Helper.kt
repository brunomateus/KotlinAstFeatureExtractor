@file:JvmName("Helper")

package fr.uphf.analyze

import com.beust.klaxon.*
import fr.uphf.ast.ASTExtractor
import fr.uphf.ast.ASTNode
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile


typealias DetectionResult = Map<String, List<Finding>>

fun getResult(findings: List<Finding>): DetectionResult = findings.groupBy { it.id }

fun DetectionResult.toJson(): JsonObject {
    val summary = JsonObject()
    val details = JsonArray<JsonObject>()
    for ((feature, occurences) in this) {
        val occurencesJson = JsonArray<JsonObject>()
        summary[feature] = occurences.size
        occurences.forEach { occurencesJson.add(
            JsonObject(mapOf("file" to it.entity.location.file,
                "entity" to it.entity.name,
                "kt_element" to it.entity.ktElement.toString(),
                "location" to it.entity.location.compact()))
        ) }
        val featureJson = JsonObject(mapOf("name" to feature, "occur" to occurencesJson))
        details.add(featureJson)
    }
    return JsonObject(mapOf("summary" to summary, "findings" to details))

}

fun DetectionResult.asStringJson(): String = this.toJson().toJsonString(true)


fun printAST(node: ASTNode) {
    val content = getASTasStringJson(node)
    println(content)
}

fun getASTasStringJson(code: String, filename: String = "temp.kt"): String {
    return getASTasStringJson(getASTasJson(code, filename))
}

fun getASTasStringJson(node: ASTNode): String {
    val builder = StringBuilder(Klaxon().toJsonString(node))
    return (Parser().parse(builder) as JsonBase).toJsonString(true)
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