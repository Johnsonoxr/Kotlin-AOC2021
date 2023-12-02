package day05

import println
import readInput
import kotlin.math.absoluteValue
import kotlin.system.measureNanoTime

private const val FOLDER = "day05"

fun main() {

    data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int)
    data class Point(val x: Int, val y: Int)

    val regex = "[0-9]+".toRegex()
    fun parseLine(line: String): Line {
        val (x1, y1, x2, y2) = regex.findAll(line).map { it.value }.toList()
        return Line(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
    }

    fun part1(input: List<String>): Int {

        fun Line.isValid(): Boolean {
            return x1 == x2 || y1 == y2
        }

        fun Line.toPoints(): List<Point> {
            val points = mutableListOf<Point>()
            if (x1 == x2) {
                val largeY = maxOf(y1, y2)
                val smallY = minOf(y1, y2)
                for (y in smallY..largeY) {
                    points.add(Point(x1, y))
                }
            } else {
                val largeX = maxOf(x1, x2)
                val smallX = minOf(x1, x2)
                for (x in smallX..largeX) {
                    points.add(Point(x, y1))
                }
            }
            return points
        }

        val lines = input.map { parseLine(it) }.filter { it.isValid() }
        val points = lines.map { it.toPoints() }.flatten()
        return points.groupBy { it }.count { it.value.size >= 2 }
    }

    fun part2(input: List<String>): Int {

        fun Line.isValid(): Boolean {
            return x1 == x2 || y1 == y2 || (x1 - x2).absoluteValue == (y1 - y2).absoluteValue
        }

        fun Line.toPoints(): List<Point> {
            val points = mutableListOf<Point>()

            val dx = x2 - x1
            val dy = y2 - y1
            val steps = maxOf(dx.absoluteValue, dy.absoluteValue)

            val stepX = when {
                dx == 0 -> 0
                dx > 0 -> 1
                else -> -1
            }
            val stepY = when {
                dy == 0 -> 0
                dy > 0 -> 1
                else -> -1
            }

            for (i in 0..steps) {
                points.add(Point(x1 + i * stepX, y1 + i * stepY))
            }

            return points
        }

        val lines = input.map { parseLine(it) }.filter { it.isValid() }
        val points = lines.map { it.toPoints() }.flatten()
        return points.groupBy { it }.count { it.value.size >= 2 }
    }

    check(part1(readInput("$FOLDER/test")) == 5)
    check(part2(readInput("$FOLDER/test")) == 12)

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