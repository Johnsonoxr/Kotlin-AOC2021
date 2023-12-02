package day07

import println
import readInput
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.system.measureNanoTime

private const val FOLDER = "day07"

fun main() {
    fun part1(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        var center = 0
        var fuel = positions.sumOf { abs(it - 0) }

        while (true) {
            val newFuel = positions.sumOf { abs(it - center) }
            if (newFuel > fuel) {
                break
            }
            fuel = newFuel
            center++
        }

        return fuel
    }

    fun part2(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        var center = 0
        var fuel = positions.sumOf { abs(it - 0).let { n -> n * (n + 1) / 2 } }

        while (true) {
            val newFuel = positions.sumOf { abs(it - center).let { n -> n * (n + 1) / 2 } }
            if (newFuel > fuel) {
                break
            }
            fuel = newFuel
            center++
        }

        return fuel
    }

    check(part1(readInput("$FOLDER/test")) == 37)
    check(part2(readInput("$FOLDER/test")) == 168)

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