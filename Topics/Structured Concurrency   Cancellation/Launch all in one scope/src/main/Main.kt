private suspend fun loadScreenInSomeScope() = coroutineScope {

    launch { loadImage("image_1") }
    launch { loadImage("image_2") }
    launch { preCache("image_3") }
}