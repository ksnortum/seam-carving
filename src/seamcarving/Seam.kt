package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.sqrt

class Seam {
    private val matrix = mutableListOf(mutableListOf(mutableListOf<Int>()))
    private lateinit var energyMap: Array<DoubleArray> // = arrayOf<DoubleArray>()
    private var w = 0
    private var h = 0

    companion object {
        const val RED_INDEX = 0
        const val GREEN_INDEX = 1
        const val BLUE_INDEX = 2

        const val UNDEFINED = -1
    }

    fun findSeam(input: String, output: String) {
        val image = ImageIO.read(File(input))
        buildMatrix(image) // sets w and h
        buildEnergyMap()
        val seam = createSeam()
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

    private fun createSeam(): MutableList<Pair<Int, Int>> {
        val source = 0
        val target = w * (h + 2) - 1
        val previous = dijkstra(source, target)
        val seam = mutableListOf<Pair<Int, Int>>() // S (sequence)
        var u = target
        var x = u % w
        var y = u / w

        if (previous[u] != UNDEFINED || u == source) {
            while (u != UNDEFINED) {
                // no imaginary top and bottom row
                if (y in 1 until h - 1) {
                    // y is offset by 1 because there is an imaginary "zero row"
                    seam.add(0, x to y - 1)
                }
                u = previous[u]
                x = u % w
                y = u / w
            }
        }

        println("Out of createSeam()") // TODO testing

        return seam
    }

    // TODO used energyMap needs zero on top and bottom
    private fun dijkstra(source: Int, target: Int): IntArray {
        val size = w * (h + 2) // zeros top and bottom
        val distance = DoubleArray(size) { Double.MAX_VALUE }  // dist[]
        distance[source] = 0.0
        val previous = IntArray(size) { UNDEFINED }  // prev[]
        // val vertices = IntArray(size) // Q
        val vertices = mutableListOf<Int>()
        for (i in 0 until size) vertices.add(i)

        while (vertices.isNotEmpty()) {
            val u = vertexWithMinDistance(vertices, distance)
            if (u == target) break // end early if target
            vertices.removeAt(u)
            // print(if (vertices.size % 1000 == 0) "." else "") // TODO testing

            for (v in neighbors(u)) {
                val x = v % w
                val y = v / w
                // energyMap has imaginary zeros top and bottom
                val alt = if (y == 0 || y == h + 1) 0.0 else distance[u] + energyMap[y - 1][x]

                if (alt < distance[v]) {
                    distance[v] = alt
                    previous[v] = u
                }
            }
        }

        println("Out of dijkstra()") // TODO testing

        return previous
    }

    private fun vertexWithMinDistance(vertices: List<Int>, distance: DoubleArray): Int {
        var lowestDistanceIndex = 0
        var lowestDistance = Double.MAX_VALUE

        for (u in vertices) {
            if (distance[u] < lowestDistance) {
                lowestDistanceIndex = u
                lowestDistance = distance[u]
            }
        }

        // println("lowestDistanceIndex = $lowestDistanceIndex") // TODO testing

        return lowestDistanceIndex
    }

    // energyMap has imaginary zeros on top and bottom
    private fun neighbors(u: Int): List<Int> {
        val neighbors = mutableListOf<Int>()
        val x = u % w
        val y = u / w
        if (x < w && (y == 0 || y == h + 1)) neighbors.add(u + 1) // sideways on top and bottom
        val u1 = u + w // row below

        if (u1 / w < h + 1) { // not on bottom row
            neighbors.add(u1 + w) // center
            if (x > 0) neighbors.add(u1 + w - 1) // left
            if (x < w) neighbors.add(u1 + w + 1) // right
        }

        // println("neighbors = $neighbors") // TODO testing

        return neighbors
    }

//    private fun createSeam(): MutableList<Pair<Int, Int>> {
//        // Go across row zero and find the lowest energy path
//        var lowestSeam = mutableListOf<Pair<Int, Int>>()
//        var lowestEnergy = Double.MAX_VALUE
//
//        for (x in 0 until w) {
//            val seam = mutableListOf<Pair<Int, Int>>()
//            val energy = followLeast(seam, x, 0)
//
//            if (energy < lowestEnergy) {
//                lowestSeam = seam
//                lowestEnergy = energy
//            }
//        }
//
//        print("lowestEnergy = $lowestEnergy, lowestSeam total = ") // TODO testing
//        println(lowestSeam.fold(0.0) { total, item -> total + energyMap[item.second][item.first] }) // TODO testing
//        return lowestSeam
//    }

//    private fun followLeast(seam: MutableList<Pair<Int, Int>>, x: Int, y: Int): Double {
//        seam.add(Pair(x, y))
//        val energy = energyMap[y][x]
//
//        if (y == h - 1) {
//            return energy
//        }
//
//        val y1 = y + 1
//        val energies = listOf(below(x - 1, y1), below(x, y1), below(x + 1, y1))
//        // Index of which of the three energies is lowest
//        val energyIndex = energies.withIndex().minByOrNull { it.value }?.index ?: throw NullPointerException()
//        val x1 = x + energyIndex - 1
//
//        return energy + followLeast(seam, x1, y1)
//    }

//    private fun below(x: Int, y: Int): Double {
//        return if (x < 0 || x >= w) {
//            Double.MAX_VALUE // OOB is never the least
//        } else {
//            energyMap[y][x]
//        }
//    }

    private fun processImage(image: BufferedImage, seam: List<Pair<Int, Int>>) {
        for (coordinates in seam) {
            // println(coordinates) // TODO testing
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