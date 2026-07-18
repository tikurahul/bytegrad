package bytegrad.engine

/** Operators applicable on [Value] */
enum class Operator {
    None,               // Leaf nodes start off with None
    Plus,               // Addition
    Times,              // Multiplication
    Power,              // Power
    Exponent,           // Exponent
}
