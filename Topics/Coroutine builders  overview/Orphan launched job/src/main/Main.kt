fun doAllTheJob() {
    GlobalScope.launch {
        connectToServer()
    }
}