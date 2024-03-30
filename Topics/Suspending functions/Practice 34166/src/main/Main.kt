import java.lang.Thread

suspend fun bakeCake() {
    println("I'm a cyber baker. I'm starting to bake fast!")
    delaySeconds(5)
    println("The cake is ready!")
}

// Please change only the "main" function:
suspend fun main() {
    for (i in 1..3) {
        bakeCake()
    }

}