package fr.uphf.ast

import com.beust.klaxon.Json

data class ASTNode(val type: String, val label: String, val children: MutableList<ASTNode> = emptyList<ASTNode>().toMutableList(),  @Json(ignored = true) @Transient var parent: ASTNode? = null ) {

	fun addChild(child: ASTNode){
		val modifierList = children.find{ node -> child.type == "KtDeclarationModifierList" && node.type == child.type }
		if (modifierList != null){
			modifierList.children.addAll(child.children)
			for (c in child.children){
				c.parent = modifierList
			}
		} else {
			children.add(child)
			child.parent = this
		}
	}

	fun getChild(i: Int) = children[i]

	fun getFirstChild(): ASTNode = children.first()

	fun isLeaf() = children.isEmpty()

	fun setParentInChildren() {
		children.forEach {it.parent = this}
	}

	fun leafNode(): Set<String> {
		var leafs = emptySet<String>()
		if (isLeaf()) {
			leafs = leafs.plus(type)
		} else {
			children.forEach { leafs = leafs.union(it.leafNode()) }
		}
		return leafs
	}

	override fun toString(): String = type + " " + label
}
