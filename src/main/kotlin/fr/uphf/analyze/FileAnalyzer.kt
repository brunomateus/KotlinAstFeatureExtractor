package fr.uphf.analyze

import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtFile

abstract class FileAnalyzer {

    abstract fun analyze(file: KtFile): List<Finding>

}