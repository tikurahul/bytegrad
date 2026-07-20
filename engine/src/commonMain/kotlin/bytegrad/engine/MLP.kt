package bytegrad.engine

/**
 * The Multi Layer Perceptron.
 */
class MLP(inputs: Int, layerSizes: List<Int>) {
    internal val all = mutableListOf<Int>()
    internal val layers = mutableListOf<Layer>()

    init {
        // Include input layer
        all += inputs
        all += layerSizes
        // Create the layer cascade
        for (i in 0 until all.size - 1) {
            val inputs = all[i]
            val next = all[i + 1]
            layers.add(Layer(inputs = inputs, outputs = next))
        }
    }

    operator fun invoke(x: List<Value>): List<Value> {
        var result = x
        for (layer in layers) {
            result = layer(result)
        }
        return result
    }

    override fun toString(): String {
        val layers = layers.joinToString(separator = ",")
        return "MLP(inputs = ${all.size}, layers = [$layers])"
    }
}
