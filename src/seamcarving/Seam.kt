package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.sqrt

class Seam {
    private val matrix = mutableListOf(mutableListOf(mutableListOf<Int>()))
    private lateinit var energyMap: Array<DoubleArray>
    private var w = 0
    private var h = 0

    companion object {
        const val RED_INDEX = 0
        const val GREEN_INDEX = 1
        const val BLUE_INDEX = 2
    }

    fun findVerticalSeam(input: String, output: String) {
        val image = ImageIO.read(File(input))
        buildMatrix(image) // sets w and h
        buildEnergyMap()
        val seam = createVerticalSeam()
        processImage(image, seam)
        ImageIO.write(image, "png", File(output))
    }

    private fun buildMatrix(image: BufferedImage) {
        w = image.width
        h = image.height

        for (y in 0 until h) {
            val matrixRow = mutableListOf<MutableList<Int>>()
            for (x in 0 until w) {
                val matrixCol = mutableListOf<Int>()
                val pixel = image.getRGB(x, y)
                val color = Color(pixel, true)
                matrixCol.add(RED_INDEX, color.red)
                matrixCol.add(GREEN_INDEX, color.green)
                matrixCol.add(BLUE_INDEX, color.blue)
                matrixRow.add(matrixCol)
            }
            matrix.add(y, matrixRow)
        }
    }

    private fun buildEnergyMap() {
        energyMap = Array(h) { DoubleArray(w) }

        for (y in 0 until h) {
            for (x in 0 until w) {
                energyMap[y][x] = sqrt((deltaX2(x, y) + deltaY2(x, y)).toDouble())
            }
        }
    }

    private fun createVerticalSeam(): MutableList<Pair<Int, Int>> {
        return buildLowestSeam(buildLowestEnergyMatrix())
    }

    private fun buildLowestEnergyMatrix(): Array<DoubleArray> {
        val lowestEnergyMatrix = energyMap.copyOf()

        for (y in 1 until h) {
            for (x in 0 until w) {
                lowestEnergyMatrix[y][x] += lowestAbove(x, y)
            }
        }

        return lowestEnergyMatrix
    }

    private fun lowestAbove(x: Int, y: Int): Double {
        return listOf(above(x - 1, y - 1), above(x, y - 1), above(x + 1, y - 1)).minOf { it }
    }

    private fun above(x: Int, y: Int): Double {
        return if (x in 0 until w) {
            energyMap[y][x]
        } else {
            Double.MAX_VALUE
        }
    }

    private fun buildLowestSeam(lowestEnergyMatrix: Array<DoubleArray>): MutableList<Pair<Int, Int>> {
        val lowestSeam = mutableListOf<Pair<Int, Int>>()

        // Find the index of the lowest energy on the bottom row
        val bottomRow = lowestEnergyMatrix[h - 1].asList()
        val indexOfLeast = bottomRow.withIndex().minByOrNull { it.value }!!.index
        lowestSeam.add(indexOfLeast to h - 1)

        // Work back up to top by lowest energy
        var y1 = h - 2
        var x1 = indexOfLeast

        while (y1 >= 0) {
            val threeAbove = listOfAbove(lowestEnergyMatrix[y1], x1)
            val indexOfLowest = threeAbove.withIndex().minByOrNull { it.value }!!.index
            x1 += indexOfLowest - 1
            lowestSeam.add(0, x1 to y1)
            y1--
        }

        return lowestSeam
    }

    private fun listOfAbove(row: DoubleArray, x: Int): List<Double> {
        val threeAbove = mutableListOf<Double>()
        if (x > 0) threeAbove.add(row[x - 1]) else threeAbove.add(Double.MAX_VALUE)
        threeAbove.add(row[x])
        if (x < w) threeAbove.add(row[x + 1]) else threeAbove.add(Double.MAX_VALUE)

        return threeAbove
    }

    private fun processImage(image: BufferedImage, seam: List<Pair<Int, Int>>) {
        for (coordinates in seam) {
            image.setRGB(coordinates.first, coordinates.second, Color.RED.rgb)
        }
    }

    // Rx(x, y)^2 + Gx(x, y)^2 + Bx(x, y)^2
    private fun deltaX2(x: Int, y: Int): Int {
        val x1 = if (x == 0) 1 else if (x == w - 1) x - 1 else x
        return square(dx(x1, y, RED_INDEX)) +
               square(dx(x1, y, GREEN_INDEX)) +
               square(dx(x1, y, BLUE_INDEX))
    }

    // Ry(x, y)^2 + Gy(x, y)^2 + By(x, y)^2
    private fun deltaY2(x: Int, y: Int): Int {
        val y1 = if (y == 0) 1 else if (y == h - 1) y - 1 else y
        return square(dy(x, y1, RED_INDEX)) +
               square(dy(x, y1, GREEN_INDEX)) +
               square(dy(x, y1, BLUE_INDEX))
    }

    // Cx = [x - 1, y] - [x + 1, y], C = color
    private fun dx(x: Int, y: Int, color: Int): Int {
        return matrix[y][x - 1][color] - matrix[y][x + 1][color]
    }

    // Cy = [x, y - 1] - [x, y + 1], C = color
    private fun dy(x: Int, y: Int, color: Int): Int {
        return matrix[y - 1][x][color] - matrix[y + 1][x][color]
    }

    private fun square(x: Int): Int = x * x
}
