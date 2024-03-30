// that's the main scope of our function that you need to set

    val scope = CoroutineScope(SupervisorJob())



private suspend fun CoroutineScope.loadScreen() {
    val requiredJob1 = launch { loadImage("image_1") } // start required operation
    launch { runUnstableOptionalJob() } // start optional operation
    joinAll(requiredJob1) // wait for required operation to finish
}