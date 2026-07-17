package bytegrad.engine

/** Explicit type conversion to go from a [Double] to a [Value] type. */
fun Number.toValue(): Value {
    return Value(data = this.toDouble(), label = "$this")
}
