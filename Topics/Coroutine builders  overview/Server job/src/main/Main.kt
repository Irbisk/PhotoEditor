fun doAllTheJob() {
    // put your code here
    // use suspending functions
    // connectToServer() and loadData()
    runBlocking {
        connectToServer()
        loadData()
    }
}