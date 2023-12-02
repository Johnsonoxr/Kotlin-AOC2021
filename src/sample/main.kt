package sample

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "sample"

fun main() {
    fun part1(input: List<String>): Int {
        return 1
    }

    fun part2(input: List<String>): Int {
        return 1
    }

    check(part1(readInput("$FOLDER/test")) == 1)
    check(part2(readInput("$FOLDER/test")) == 1)

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