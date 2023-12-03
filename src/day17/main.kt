package day17

import readInput
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.system.measureNanoTime

private const val FOLDER = "day17"

data class Position(var x: Int, var y: Int) {

    fun offset(dx: Int, dy: Int) {
        this.x += dx
        this.y += dy
    }

    override fun toString() = "P($x, $y)"
}

data class Rect(val x1: Int, val x2: Int, val y1: Int, val y2: Int) {
    operator fun contains(position: Position): Boolean {
        return position.x in x1..x2 && position.y in y1..y2
    }
}

fun main() {

    fun readTargetRect(input: String): Rect {
        val (x1, x2, y1, y2) = "x=([0-9-]+)..([0-9-]+), y=([0-9-]+)..([0-9-]+)".toRegex().find(input)!!.destructured
        return Rect(x1.toInt(), x2.toInt(), y1.toInt(), y2.toInt())
    }

    fun findPossibleLaunches(targetRect: Rect): List<Position> {
        val minDx = floor(sqrt(targetRect.x1.toDouble() * 2)).toInt()
        val maxDx = targetRect.x2
        val minDy = targetRect.y1
        val maxDy = -targetRect.y1  // otherwise it will be an overshoot

        fun launchAndHitTarget(dx: Int, dy: Int): Boolean {
            val probe = Position(0, 0)
            var velocityX = dx
            var velocityY = dy
            while (probe.x <= targetRect.x2 && probe.y >= targetRect.y1) {
                if (probe in targetRect) {
                    return true
                }
                probe.offset(velocityX, velocityY--)
                velocityX = max(velocityX - 1, 0)
            }
            return false
        }

        val possibleLaunches = mutableListOf<Position>()
        for (dx in minDx..maxDx) {
            for (dy in minDy..maxDy) {
                if (launchAndHitTarget(dx, dy)) {
                    possibleLaunches.add(Position(dx, dy))
                }
            }
        }

        return possibleLaunches
    }

    fun part1(input: List<String>): Int {
        val targetRect = readTargetRect(input[0])
        val possibleLaunches = findPossibleLaunches(targetRect)
        return possibleLaunches.maxOf { it.y * (it.y + 1) / 2 }
    }

    fun part2(input: List<String>): Int {
        val targetRect = readTargetRect(input[0])
        val possibleLaunches = findPossibleLaunches(targetRect)
        return possibleLaunches.size
    }

    check(part1(readInput("$FOLDER/test")) == 45)
    check(part2(readInput("$FOLDER/test")) == 112)

    val input = readInput("$FOLDER/input")
    val part1Result: Int
    val part1Time = measureNanoTime {
        part1Result = part1(input)
    }
    val part2Result: Int
    val part2Time = measureNanoTime {
        part2Result = part2(input)
    }

    println("Part 1 result: $part1Result")
    println("Part 2 result: $part2Result")
    println("Part 1 takes ${part1Time / 1e6f} milliseconds.")
    println("Part 2 takes ${part2Time / 1e6f} milliseconds.")
}