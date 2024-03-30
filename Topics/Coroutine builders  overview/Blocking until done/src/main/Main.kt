fun validateConnection() {
    runBlocking {

        connectToServer() // suspending function that doen't compile now
    }
}