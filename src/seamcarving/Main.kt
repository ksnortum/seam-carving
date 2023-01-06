package seamcarving

import java.awt.Color
import java.io.File
import java.lang.IllegalArgumentException
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val argsMap = getArgsMap(args)
    val input = argsMap["in"] ?: throw IllegalArgumentException("'in' not found")
    val output = argsMap["out"] ?: throw  IllegalArgumentException("'out' not found")
    Main().run(input, output)
}

fun getArgsMap(args: Array<String>): Map<String, String> {
    var key = ""
    return args.fold(mutableMapOf()) { acc, arg -> acc.apply {
        if (arg.startsWith('-')) {
            key = arg.drop(1)
        } else {
            this[key] = arg
        }
    }}
}

class Main {
    private val matrix = mutableListOf(mutableListOf(mutableListOf<Int>()))
    private var w = 0
    private var h = 0

    companion object {
        const val RED_INDEX = 0
        const val GREEN_INDEX = 1
        const val BLUE_INDEX = 2
    }

    fun run(input: String, output: String) {
        val image = ImageIO.read(File(input))
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

        val energyMap = mutableListOf(mutableListOf<Double>())
        var maxEnergy = 0.0

        for (y in 0 until h) {
            val mapRow = mutableListOf<Double>()
            for (x in 0 until w) {
                val e = sqrt(deltaX2(x, y) + deltaY2(x, y))
                mapRow.add(x, e)
                maxEnergy = max(maxEnergy, e)
            }
            energyMap.add(y, mapRow)
        }

        for (x in 0 until w) {
            for (y in 0 until h) {
                val intensity = (255.0 * energyMap[y][x] / maxEnergy).toInt()
                val color = Color(intensity, intensity, intensity)
                image.setRGB(x, y, color.rgb)
            }
        }

        ImageIO.write(image, "png", File(output))
    }

    // Rx(x, y)^2 + Gx(x, y)^2 + Bx(x, y)^2
    private fun deltaX2(x: Int, y: Int): Double {
        val x1 = if (x == 0) 1 else if (x == w - 1)  x - 1 else x
        return (square(dx(x1, y, RED_INDEX)) +
                square(dx(x1, y, GREEN_INDEX)) +
                square(dx(x1, y, BLUE_INDEX))).toDouble()
    }

    // Ry(x, y)^2 + Gy(x, y)^2 + By(x, y)^2
    private fun deltaY2(x: Int, y: Int): Double {
        val y1 = if (y == 0) 1 else if (y == h - 1) y - 1 else y
        return (square(dy(x, y1, RED_INDEX)) +
                square(dy(x, y1, GREEN_INDEX)) +
                square(dy(x, y1, BLUE_INDEX))).toDouble()
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
