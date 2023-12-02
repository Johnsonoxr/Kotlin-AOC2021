package day01

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day01"

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.toInt() }
            .windowed(size = 2, step = 1)
            .map { (a, b) -> if (a < b) 1 else 0 }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .map { it.toInt() }
            .windowed(size = 3, step = 1)
            .map { it.sum() }
            .windowed(size = 2, step = 1)
            .map { (a, b) -> if (a < b) 1 else 0 }
            .sum()
    }

    check(part1(readInput("${FOLDER}/test")) == 7)
    check(part2(readInput("${FOLDER}/test")) == 5)

    val input = readInput("${FOLDER}/input")
    val part1Time = measureNanoTime {
        part1(input).println()
    }
    val part2Time = measureNanoTime {
        part2(input).println()
    }

    println("Part 1 takes ${part1Time / 1e6f} milliseconds.")
    println("Part 2 takes ${part2Time / 1e6f} milliseconds.")
}
