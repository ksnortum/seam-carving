package seamcarving

import java.io.File
import javax.imageio.ImageIO

class ResizableImage(inputFileName: String) {
    private val image = ImageIO.read(File(inputFileName))

    fun resize(reduceWidthBy: Int, reduceHeightBy: Int, outputFileName: String) {
        var newImage = image

        repeat (reduceWidthBy) {
            newImage = Seam(newImage).removeSeam(isVertical = true)
        }

        repeat (reduceHeightBy) {
            newImage = Seam(newImage).removeSeam(isVertical = false)
        }

        ImageIO.write(newImage, "png", File(outputFileName))
    }
}