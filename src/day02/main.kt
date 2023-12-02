package day02

import println
import readInput
import kotlin.system.measureNanoTime

private val FOLDER = "day02"

fun main() {

    data class Direction(val x: Int, val y: Int)

    fun parseDirection(line: String): Direction {
        val step = "[0-9]+".toRegex().find(line)?.value?.toInt() ?: 0
        return when {
            "forward" in line -> Direction(step, 0)
            "up" in line -> Direction(0, -step)
            "down" in line -> Direction(0, step)
            else -> throw IllegalArgumentException("Unknown direction: $line")
        }
    }

    fun part1(input: List<String>): Int {

        val directions = input.map { parseDirection(it) }

        val horizontalShift = directions.filter { it.x != 0 }.sumOf { it.x }
        val verticalShift = directions.filter { it.y != 0 }.sumOf { it.y }

        return horizontalShift * verticalShift
    }

    fun part2(input: List<String>): Int {

        val directions = input.map { parseDirection(it) }

        var aim = 0
        var horizontalShift = 0
        var depth = 0
        directions.forEach { direction ->
            aim += direction.y
            horizontalShift += direction.x
            depth += direction.x * aim
        }

        return horizontalShift * depth
    }

    check(part1(readInput("$FOLDER/test")) == 150)
    check(part2(readInput("$FOLDER/test")) == 900)

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
