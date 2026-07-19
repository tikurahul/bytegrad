package bytegrad.engine

import kotlin.random.Random

internal val random = Random(seed = 42)

fun nextValue(from: Double, until: Double): Value {
    return random.nextDouble(from = from, until = until).toValue()
}
