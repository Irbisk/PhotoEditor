// transform given intFlow into a new one according to the task
val flow = intFlow.filter { it % 2 == 0 }.filter { it > 99 }.take(10).map { it.toString(16) }