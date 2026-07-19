package bytegrad.engine

/**
 * Models a `layer` of [Neuron]s.
 */
class Layer(private val inputs: Int, private val outputs: Int) {
    internal val neurons: Array<Neuron> = Array(size = outputs) {
        Neuron(inputs = inputs)
    }

    operator fun invoke(values: List<Value>): List<Value> {
        return neurons.map { n -> n(values) }
    }

    override fun toString(): String {
        return "Layer(inputs = $inputs, outputs = $outputs)"
    }
}
