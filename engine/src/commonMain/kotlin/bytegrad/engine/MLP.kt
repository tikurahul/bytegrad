package bytegrad.engine

/**
 * The Multi Layer Perceptron.
 */
class MLP(inputs: Int, layerSizes: List<Int>) {
    internal val all = mutableListOf<Int>()
    internal val layers = mutableListOf<Layer>()
    private val parameters = mutableListOf<Value>()

    init {
        // Include input layer
        all += inputs
        all += layerSizes
        // Create the layer cascade
        for (i in 0 until all.size - 1) {
            val inputs = all[i]
            val next = all[i + 1]
            val layer = Layer(inputs = inputs, outputs = next)
            parameters += layer.parameters()
            layers.add(layer)
        }
    }

    fun parameters(): List<Value> {
        return parameters
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
