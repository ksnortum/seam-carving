package seamcarving

import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val argsMap = getArgsMap(args)
    val input = argsMap["in"] ?: throw IllegalArgumentException("'in' not found")
    val output = argsMap["out"] ?: throw  IllegalArgumentException("'out' not found")
    Seam(input, output, isVertical = false).processSeam()
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
