package day12

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day12"

fun main() {

    fun String.isBig() = this[0].isUpperCase()

    fun getCaveSystem(input: List<String>): Map<String, List<String>> {
        val caveSystem = mutableMapOf<String, MutableList<String>>()
        for (line in input) {
            val (cave, anotherCave) = line.split("-")
            if (anotherCave != "start") {
                caveSystem.putIfAbsent(cave, mutableListOf(anotherCave))?.add(anotherCave)
            }
            if (cave != "start") {
                caveSystem.putIfAbsent(anotherCave, mutableListOf(cave))?.add(cave)
            }
        }
        return caveSystem
    }

    fun part1(input: List<String>): Int {
        val caveSystem = getCaveSystem(input)

        val paths = mutableListOf<List<String>>()

        fun findPaths(path: List<String>) {
            val currentCave = path.last()
            if (currentCave == "end") {
                paths.add(path)
                return
            }
            val nextCaves = caveSystem[currentCave]!!.filter { cave -> cave.isBig() || cave !in path }
            for (nextCave in nextCaves) {
                findPaths(path + nextCave)
            }
        }

        findPaths(listOf("start"))

        return paths.size
    }

    fun part2(input: List<String>): Int {
        val caveSystem = getCaveSystem(input)

        val paths = mutableListOf<List<String>>()

        fun findPaths(path: List<String>) {
            val currentCave = path.last()
            if (currentCave == "end") {
                paths.add(path)
                return
            }
            val nextCaves = caveSystem[currentCave]!!.filter { cave ->
                cave != "start" && (cave.isBig() || cave !in path || path.filter { !it.isBig() }.let { it.size == it.distinct().size })
            }
            for (nextCave in nextCaves) {
                findPaths(path + nextCave)
            }
        }

        findPaths(listOf("start"))

        return paths.size
    }

    check(part1(readInput("$FOLDER/test")) == 10)
    check(part2(readInput("$FOLDER/test")) == 36)

    val input = readInput("$FOLDER/input")
    val part1Time = measureNanoTime {
        part1(input).println()
    }
    val part2Time = measureNanoTime {
        part2(input).println()
    }

    println("Part 1 takes ${part1Time / 1e6f} milliseconds.")
    println("Part 2 takes ${part2Time / 1e6f} milliseconds.")
}