package fr.uphf.analyze

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
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

        fun asJson(findings: List<Finding>): String = asJson(from(findings))

        fun asJson(result: Map<String, List<Finding>>): String {
            val summary = JsonObject()
            val details = JsonArray<JsonObject>()
            result.forEach { feature, occurences ->
                val occurencesJson = JsonArray<JsonObject>()
                summary[feature] = occurences.size
                occurences.forEach { occurencesJson.add(
                    JsonObject(mapOf("entity" to it.entity.name,
                        "kt_element" to it.entity.ktElement,
                        "location" to it.entity.location.locationString))
                ) }
                val featureJson = JsonObject(mapOf("name" to feature, "occur" to occurencesJson))
                details.add(featureJson)
            }
            return JsonObject(mapOf("summary" to summary, "findings" to details)).toJsonString(true)
        }

    }

}