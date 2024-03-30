suspend fun walk(meters: Double) {
    println("Walking $meters meters. Feel free to dream about something!")
    delaySeconds(meters.toLong())
}

suspend fun waitInQueue() {
    println("Staying in the queue. Feel free to dream about something!")
    waitForTurn()
}

// Please change only the "main" function:
suspend fun main() {
    walk(352.0)
    waitInQueue()
    walk(120.0)

}