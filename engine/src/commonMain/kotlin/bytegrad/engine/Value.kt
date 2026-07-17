package bytegrad.engine

// An implementation of the scalar value type like in Micrograd.
// This is my take on Andrej's famous YouTube video: https://www.youtube.com/watch?v=VMj-3S1tku0
data class Value(
    val data: Double,
    // The label
    var label: String = "",
    // The previous nodes if this was a result of an operation.
    internal val previous: List<Value> = emptyList(),
    internal val operator: Operator = Operator.None,
) {
    operator fun plus(other: Value): Value {
        return Value(
            data = data + other.data,
            previous = listOf(this, other),
            operator = Operator.Plus
        )
    }

    operator fun times(other: Value): Value {
        return Value(
            data = data * other.data,
            previous = listOf(this, other),
            operator = Operator.Times
        )
    }
}
