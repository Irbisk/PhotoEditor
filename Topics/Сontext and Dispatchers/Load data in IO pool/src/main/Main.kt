fun main() = runBlocking(Dispatchers.Default) {
    println("Started in " + coroutineContext[CoroutineDispatcher])

    // `loadData()` function is already defined for you
    // make it run in an appropriate thread pool here
    // don't modify the prints
    withContext(Dispatchers.IO) { // jumps to IO thread pool
        loadData()
    }

    println("Finished in " + coroutineContext[CoroutineDispatcher])
}