package bytegrad

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import bytegrad.engine.*
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
        val image = remember { useEngineMLP().toComposeImageBitmap() }
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
            Box(modifier = Modifier.fillMaxSize()) {
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
                // Scrollbars
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(verticalScrollState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                )
                HorizontalScrollbar(
                    adapter = rememberScrollbarAdapter(horizontalScrollState),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        }
    }
}

internal fun useEngineNeuron(): BufferedImage {
    val size = 8
    val neuron = Neuron(inputs = size)
    val values = List(size) { nextValue(from = 1.0, until = 5.0) }
    val output = neuron(values)
    output.zeroGrad()
    output.backwardPass()
    val graph = output.renderAsGraph()
    return graph.toFile(FileType.PNG).inputStream().use {
        ImageIO.read(it)
    }
}

internal fun useEngineMLP(): BufferedImage {
    val size = 3
    val mlp = MLP(inputs = 3, layerSizes = listOf(4, 4, 1))
    val values = List(size) { nextValue(from = 1.0, until = 5.0) }
    val output = mlp(x = values)[0]
    output.zeroGrad()
    output.backwardPass()
    val graph = output.renderAsGraph()
    return graph.toFile(FileType.PNG).inputStream().use {
        ImageIO.read(it)
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

    val sum = x1w1 + x2w2; sum.label = "sum"
    val n = sum + b; n.label = "n"
    val output = n.tanh(); output.label = "output"

    output.zeroGrad()
    output.backwardPass()

    val graph = output.renderAsGraph()
    return graph.toFile(FileType.PNG).inputStream().use {
        ImageIO.read(it)
    }
}
