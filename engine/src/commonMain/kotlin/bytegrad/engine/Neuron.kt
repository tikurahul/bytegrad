package bytegrad.engine

/**
 * Models a simple [Neuron] as a linear combination of [weights] plus a [bias].
 */
class Neuron(inputs: Int) {
    // Labels
    private val id = nextId()
    private val prefix = "neuron-$id"

    // Weights
    internal val weights: Array<Value> = Array(inputs) {
        nextValue(from = -1.0, until = 1.0).apply { label = "$prefix-weight-$it" }
    }

    // Bias
    internal val bias = nextValue(from = -1.0, until = 1.0).apply { label = "$prefix-bias" }

    // All the parameters
    private val parameters = listOf(*weights, bias)

    fun parameters(): List<Value> {
        return parameters
    }

    operator fun invoke(values: List<Value>): Value {
        require(weights.size == values.size)
        // w * value + b
        var activation = bias
        for (i in weights.indices) {
            val value = values[i]
            val weight = weights[i]
            val m = weight * value
            activation += m
        }
        return activation.tanh()
    }

    override fun toString(): String {
        val weights = weights.contentToString()
        return "Neuron([$weights], $bias)"
    }
}
