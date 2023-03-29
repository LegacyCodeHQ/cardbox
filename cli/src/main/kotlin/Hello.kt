import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
  val filenameWithoutExtension = "04"
  val inputImageFile = File("$filenameWithoutExtension.png")
  val inputImage = ImageIO.read(inputImageFile)
  val bgColor = getBackgroundColor(inputImage)
  val boundingBox = getBoundingBox(inputImage, bgColor)
  val croppedImage = cropImage(inputImage, bgColor, boundingBox, 1400, 900)
  val outputImageFile = File("$filenameWithoutExtension-out.png")
  ImageIO.write(croppedImage, "png", outputImageFile)
}

fun getBackgroundColor(image: BufferedImage): Color {
  // Count the frequency of each color in the image
  val colorFreq = mutableMapOf<Color, Int>()
  for (x in 0 until image.width) {
    for (y in 0 until image.height) {
      val color = Color(image.getRGB(x, y))
      colorFreq[color] = colorFreq.getOrDefault(color, 0) + 1
    }
  }
  // Find the color with the highest frequency (background color)
  return colorFreq.maxByOrNull { it.value }!!.key
}

fun getBoundingBox(image: BufferedImage, bgColor: Color): BoundingBox {
  var x1 = Int.MAX_VALUE
  var y1 = Int.MAX_VALUE
  var x2 = Int.MIN_VALUE
  var y2 = Int.MIN_VALUE
  for (x in 0 until image.width) {
    for (y in 0 until image.height) {
      if (Color(image.getRGB(x, y)) != bgColor) {
        x1 = minOf(x1, x)
        y1 = minOf(y1, y)
        x2 = maxOf(x2, x)
        y2 = maxOf(y2, y)
      }
    }
  }
  return BoundingBox(x1, y1, x2 - x1 + 1, y2 - y1 + 1)
}

fun cropImage(
  image: BufferedImage,
  bgColor: Color,
  boundingBox: BoundingBox,
  desiredWidth: Int,
  desiredHeight: Int,
): BufferedImage {
  val croppedImage = BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_RGB)
  val g2d = croppedImage.createGraphics()
  g2d.color = bgColor
  g2d.fillRect(0, 0, croppedImage.width, croppedImage.height)
  val xOffset = (desiredWidth - boundingBox.width) / 2
  val yOffset = (desiredHeight - boundingBox.height) / 2
  val x1 = maxOf(0, boundingBox.x - xOffset)
  val y1 = maxOf(0, boundingBox.y - yOffset)
  val x2 = minOf(boundingBox.x + boundingBox.width + xOffset, image.width)
  val y2 = minOf(boundingBox.y + boundingBox.height + yOffset, image.height)
  val graph = image.getSubimage(x1, y1, x2 - x1, y2 - y1)
  g2d.drawImage(graph, xOffset - (boundingBox.x - x1), yOffset - (boundingBox.y - y1), null)
  g2d.dispose()
  return croppedImage
}

data class BoundingBox(
  val x: Int,
  val y: Int,
  val width: Int,
  val height: Int,
)
