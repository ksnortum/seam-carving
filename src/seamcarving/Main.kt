package seamcarving

import java.awt.Color
import java.io.File
import java.lang.IllegalArgumentException
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

const val RED_INDEX = 0
const val GREEN_INDEX = 1
const val BLUE_INDEX = 2

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

        // println(matrix[0]) // TODO testing
        // println(matrix[0].size) // TODO testing

        val energyMap = mutableListOf(mutableListOf<Double>())
        var maxEnergy = 0.0

        for (y in 0 until h) {
            val mapRow = mutableListOf<Double>()
            for (x in 0 until w) {
                val e: Double
                if (x == 0) {
                    e = if (y == 0) { // NW corner
                        sqrt(deltaX2(1, 0) + deltaY2(0, 1))
                    } else if (y == h - 1) { // SW corner
                        sqrt(deltaX2(1, y) + deltaY2(0, y - 1))
                    } else { // west border
                        sqrt(deltaX2(1, y) + deltaY2(0, y))
                    }
                } else if (y == 0) { // x != 0
                    e = if (x == w - 1) { // NE corner
                        sqrt(deltaX2(x - 1, 0) + deltaY2(x, 1))
                    } else { // north border
                        sqrt(deltaX2(x, 0) + deltaY2(x, 1))
                    }
                } else { // x != 0 and y != 0
                    e = if (x == w - 1) { // east border
                        sqrt(deltaX2(x - 1, y) + deltaY2(x, y))
                    } else if (y == h - 1) { // south border
                        sqrt(deltaX2(x, y - 1) + deltaY2(x, y))
                    } else if (x == w - 1 && y == h - 1) { // SE corner
                        sqrt(deltaX2(x - 1, y) + deltaY2(x, y - 1))
                    } else { // all others
                        energy(x, y)
                    }
                }
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

    private fun energy(x: Int, y: Int): Double = sqrt(deltaX2(x, y) + deltaY2(x, y))

    private fun deltaX2(x: Int, y: Int): Double {

        return (square(dx(x, y, RED_INDEX)) + square(dx(x, y, GREEN_INDEX)) + square(dx(x, y, BLUE_INDEX))).toDouble()
    }

    private fun deltaY2(x: Int, y: Int): Double {
        return (square(dy(x, y, RED_INDEX)) + square(dy(x, y, GREEN_INDEX)) + square(dy(x, y, BLUE_INDEX))).toDouble()
    }

    private fun dx(x: Int, y: Int, color: Int): Int {
        // println("x = $x, y = $y, w = $w, h = $h") // TODO testing
        // return matrix[y][max(x - 1, 0)][color] - matrix[y][min(x + 1, w - 1)][color]
        return matrix[y][x - 1][color] - matrix[y][x + 1][color]

    }

    private fun dy(x: Int, y: Int, color: Int): Int {
        // return matrix[max(y - 1, 0)][x][color] - matrix[min(y + 1, h - 1)][x][color]
        return matrix[y - 1][x][color] - matrix[y + 1][x][color]

    }

    private fun square(x: Int): Int = x * x
}
