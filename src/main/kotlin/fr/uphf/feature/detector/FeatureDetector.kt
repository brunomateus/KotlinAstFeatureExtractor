package fr.uphf.feature

import fr.uphf.analyze.FileAnalyzer
import fr.uphf.analyze.compileTo
import fr.uphf.feature.detector.*
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

class FeatureDetector(private vararg val detectors: FileAnalyzer): FileAnalyzer(){

    override fun analyze(file: KtElement): List<Finding> {
        val results = emptyList<Finding>().toMutableList()

        for(detector in detectors){
            results.addAll(detector.analyze(file))
        }

        return results.toList()
    }

    companion object {

        fun extractAll(file: KtElement): List<Finding> {

            return FeatureDetector(
                DataClassDetector(),
                SealedClassDetector(),
                DelegationDetector(),
                DestructuringDeclarationDetector(),
                ExtensionFunctionAndOverloadedOpDetector(),
                LambdaDetector(),
                NamedAndDefaultArgumentDetector(),
                RangeExpressionDetector(),
                SafeOperartorDetector(),
                SmartCastDetector(),
                WhenExpressionDetector(),
                SingletonDetector(),
                CompanionDetector(),
                InlineFunctionDetector(),
                TypeInferenceDetector(),
                StringTemplateDetector(),
                InfixFunctionDetector(),
                InlineKlassDetector(),
                TypeAliasDetector(),
                TailrecFunctionDetector(),
                ContractDetector(),
                CoroutineDetector(),
                OverloadDetector()
            ).analyze(file)
        }

        fun extractAll(file: File): List<Finding> {
            return extractAll(compileTo(file.readText().replace("\r", ""), file.path))
        }

        fun extractAll(files: List<File>): List<Finding> {
            val findings = emptyList<Finding>().toMutableList()
            files.forEach { file -> extractAll(file).also{
                    findings.addAll(it)
                }
            }

            return findings.toList()
        }


    }

}