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
import bytegrad.engine.toValue
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
    val x = Value(data = 10.0, label = "x")
    val y = Value(data = 20.0, label = "y")
    val z = 20.0.toValue() + x
    z.label = "z"
    val a = x + y * z
    a.label = "a"
    val graph = a.renderAsGraph()
    return graph.toFile(FileType.PNG).inputStream().use {
        ImageIO.read(it)
    }
}
