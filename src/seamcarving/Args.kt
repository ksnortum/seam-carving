package seamcarving

import java.io.File

class Args(private val args: Array<String>) {
    private val slash = File.separator

    fun parseArgs(): Map<String, String> {
        val argsMap = buildArgsMap()
        argsMap["in"] = getInputFile(argsMap)
        argsMap["out"] = getOutputFile(argsMap)

        while (true) {
            argsMap["width"] = getWidthHeight(argsMap, "width")
            argsMap["height"] = getWidthHeight(argsMap, "height")
            val width = argsMap["width"]?.toInt() ?: 0
            val height = argsMap["height"]?.toInt() ?: 0

            if (width > 0 || height > 0) break

            println("Width and height reduction cannot both be zero")
            argsMap.remove("width") // Signal that width and height need to be manually entered
            argsMap.remove("height")
        }

        return argsMap
    }

    private fun buildArgsMap(): MutableMap<String, String> {
        var key = ""
        return args.fold(mutableMapOf()) { acc, arg ->
            acc.apply {
                if (arg.startsWith('-')) {
                    key = arg.drop(1)
                } else {
                    this[key] = arg
                }
            }
        }
    }

    private fun getInputFile(argsMap: MutableMap<String, String>): String {
        val prompt = "Enter the path to the file to resize: "
        var inputFile = argsMap["in"] ?: promptForString(prompt)
        var file = File(inputFile)

        while (!file.exists()) {
            println("File name does not exist")
            inputFile = promptForString(prompt)
            file = File(inputFile)
        }

        return inputFile
    }

    private fun getOutputFile(argsMap: MutableMap<String, String>): String {
        var outputFile = argsMap["out"] ?: ""
        var defaultFileName = outputFile

        if (outputFile.isBlank()) {
            val inputFile = argsMap["in"] ?: ""
            val parent = inputFile.substringBeforeLast(slash)
            val fileName = inputFile.substringAfterLast(slash)
            val fileNameNoExt = fileName.substringBeforeLast(".")
            var ext = fileName.substringAfterLast(".", "")
            ext = if (ext.isBlank()) "" else ".$ext"
            defaultFileName = "$parent$slash${fileNameNoExt}-resized$ext"
            outputFile = promptForString("Enter output file name, <Enter> = $defaultFileName: ")
        }

        return outputFile.ifBlank { defaultFileName }
    }

    private fun getWidthHeight(argsMap: MutableMap<String, String>, type: String): String {
        val prompt = "Enter the amount to reduce the $type by or 0 for no $type reduction: "
        val numberText = argsMap[type] ?: promptForString(prompt)

        var number = try {
            numberText.toInt()
        } catch (e: NumberFormatException) {
            println("Reduction in $type is not a valid integer")
            promptForInt(prompt)
        }

        while (number < 0) {
            println("Reduction in $type must be a non-negative integer")
            number = promptForInt(prompt)
        }

        return number.toString()
    }

    private fun promptForString(prompt: String): String {
        print(prompt)
        return readln()
    }

    private fun promptForInt(prompt: String): Int {
        var int = 0
        var notAValidNumber = true

        while (notAValidNumber) {
            print(prompt)
            val input = readln()

            if (input.isBlank()) {
                return 0
            }

            try {
                int = input.toInt()
                notAValidNumber = false
            } catch (e: NumberFormatException) {
                println("Not a valid integer")
            }
        }

        return int
    }
}

