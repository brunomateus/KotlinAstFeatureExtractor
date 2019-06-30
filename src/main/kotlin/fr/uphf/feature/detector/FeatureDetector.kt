package fr.uphf.feature

import fr.uphf.analyze.FileAnalyzer
import fr.uphf.feature.detector.*
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

class FeatureDetector(vararg val detectors: FileAnalyzer): FileAnalyzer(){

    override fun analyze(file: KtElement): List<Finding> {
        var results = emptyList<Finding>().toMutableList()

        for(detector in detectors){
            results.addAll(detector.analyze(file))
        }

        return results.toList()
    }

    companion object {

        fun extractAll(file: KtElement): List<Finding> {

            return FeatureDetector(
                DataClassDetector(),
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
                    StringTemplateDetector()
            ).analyze(file)
        }


    }

}