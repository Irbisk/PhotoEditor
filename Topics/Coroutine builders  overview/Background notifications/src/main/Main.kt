import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun doAllTheJob() {
    // put your code here
    // use suspending functions
    //  and loadData()
    GlobalScope.launch {
        printProgress()
    }
    runBlocking {
        loadData()
    }

}