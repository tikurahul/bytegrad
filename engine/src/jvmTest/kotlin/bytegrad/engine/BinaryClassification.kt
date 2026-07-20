package bytegrad.engine

import kotlin.test.Test

class BinaryClassification {
    @Test
    fun binaryClassification() {
        // Inputs
        val inputs = listOf(
            listOf(2.0, 1.0, -1.0).map { it.toValue() },
            listOf(3.0, -1.0, 0.5).map { it.toValue() },
            listOf(0.5, 1.0, 1.0).map { it.toValue() },
            listOf(1.0, 1.0, -1.0).map { it.toValue() }
        )
        // Expected outputs
        val outputs = listOf(1.0, -1.0, -1.0, 1.0).map { it.toValue() }
        // Define the MLP
        val mlp = MLP(inputs = 3, layerSizes = listOf(4, 4, 1))
        var predictions = inputs.map { input ->
            // The last layer has size 1
            mlp(input).first()
        }
        var loss = squaredLoss(predictions = predictions, expected = outputs)
        loss.zeroGrad()
        loss.backwardPass()
        val alpha = 0.01
        var i = 0
        while (i < 1000) {
            // Goal is to minimize the loss
            for (parameter in mlp.parameters()) {
                // Gradient descent
                parameter.data += -1 * alpha * parameter.grad
            }
            predictions = inputs.map { input ->
                // The last layer has size 1
                mlp(input).first()
            }
            loss = squaredLoss(predictions = predictions, expected = outputs)
            loss.zeroGrad()
            loss.backwardPass()
            i += 1
        }
        println("Final loss ${loss.data}")
    }

    private fun squaredLoss(predictions: List<Value>, expected: List<Value>): Value {
        var loss = 0.0.toValue()
        for (i in predictions.indices) {
            val p = predictions[i]
            val e = expected[i]
            loss += (e - p).power(2.0)
        }
        return loss
    }
}