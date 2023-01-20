# seam-carving
This is a school [project from HyperSkill](https://hyperskill.org/projects/100) that uses seam carving to resize an 
image.  The image is shrunk without ratio distortion or cropping.  The app currently works only on PNG files and 
shrinking the image.

### Execution

Running the app requires compiling, creating a jar file, then executing it in Java.  See [this link](
https://kotlinlang.org/docs/command-line.html) for details, but this should work:

    kotlinc -include-runtime -d seamcarving.jar src/seamcarving/*.kt
    java -jar seamcarving -in <filename> -out <filename> -width <reduce width by> -height <reduce height by>

The app takes four options:

* `-in` the file name of the image to resize
* `-out` the file name of the new, resized image
* `-width` how much the width of the image should be shrunk by
* `-height` how much the height of the image should be shrunk by

### CLI
All command line options are optional and missing options will be prompted for.  The input file is tested to make sure
it exists.  The output file will overwrite an existing file without warning.  If you don't want to change a dimension,
set it to zero (for instance `-width 0`) but the dimensions cannot both be zero. 

### TODO
* use other file formats
