package bytegrad.engine

// An implementation of the scalar value type like in Micrograd.
// This is my take on Andrej's famous YouTube video: https://www.youtube.com/watch?v=VMj-3S1tku0
class Value(
    val data: Double,
    // Labels
    var label: String = "",
    // The previous nodes if this was a result of an operation.
    internal val previous: Set<Value> = emptySet(),
    internal val operator: Operator = Operator.None,
) {
    // The gradient
    internal var grad: Double = 0.0

    // The backward pass
    internal var backward: () -> Unit = {}

    operator fun plus(other: Value): Value {
        val output = Value(
            data = data + other.data,
            previous = setOf(this, other),
            operator = Operator.Plus
        )
        output.backward = {
            this.grad += 1.0 * output.grad
            other.grad += 1.0 * output.grad
        }
        return output
    }

    operator fun times(other: Value): Value {
        val output = Value(
            data = data * other.data,
            previous = setOf(this, other),
            operator = Operator.Times
        )
        output.backward = {
            this.grad += output.grad * other.data
            other.grad += output.grad * this.data
        }
        return output
    }

    fun zeroGrad() {
        grad = 0.0
        if (previous.isEmpty()) return
        for (value in previous) value.zeroGrad()
    }
}
