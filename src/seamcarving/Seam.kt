package seamcarving

import java.awt.image.BufferedImage

class Seam(private val image: BufferedImage) {

    private val energyMap = EnergyMap(image).buildEnergyMap()
    private val w = image.width
    private val h = image.height

    fun removeSeam(isVertical: Boolean): BufferedImage {
        val seam = createSeam(isVertical)
        return processImage(seam, isVertical)
    }

    private fun createSeam(isVertical: Boolean): MutableList<Pair<Int, Int>> {
        return if (isVertical)
            buildLowestVerticalSeam(buildLowestVerticalEnergyMatrix())
        else
            buildLowestHorizontalSeam(buildLowestHorizontalEnergyMatrix())
    }

    private fun buildLowestVerticalEnergyMatrix(): Array<DoubleArray> {
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

    private fun buildLowestHorizontalEnergyMatrix(): Array<DoubleArray> {
        val lowestEnergyMatrix = energyMap.copyOf()

        for (x in 1 until w) {
            for (y in 0 until h) {
                lowestEnergyMatrix[y][x] += lowestLeft(x, y)
            }
        }

        return lowestEnergyMatrix
    }

    private fun lowestLeft(x: Int, y: Int): Double {
        return listOf(left(x - 1, y - 1), left(x - 1, y), left(x - 1, y + 1)).minOf { it }
    }

    private fun left(x: Int, y: Int): Double {
        return if (y in 0 until h) {
            energyMap[y][x]
        } else {
            Double.MAX_VALUE
        }
    }

    private fun buildLowestVerticalSeam(lowestEnergyMatrix: Array<DoubleArray>): MutableList<Pair<Int, Int>> {
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
        if (x < w - 1) threeAbove.add(row[x + 1]) else threeAbove.add(Double.MAX_VALUE)

        return threeAbove
    }

    private fun buildLowestHorizontalSeam(lowestEnergyMatrix: Array<DoubleArray>): MutableList<Pair<Int, Int>> {
        val lowestSeam = mutableListOf<Pair<Int, Int>>()

        // Find the index of the lowest energy on the rightmost row
        val rightmostColumn = lowestEnergyMatrix.map { it[w - 1] }.toList()
        val indexOfLeast = rightmostColumn.withIndex().minByOrNull { it.value }!!.index
        lowestSeam.add(w - 1 to indexOfLeast)

        // Work back to leftmost by lowest energy
        var y1 = indexOfLeast
        var x1 = w - 2

        while (x1 >= 0) {
            val threeLeftward = listOfLeft(lowestEnergyMatrix.map { it[x1] }, y1)
            val indexOfLowest = threeLeftward.withIndex().minByOrNull { it.value }!!.index
            y1 += indexOfLowest - 1
            lowestSeam.add(0, x1 to y1)
            x1--
        }

        return lowestSeam
    }

    private fun listOfLeft(column: List<Double>, y: Int): List<Double> {
        val threeToLeft = mutableListOf<Double>()
        if (y > 0) threeToLeft.add(column[y - 1]) else threeToLeft.add(Double.MAX_VALUE)
        threeToLeft.add(column[y])
        if (y < h - 1) threeToLeft.add(column[y + 1]) else threeToLeft.add(Double.MAX_VALUE)

        return threeToLeft
    }

    private fun processImage(seam: List<Pair<Int, Int>>, isVertical: Boolean): BufferedImage {
        return if (isVertical) {
            processImageVertically(seam)
        } else {
            processImageHorizontally(seam)
        }
    }

    private fun processImageVertically(seam: List<Pair<Int, Int>>): BufferedImage {
        val newImage = BufferedImage(w - 1, h, BufferedImage.TYPE_INT_ARGB)

        for (coordinates in seam) {
            val y = coordinates.second
            var offset = 0
            for (x in 0 until w - 1) {
                if (x == coordinates.first) {
                    offset = 1
                }
                val color = image.getRGB(x + offset, y)
                newImage.setRGB(x, y, color)
            }
        }

        return newImage
    }

    private fun processImageHorizontally(seam: List<Pair<Int, Int>>): BufferedImage {
        val newImage = BufferedImage(w, h - 1, BufferedImage.TYPE_INT_ARGB)

        for (coordinates in seam) {
            val x = coordinates.first
            var offset = 0
            for (y in 0 until h - 1) {
                if (y == coordinates.second) {
                    offset = 1
                }
                val color = image.getRGB(x, y + offset)
                newImage.setRGB(x, y, color)
            }
        }

        return newImage
    }
}
