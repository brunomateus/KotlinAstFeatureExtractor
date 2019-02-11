package fr.uphf.ast

data class ASTNode(val type: String, val label: String, val children: MutableList<ASTNode> = emptyList<ASTNode>().toMutableList()) {

	fun addChild(child: ASTNode) = children.add(child)

	fun getChild(i: Int) = children[i]

	fun getFirstChild(): ASTNode = children.first()

}
