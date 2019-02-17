package fr.uphf.analyze

import io.gitlab.arturbosch.detekt.api.Finding

abstract class DetectionResult{

    companion object {

        fun from(findings: List<Finding>): Map<String, List<Finding>> {
            val result = HashMap<String, MutableList<Finding>>()
            findings.groupByTo(result){
                it.id
            }
            return result.toMap()
        }

    }

}