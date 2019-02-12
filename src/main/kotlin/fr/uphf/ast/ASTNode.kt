package fr.uphf.ast

data class ASTNode(val type: String, val label: String, val children: MutableList<ASTNode> = emptyList<ASTNode>().toMutableList()) {

	fun addChild(child: ASTNode){
		val modifierList = children.find{ node -> child.type == "KtDeclarationModifierList" && node.type == child.type }
		if (modifierList != null){
			modifierList.children.addAll(child.children)
		} else {
			children.add(child)
		}
	}

	fun getChild(i: Int) = children[i]

	fun getFirstChild(): ASTNode = children.first()

}
