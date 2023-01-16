package seamcarving

import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val argsMap = getArgsMap(args)
    val inputFileName = argsMap["in"] ?: throw IllegalArgumentException("'in' not found")
    val outputFileName = argsMap["out"] ?: throw IllegalArgumentException("'out' not found")
    val width = argsMap["width"] ?: throw IllegalArgumentException("'width' not found")
    val height = argsMap["height"] ?: throw IllegalArgumentException("'height' not found")
    ResizableImage(inputFileName).resize(width.toInt(), height.toInt(), outputFileName)
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
