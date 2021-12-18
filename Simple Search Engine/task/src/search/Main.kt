package search
import java.io.File

fun main(args: Array<String>) {
    val people = if (args.size >= 2) {
        if (args[0] == "--data") {
            getPeopleFromFile(args[1])
        } else {
            getPeople()
        }
    } else {
        getPeople()
    }

    val peopleMap = buildMap(people)

    while (true) {
        println()
        printMenu()
        val input = readLine()!!.first()
        println()
        when (input) {
            '1' -> askQuery(peopleMap, people)
            '2' -> printAllPeople(people)
            '0' -> break
            else -> println("Incorrect option! Try again.")
        }
    }

    println("Bye!")

}

fun printAllPeople(people: List<String>) {
    println("=== List of people ===")
    for (person in people) println(person)
}

fun getPeople(): List<String> {
    println("Enter the number of people:")
    val numberOfPeople = readLine()!!.toInt()
    println("Enter all people:")
    val people = mutableListOf<String>()
    for (i in 1..numberOfPeople) {
        people.add(readLine()!!)
    }
    return people
}

fun buildMap(people: List<String>): Map<String, MutableList<Int>> {
    val map = mutableMapOf<String, MutableList<Int>>()
    for (i in people.indices) {
        for (word in people[i].split(Regex("\\s+"))) {
            if (word.lowercase() in map) {
                if (i !in map[word.lowercase()]!!) map[word.lowercase()]!!.add(i)
            } else {
                map[word.lowercase()] = mutableListOf(i)
            }
        }
    }
    return map
}

fun getPeopleFromFile(fileName: String): List<String> {
    val file = File(fileName)
    return file.readLines()
}

fun askQuery(map: Map<String, List<Int>>, people: List<String>) {
    println("Select a matching strategy: ${Strategy.values().joinToString()}")
    val strategyName = readLine()!!.uppercase().trim()
    println()
    println("Enter a name or email to search all matching people.")
    val query = readLine()!!.lowercase().trim().split(Regex("\\s+"))
    when (Strategy.valueOf(strategyName)) {
        Strategy.ALL -> queryAll(map, people, query)
        Strategy.ANY -> queryAny(map, people, query)
        Strategy.NONE -> queryNone(map, people, query)
    }
}

fun queryNone(map: Map<String, List<Int>>, people: List<String>, query: List<String>) {
    val lineFounds = (0..people.lastIndex).toMutableList()
    for (word in query) {
        if (word in map) {
            for (i in map[word]!!) {
                lineFounds.remove(i)
            }
        }
    }
    if (lineFounds.size == 0) {
        println("No matching people found.")
    } else {
        println("${lineFounds.size} people found:")
        for (i in lineFounds) {
            println(people[i])
        }
    }
}

fun queryAny(map: Map<String, List<Int>>, people: List<String>, query: List<String>) {
    val lineFounds = mutableListOf<Int>()
    for (word in query) {
        if (word in map) {
            for (i in map[word]!!) {
                if (i !in lineFounds) lineFounds += i
            }
        }
    }
    if (lineFounds.size == 0) {
        println("No matching people found.")
    } else {
        println("${lineFounds.size} people found:")
        for (i in lineFounds) {
            println(people[i])
        }
    }
}

fun queryAll(map: Map<String, List<Int>>, people: List<String>, query: List<String>) {
    var intersection = emptySet<Int>()
    var flag = true
    for (word in query) {
        if (word !in map) {
            println("No matching people found.")
            return
        } else {
            if (flag) {
                intersection = map[word]!!.toSet()
                flag = false
            } else {
                intersection = map[word]!!.intersect(intersection)
            }
        }
    }
    if (intersection.isEmpty()) {
        println("No matching people found.")
    } else {
        println("${intersection.size} people found:")
        for (i in intersection) {
            println(people[i])
        }
    }
}


fun printMenu() {
    println(
        """
            === Menu ===
            1. Find a person
            2. Print all people
            0. Exit
        """.trimIndent()
    )
}

enum class Strategy {
    ANY,
    ALL,
    NONE;
}