private suspend fun CoroutineScope.loadScreen() {
    // launch pre-caching in background
    launch { preCache("image_3") }
    // load primary data
    loadImage("image_1")
    loadImage("image_2")
}

// this function will be called right after pre-caching starts
private fun stopLoading(scope: CoroutineScope) {
    scope.coroutineContext.cancelChildren()
}