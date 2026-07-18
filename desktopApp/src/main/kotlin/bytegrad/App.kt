package bytegrad

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import bytegrad.engine.Value
import bytegrad.engine.renderAsGraph
import org.graphper.api.FileType
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@Composable
fun App() {
    Graph()
}

@Composable
fun Graph() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val image = remember { useEngine().toComposeImageBitmap() }
        val density = LocalDensity.current
        val vw = with(density) { maxWidth.toPx() }
        val vh = with(density) { maxHeight.toPx() }
        if (image.width <= vw && image.height <= vh) {
            Image(
                bitmap = image,
                contentDescription = "The DAG (scaled up)",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val horizontalScrollState = rememberScrollState()
            val verticalScrollState = rememberScrollState()
            Box(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = image,
                    contentDescription = "The DAG",
                    contentScale = ContentScale.None
                )
            }
        }
    }
}

internal fun useEngine(): BufferedImage {
    // Inputs
    val x1 = Value(2.0, label = "x1")
    val x2 = Value(0.0, label = "x2")
    // Weights
    val w1 = Value(-3.0, label = "w1")
    val w2 = Value(1.0, label = "w2")
    // Bias
    val b = Value(6.881373587019543, label = "b")
    // x1w1 + x2w2 + b
    val x1w1 = x1 * w1; x1w1.label = "x1w1"
    val x2w2 = x2 * w2; x1w1.label = "x2w2"

    val x1w1X2w2 = x1w1 + x2w2; x1w1X2w2.label = "x1w1+x2w2"
    val n = x1w1X2w2 + b; n.label = "n"
    val output = n.tanh(); output.label = "output"

    output.zeroGrad()
    output.backwardPass()

    val graph = output.renderAsGraph()
    return graph.toFile(FileType.PNG).inputStream().use {
        ImageIO.read(it)
    }
}
