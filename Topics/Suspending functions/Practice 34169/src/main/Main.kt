// change this function:
suspend fun logImageLoading(url: String): Image {
    val image = loadImage(url)
    println("Image loaded!")
    return image
}