# seam-carving
This is a school [project from HyperSkill](https://hyperskill.org/projects/100) that uses seam carving to resize an 
image.  The image is shrunk without ratio distortion or cropping.  The app has been tested on PNG, JPG, and GIF files,
but may work on other image types.  Only shrinking the image is supported.

### Execution
The easiest way to run the app is to use an IDE that handles Kotlin, such as IntelliJ IDEA.  If you want to compile
the app, then follow the instructions below.

You will need to download Java and the Kotlin compiler from the [JetBrains website](https://github/JetBrains/kotlin/releases).
Note that you want the `kotlin-compiler-x.y.z.zip` file, *not* any of the kotlin-native files.

Running the app requires compiling, creating a jar file, then executing it in Java.  See [this link](
https://kotlinlang.org/docs/command-line.html) for details, but this should work (use backslashes in Windows):

    kotlinc -include-runtime -d seamcarving.jar src/seamcarving/*.kt
    java -jar seamcarving -in <filename> -out <filename> -width <reduce width by> -height <reduce height by>

The app takes four arguments, all optional:

* `-in` the file name of the image to resize
* `-out` the file name of the new, resized image
* `-width` how much the width of the image should be shrunk by
* `-height` how much the height of the image should be shrunk by

### CLI
All command line options are optional and missing arguments will be prompted for.  The input file is tested to make sure
it exists.  The output file will overwrite an existing file without warning.  If you don't want to change a dimension,
set it to zero (for instance `-width 0`) but the dimensions cannot both be zero. 

