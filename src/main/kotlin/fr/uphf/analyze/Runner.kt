package fr.uphf.analyze

import fr.uphf.feature.FeatureDetector
import java.io.File

fun main(args: Array<String>){

    if(args.isNotEmpty()){
        val projects = File(args.first()).walkTopDown().maxDepth(1).filter { it.isDirectory  }
        var n = 1
        projects.drop(1).forEach { project ->
            println("${n++} Analyzing project ${project.name}")
            val findings = FeatureDetector.extractAll(project.walkTopDown().filter { it.name.endsWith(".kt")}.toList())
            val result = getResult(findings)
            File("/tmp/result/${project.name}.json").writeText(result.asStringJson())
        }
    } else {
        println("It is necessary to inform the location (path) of analyzes")
    }



}