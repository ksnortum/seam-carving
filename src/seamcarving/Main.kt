package seamcarving

import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val argsMap = Args(args).parseArgs()
    val inputFileName = argsMap["in"] ?: throw IllegalArgumentException("'in' not found")
    val outputFileName = argsMap["out"] ?: throw IllegalArgumentException("'out' not found")
    val width = argsMap["width"] ?: throw IllegalArgumentException("'width' not found")
    val height = argsMap["height"] ?: throw IllegalArgumentException("'height' not found")
    println("Processing...")
    ResizableImage(inputFileName).resize(width.toInt(), height.toInt(), outputFileName)
    println("Finished")
}
