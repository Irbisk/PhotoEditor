fun doAllTheJob() {
    println("Working...")

    val firstJob = runBlocking {
        doLotsOfWork1() // 5 ms 
    }

    val secondjob = runBlocking {
        doLotsOfWork2() // 5 ms 
    }

    println("Finishing")
}