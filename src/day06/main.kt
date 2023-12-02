package day06

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day06"

fun main() {

    fun part1(input: List<String>): Int {
        val countDownMap = mutableMapOf<Int, Int>()
        (0..8).forEach { c ->
            countDownMap[c] = 0
        }

        val countDownList = input.first().split(",").map { it.toInt() }
        countDownList.forEach { countDown ->
            countDownMap[countDown] = countDownMap[countDown]!! + 1
        }

        repeat(80) {
            (0..8).forEach { c ->
                countDownMap[c - 1] = countDownMap.getOrElse(c) { 0 }
            }
            countDownMap[8] = 0
            countDownMap[6] = countDownMap.getOrElse(6) { 0 } + countDownMap.getOrElse(-1) { 0 }
            countDownMap[8] = countDownMap.getOrElse(-1) { 0 }
            countDownMap[-1] = 0
        }

        return countDownMap.values.sum()
    }

    fun part2(input: List<String>): Long {
        val countDownMap = mutableMapOf<Int, Long>()
        (0..8).forEach { c ->
            countDownMap[c] = 0
        }

        val countDownList = input.first().split(",").map { it.toInt() }
        countDownList.forEach { countDown ->
            countDownMap[countDown] = countDownMap[countDown]!! + 1
        }

        repeat(256) {
            (0..8).forEach { c ->
                countDownMap[c - 1] = countDownMap.getOrElse(c) { 0 }
            }
            countDownMap[8] = 0
            countDownMap[6] = countDownMap.getOrElse(6) { 0 } + countDownMap.getOrElse(-1) { 0 }
            countDownMap[8] = countDownMap.getOrElse(-1) { 0 }
            countDownMap[-1] = 0
        }

        return countDownMap.values.sum()
    }

    check(part1(readInput("$FOLDER/test")) == 5934)
    check(part2(readInput("$FOLDER/test")) == 26984457539L)

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