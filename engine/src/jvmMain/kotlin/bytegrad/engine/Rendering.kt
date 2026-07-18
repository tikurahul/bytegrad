package bytegrad.engine

import org.graphper.api.Graphviz
import org.graphper.api.Node
import org.graphper.api.attributes.Color
import org.graphper.api.attributes.NodeShapeEnum
import org.graphper.api.attributes.Rankdir

/** Represents an edge in the graph */
internal data class Edge(val from: Value, val to: Value)

/**
 * Renders a [Value] instance as a DAG.
 */
fun Value.renderAsGraph(): Graphviz {
    val (nodes, edges) = buildGraph()
    // The map of id -> node
    val nodeMap = mutableMapOf<Value, Node>()
    // The map of the id -> operator node if one exists
    val operatorMap = mutableMapOf<Value, Node>()
    // Build node maps and operator maps
    for (node in nodes) {
        nodeMap[node] = node.graphNode()
        if (node.operator != Operator.None) {
            operatorMap[node] = node.operatorGraphNode()
        }
    }
    // Create the graph.
    // Left to right
    val graph = Graphviz.digraph()
        .rankdir(Rankdir.LR)

    for ((value, node) in operatorMap) {
        // Make the connections from the operator node to the computed value
        graph.addLine(node, nodeMap[value])
    }

    edges.forEach { edge ->
        val head = nodeMap[edge.from]
        val operator = operatorMap[edge.to]
        // Nodes -> Operator
        graph.addLine(head, operator)
    }
    return graph.build()
}

// Helpers

private fun Value.operatorGraphNode(): Node {
    val display = operator.name
    val node = Node.builder()
        .label(display)
        .shape(NodeShapeEnum.CIRCLE)
        .color(Color.LIGHT_BLUE)
        .build()
    return node
}

private fun Value.graphNode(): Node {
    val grad = String.format("%.4f", grad)
    val value = String.format("%.4f", data)
    val prefix = if (label.isNotBlank()) "$label |" else ""
    val displayLabel = "$prefix $value | $grad"
    val node = Node.builder()
        .label(displayLabel)
        .shape(NodeShapeEnum.RECT)
        .color(Color.LIGHT_GREY)
        .build()
    return node
}


private fun Value.buildGraph(): Pair<Set<Value>, Set<Edge>> {
    val remaining = ArrayDeque<Value>()
    remaining += this
    return buildGraph(remaining = remaining)
}

private tailrec fun buildGraph(
    remaining: ArrayDeque<Value>,
    nodes: MutableSet<Value> = mutableSetOf(),
    edges: MutableSet<Edge> = mutableSetOf()
): Pair<Set<Value>, Set<Edge>> {
    if (remaining.isEmpty()) return nodes to edges
    val current = remaining.removeFirst()
    nodes += current
    for (previousNode in current.previous) {
        edges += Edge(from = previousNode, to = current)
        remaining.addLast(element = previousNode)
    }
    return buildGraph(remaining = remaining, nodes = nodes, edges = edges)
}
