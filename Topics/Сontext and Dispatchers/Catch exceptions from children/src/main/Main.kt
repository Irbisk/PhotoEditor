// Define a proper scope here
// and make sure it doesn't propagate exception
// but prints exception's `.message` to console
// val mainScope =

val handler = CoroutineExceptionHandler { _, exception ->
    println("${exception.message}")
}

val mainScope = CoroutineScope(SupervisorJob() + handler)

suspend fun main() {
    // we load data in the main scope
    // and wait for it to finish explicitly
    // so there is no need to call 'runBlocking'
    // also no need to modify this code
    val job1 = mainScope.loadDataButFail()
    val job2 = mainScope.loadData()
    joinAll(job1, job2)
}