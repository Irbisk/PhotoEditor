

suspend fun workingWithDb(): Unit = coroutineScope {
    val timeInMillis = measureTimeMillis {
        connectToDb()
        fetchData()
    }

    if (timeInMillis > 100) {
        // write your code here
        println("I'm tired of waiting! Closing the connection...")
        this.coroutineContext.cancel()
    }
}