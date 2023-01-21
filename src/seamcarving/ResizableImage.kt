package seamcarving

import java.io.File
import javax.imageio.ImageIO

class ResizableImage(private val inputFileName: String) {
    private val image = ImageIO.read(File(inputFileName))

    fun resize(reduceWidthBy: Int, reduceHeightBy: Int, outputFileName: String) {
        val ext = inputFileName.substringAfterLast(".", "")
        val formatNames = ImageIO.getReaderFormatNames()

        if (ext !in formatNames) {
            println("Sorry, your system does not accept images of type $ext")
            println("Supported types:")
            println(formatNames.sorted().joinToString(", "))
            return
        }

        var newImage = image

        repeat (reduceWidthBy) {
            newImage = Seam(newImage, ext).removeSeam(isVertical = true)
        }

        repeat (reduceHeightBy) {
            newImage = Seam(newImage, ext).removeSeam(isVertical = false)
        }

        val success = ImageIO.write(newImage, ext, File(outputFileName))
        if (!success) println("There was a problem writing to the output file")
    }
}