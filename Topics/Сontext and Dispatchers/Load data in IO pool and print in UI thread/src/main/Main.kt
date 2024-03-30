fun main() = runBlocking(Dispatchers.Default) {
    // Modify this code to make sure data loading and storing
    // happens in Dispatchers.IO, but data printing in `UiDispatcher`
    // which is already defined for you as a replacement for Dispatchers.Main
    val mainJob = mainScope.launch(Dispatchers.IO) {
        val data = loadData()
        val job = launch(UiDispatcher) {
            printData(data)
        }
        job.join()
        storeData(data)

    }
    mainJob.join()
}