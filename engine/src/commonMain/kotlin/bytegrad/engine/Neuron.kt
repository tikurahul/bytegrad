package bytegrad.engine

/**
 * Models a simple neuron.
 */
class Neuron(inputs: Int) {
    internal val weights: Array<Value> = Array(inputs) {
        nextValue(from = -1.0, until = 1.0).apply { label = "w$it" }
    }
    internal val bias = nextValue(from = -1.0, until = 1.0).apply { label = "b" }
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
