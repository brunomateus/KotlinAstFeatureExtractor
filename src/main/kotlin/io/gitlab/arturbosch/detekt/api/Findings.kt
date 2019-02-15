package io.gitlab.arturbosch.detekt.api

/**
 * Base interface of detection findings. Inherits a bunch of useful behaviour
 * from sub interfaces.
 *
 * Basic behaviour of a finding is that is can be assigned to an id and a source code position described as
 * an entity. Metrics and entity references can also considered for deeper characterization.
 *
 * @author Artur Bosch
 */
sealed class Finding() : Compactable, HasEntity, HasMetrics {
    abstract val id: String
    //abstract val issue: Issue
    abstract val references: List<Entity>
    abstract val message: String

    open fun messageOrDescription(): String = message
}

/**
 * Describes a source code position.
 */
interface HasEntity {
    val entity: Entity
    val location: Location
        get() = entity.location
    val locationAsString: String
        get() = location.locationString
    val startPosition: SourceLocation
        get() = location.source
    val charPosition: TextLocation
        get() = location.text
    val file: String
        get() = location.file
    val signature: String
        get() = entity.signature
    val name: String
        get() = entity.name
    val inClass: String
        get() = entity.className
}

/**
 * Adds metric container behaviour.
 */
interface HasMetrics {
    val metrics: List<Metric>
    fun metricByType(type: String): Metric? = metrics.find { it.type == type }
}

/**
 * Provides a compact string representation.
 */
interface Compactable {
    fun compact(): String
    fun compactWithSignature(): String = compact()
}

open class Feature(
    override val id: String,
    override val entity: Entity,
    override val message: String = "",
    override val references: List<Entity> = listOf(),
    override val metrics: List<Metric> = listOf()
    ) : Finding() {

    override fun compact(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * A code smell indicates any possible design problem inside a program's source code.
 * The type of a code smell is described by an [Issue].
 *
 * If the design problem results from metric violations, a list of [Metric]'s
 * can describe further the kind of metrics.
 *
 * If the design problem manifests by different source locations, references to these
 * locations can be stored in additional [Entity]'s.
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class CodeSmell(
    private val issue: Issue,
    override val entity: Entity,
    override val message: String,
    override val metrics: List<Metric> = listOf(),
    override val references: List<Entity> = listOf()) : Finding() {

    override val id: String = issue.id

    override fun compact(): String = "$id - ${entity.compact()}"

    override fun compactWithSignature() = compact() + " - Signature=" + entity.signature

    override fun toString(): String {
        return "CodeSmell(issue=$issue, " +
                "entity=$entity, " +
                "message=$message, " +
                "metrics=$metrics, " +
                "references=$references, " +
                "id='$id')"
    }

    override fun messageOrDescription() = when {
        message.isEmpty() -> issue.description
        else -> message
    }
}


