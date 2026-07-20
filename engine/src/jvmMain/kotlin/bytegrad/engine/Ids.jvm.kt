package bytegrad.engine

import java.util.concurrent.atomic.AtomicInteger

internal val id = AtomicInteger(0)

actual fun nextId(): Int {
    return id.incrementAndGet()
}
