package day11

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day11"

fun main() {

    data class Point(val y: Int, val x: Int)

    val neighbors = listOf(
        Point(-1, -1),
        Point(-1, 0),
        Point(-1, 1),
        Point(0, -1),
        Point(0, 1),
        Point(1, -1),
        Point(1, 0),
        Point(1, 1)
    )

    fun part1(input: List<String>): Int {

        val energyMap: List<MutableList<Int>> = input.map { line -> line.toCharArray().map { it.toString().toInt() }.toMutableList() }

        var flashCount = 0
        repeat(100) {
            for (y in energyMap.indices) {
                for (x in energyMap[y].indices) {
                    energyMap[y][x] += 1
                }
            }

            val flashingMap = List(10) { MutableList(10) { false } }

            var flashMapChanged: Boolean
            do {
                flashMapChanged = false
                for (y in energyMap.indices) {
                    for (x in energyMap[y].indices) {
                        if (flashingMap[y][x] || energyMap[y][x] <= 9) {
                            continue
                        }
                        energyMap[y][x] = 0
                        flashingMap[y][x] = true
                        flashMapChanged = true
                        flashCount += 1

                        for (neighbor in neighbors) {
                            val neighborY = y + neighbor.y
                            val neighborX = x + neighbor.x
                            if (neighborY in energyMap.indices && neighborX in energyMap[y].indices && !flashingMap[neighborY][neighborX]) {
                                energyMap[neighborY][neighborX] += 1
                            }
                        }
                    }
                }
            } while (flashMapChanged)
        }

        return flashCount
    }

    fun part2(input: List<String>): Int {
        val energyMap: List<MutableList<Int>> = input.map { line -> line.toCharArray().map { it.toString().toInt() }.toMutableList() }

        repeat(Int.MAX_VALUE) { step ->
            for (y in energyMap.indices) {
                for (x in energyMap[y].indices) {
                    energyMap[y][x] += 1
                }
            }

            val flashingMap = List(10) { MutableList(10) { false } }

            var flashCount = 0
            var flashMapChanged: Boolean
            do {
                flashMapChanged = false
                for (y in energyMap.indices) {
                    for (x in energyMap[y].indices) {
                        if (flashingMap[y][x] || energyMap[y][x] <= 9) {
                            continue
                        }
                        energyMap[y][x] = 0
                        flashingMap[y][x] = true
                        flashMapChanged = true
                        flashCount += 1

                        for (neighbor in neighbors) {
                            val neighborY = y + neighbor.y
                            val neighborX = x + neighbor.x
                            if (neighborY in energyMap.indices && neighborX in energyMap[y].indices && !flashingMap[neighborY][neighborX]) {
                                energyMap[neighborY][neighborX] += 1
                            }
                        }
                    }
                }
            } while (flashMapChanged)

            if (flashCount == 100) {
                return step + 1
            }
        }
        return 0
    }

    check(part1(readInput("$FOLDER/test")) == 1656)
    check(part2(readInput("$FOLDER/test")) == 195)

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