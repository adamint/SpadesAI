package spades.decisiontree

import hu.webarticum.treeprinter.SimpleTreeNode
import spades.engine.Hand
import spades.engine.Round
import spades.engine.Trick

abstract class DecisionNode<T>(
    name: String? = null,
    internal val children: List<DecisionNode<T>> = listOf()
) {
    val readableName: String =
        name ?: if (this::class.simpleName!!.length >= 18) this::class.simpleName!!.filter { it.isUpperCase() }
        else this::class.simpleName!!

    val leaf get() = children.isEmpty()

    open fun getPrintableNode(depth: Int = 0): SimpleTreeNode {
        val node = SimpleTreeNode(readableName)
        children.forEach { node.addChild(it.getPrintableNode(depth + 1)) }

        return node
    }

    fun classify(hand: Hand, trick: Trick, round: Round): T {
        //print(this::class.simpleName + " ")
        return classifyImpl(hand, trick, round)
    }

    protected abstract fun classifyImpl(hand: Hand, trick: Trick, round: Round): T
}