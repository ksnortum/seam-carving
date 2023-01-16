package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.sqrt

class EnergyMap(private val image: BufferedImage) {
    private val w = image.width
    private val h = image.height

    fun buildEnergyMap(): Array<DoubleArray> {
        val energyMap = Array(image.height) { DoubleArray(image.width) }

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                energyMap[y][x] = sqrt((deltaX2(x, y) + deltaY2(x, y)).toDouble())
            }
        }

        return energyMap
    }

    // Rx(x, y)^2 + Gx(x, y)^2 + Bx(x, y)^2
    private fun deltaX2(x: Int, y: Int): Int {
        val x1 = if (x == 0) 1 else if (x == w - 1) x - 1 else x
        val colorLeft = Color(image.getRGB(x1 - 1, y), true)
        val colorRight = Color(image.getRGB(x1 + 1, y), true)
        return square(colorLeft.red - colorRight.red) +
                square(colorLeft.green - colorRight.green) +
                square(colorLeft.blue - colorRight.blue)
    }

    // Ry(x, y)^2 + Gy(x, y)^2 + By(x, y)^2
    private fun deltaY2(x: Int, y: Int): Int {
        val y1 = if (y == 0) 1 else if (y == h - 1) y - 1 else y
        val colorDown = Color(image.getRGB(x, y1 - 1), true)
        val colorUp = Color(image.getRGB(x, y1 + 1), true)

        return square(colorDown.red - colorUp.red) +
                square(colorDown.green - colorUp.green) +
                square(colorDown.blue - colorUp.blue)
    }

    private fun square(x: Int): Int = x * x
}