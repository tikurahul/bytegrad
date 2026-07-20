package bytegrad.engine

import kotlin.math.pow

// An implementation of the scalar value type like in Micrograd.
// This is my take on Andrej's famous YouTube video: https://www.youtube.com/watch?v=VMj-3S1tku0
class Value(
    val data: Double,
    // Labels
    var label: String = "",
    // The previous nodes if this was a result of an operation.
    internal val previous: Array<Value> = emptyArray(),
    internal val operator: Operator = Operator.None,
) {
    // The gradient
    var grad: Double = 0.0
        internal set

    // The backward pass
    var backward: () -> Unit = {}
        internal set

    operator fun plus(other: Value): Value {
        val output = Value(
            data = data + other.data,
            previous = arrayOf(this, other),
            operator = Operator.Plus
        )
        output.backward = {
            grad += 1.0 * output.grad
            other.grad += 1.0 * output.grad
        }
        return output
    }

    operator fun minus(other: Value): Value {
        val negation = other * Value(data = -1.0)
        return this + negation
    }

    operator fun times(other: Value): Value {
        val output = Value(
            data = data * other.data,
            previous = arrayOf(this, other),
            operator = Operator.Times
        )
        output.backward = {
            grad += output.grad * other.data
            other.grad += output.grad * data
        }
        return output
    }

    operator fun div(other: Value): Value {
        return this.times(other.power(value = -1.0))
    }

    fun power(value: Float) = power(value = value.toDouble())

    // Forcing other to be a constant here to make the backward pass easier
    fun power(value: Double): Value {
        val output = Value(
            data = data.pow(x = value),
            previous = arrayOf(this),
            operator = Operator.Power
        )
        output.backward = {
            // d/dx x.pow(n) = n * x.pow(n -1)
            grad += (value * data.pow(value - 1)) * output.grad
        }
        return output
    }

    // Math.exp()
    fun exp(): Value {
        val output = Value(
            data = kotlin.math.exp(x = data),
            previous = arrayOf(this),
            operator = Operator.Exponent
        )

        output.backward = {
            // d/dx(exp(x)) = exp(x)
            grad += kotlin.math.exp(this.data) * output.grad
        }

        return output
    }

    fun relu(): Value {
        val relu = if (this.data >= 0) 1.0 else 0.0
        val output = Value(data = relu, previous = arrayOf(this), operator = Operator.Relu)
        output.backward = {
            val m = if (data >= 0) 1.0 else 0.0
            grad += m * output.grad
        }
        return output
    }

    fun tanh(): Value {
        val x = data
        val t = (kotlin.math.exp(2 * x) - 1) / (kotlin.math.exp(2 * x) + 1)
        val output = Value(data = t, previous = arrayOf(this), operator = Operator.Tanh)
        output.backward = {
            grad += (1 - t.pow(2)) * output.grad
        }
        return output
    }

    fun backwardPass(gradientInit: Boolean = true) {
        // The result node should initialize its gradient
        if (gradientInit) grad = 1.0
        backward()
        if (previous.isEmpty()) return
        // Children only get the propagated gradients
        for (value in previous) value.backwardPass(gradientInit = false)
    }

    fun zeroGrad() {
        grad = 0.0
        if (previous.isEmpty()) return
        for (value in previous) value.zeroGrad()
    }

    override fun toString(): String {
        val previous = previous.joinToString(separator = ",")
        return "Value(data=$data, grad=$grad, operator=$operator, previous=[$previous])"
    }
}
