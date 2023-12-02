package day09

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day09"

fun main() {
    fun part1(input: List<String>): Int {

        val heights = input.map { it.toCharArray().map { c -> c.toString().toInt() } }

        var lowestCount = 0
        for (y in heights.indices) {
            for (x in heights[y].indices) {
                val height = heights[y][x]
                if (x >= 1 && heights[y][x - 1] <= height) continue
                if (x < heights[y].size - 1 && heights[y][x + 1] <= height) continue
                if (y >= 1 && heights[y - 1][x] <= height) continue
                if (y < heights.size - 1 && heights[y + 1][x] <= height) continue
                lowestCount += height + 1
            }
        }

        return lowestCount
    }

    fun part2(input: List<String>): Int {

        val basinMap: List<MutableList<Boolean>> = input.map { it.toCharArray().map { c -> c.toString().toInt() != 9 }.toMutableList() }
        val w = basinMap[0].size
        val h = basinMap.size

        data class Point(val y: Int, val x: Int)

        val seeds = mutableListOf<Point>()

        fun findNextSeed(): Point? {
            for (seed in seeds) {
                when {
                    seed.y >= 1 && basinMap[seed.y - 1][seed.x] -> return Point(seed.y - 1, seed.x)
                    seed.y < h - 1 && basinMap[seed.y + 1][seed.x] -> return Point(seed.y + 1, seed.x)
                    seed.x >= 1 && basinMap[seed.y][seed.x - 1] -> return Point(seed.y, seed.x - 1)
                    seed.x < w - 1 && basinMap[seed.y][seed.x + 1] -> return Point(seed.y, seed.x + 1)
                }
            }
            return null
        }

        val basinSizes = mutableListOf<Int>()

        while (basinMap.any { basinLine -> basinLine.any { isBasin -> isBasin } }) {
            val seedY = basinMap.indexOfFirst { basinLine -> basinLine.any { isBasin -> isBasin } }
            val seedX = basinMap[seedY].indexOfFirst { isBasin -> isBasin }
            val sourceSeed = Point(seedY, seedX)
            seeds.add(sourceSeed)
            basinMap[sourceSeed.y][sourceSeed.x] = false
            while (true) {
                val nextSeed = findNextSeed() ?: break
                seeds.add(nextSeed)
                basinMap[nextSeed.y][nextSeed.x] = false
            }
            basinSizes.add(seeds.size)
            seeds.clear()
        }

        return basinSizes.sortedDescending().take(3).reduce { acc, i -> acc * i }
    }

    check(part1(readInput("$FOLDER/test")) == 15)
    check(part2(readInput("$FOLDER/test")) == 1134)

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