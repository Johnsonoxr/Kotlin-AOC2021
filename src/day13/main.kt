package day13

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day13"

fun main() {

    data class Fold(val x: Int? = null, val y: Int? = null)
    data class Position(val x: Int, val y: Int)

    fun part1(input: List<String>): Int {
        val blankIndex = input.indexOfFirst { it.isBlank() }

        var positions: Set<Position> = input.take(blankIndex).map { line -> line.split(",").let { Position(it[0].toInt(), it[1].toInt()) } }.toSet()
        val foldInstructions = input.drop(blankIndex + 1).map { line ->
            val matchResult = "([xy])=([0-9]+)".toRegex().find(line)!!.groupValues.takeLast(2)
            if (matchResult[0] == "x") Fold(x = matchResult[1].toInt()) else Fold(y = matchResult[1].toInt())
        }

        foldInstructions.first().let { fold ->
            if (fold.x != null) {
                val (ps1, ps2) = positions.partition { it.x < fold.x }
                positions = ps1.toSet() + ps2.filter { it.x > fold.x }.map { Position(fold.x - (it.x - fold.x), it.y) }.toSet()
            } else if (fold.y != null) {
                val (ps1, ps2) = positions.partition { it.y < fold.y }
                positions = ps1.toSet() + ps2.filter { it.y > fold.y }.map { Position(it.x, fold.y - (it.y - fold.y)) }.toSet()
            }
            return positions.size
        }
    }

    fun part2(input: List<String>): Int {
        val blankIndex = input.indexOfFirst { it.isBlank() }

        var positions: Set<Position> = input.take(blankIndex).map { line -> line.split(",").let { Position(it[0].toInt(), it[1].toInt()) } }.toSet()
        val foldInstructions = input.drop(blankIndex + 1).map { line ->
            val matchResult = "([xy])=([0-9]+)".toRegex().find(line)!!.groupValues.takeLast(2)
            if (matchResult[0] == "x") Fold(x = matchResult[1].toInt()) else Fold(y = matchResult[1].toInt())
        }

        foldInstructions.forEach { fold ->
            if (fold.x != null) {
                val (ps1, ps2) = positions.partition { it.x < fold.x }
                positions = ps1.toSet() + ps2.filter { it.x > fold.x }.map { Position(fold.x - (it.x - fold.x), it.y) }.toSet()
            } else if (fold.y != null) {
                val (ps1, ps2) = positions.partition { it.y < fold.y }
                positions = ps1.toSet() + ps2.filter { it.y > fold.y }.map { Position(it.x, fold.y - (it.y - fold.y)) }.toSet()
            }
        }

        val w = positions.maxOf { it.x } + 1
        val h = positions.maxOf { it.y } + 1

        val map = Array(h) { Array(w) { " " } }
        positions.forEach { map[it.y][it.x] = "#" }

        map.forEach { println(it.joinToString("")) }

        return 1
    }

    check(part1(readInput("$FOLDER/test")) == 17)
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