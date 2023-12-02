package day04

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day04"

fun main() {

    data class Point(val x: Int, val y: Int)

    val winnerPatterns = mutableListOf<List<Point>>()
    for (i in 0..4) {
        winnerPatterns.add((0..4).map { Point(i, it) })
        winnerPatterns.add((0..4).map { Point(it, i) })
    }

    fun checkWin(board: List<List<Int>>, drawnNumbers: List<Int>): List<Point>? {
        return winnerPatterns.firstOrNull { pattern ->
            pattern.all { (x, y) -> board[x][y] in drawnNumbers }
        }
    }

    fun part1(input: List<String>): Int {

        val drawnNumbers = input.first().split(",").map { it.toInt() }

        val regex = "[0-9]+".toRegex()
        val boards = input.drop(2).windowed(size = 5, step = 6).map { boardStr ->
            boardStr.map { lineStr ->
                regex.findAll(lineStr).map { it.value.toInt() }.toList()
            }
        }

        for (i in drawnNumbers.indices) {
            val numbers = drawnNumbers.take(i + 1)
            val winnerBoard = boards.firstOrNull { board ->
                val pattern = checkWin(board, numbers)
                return@firstOrNull pattern != null
            }
            if (winnerBoard != null) {
                val unmarkedNumbers = winnerBoard.flatten().filter { it !in numbers }
                return unmarkedNumbers.sum() * numbers.last()
            }
        }

        throw IllegalStateException("No winner found")
    }

    fun part2(input: List<String>): Int {
        val drawnNumbers = input.first().split(",").map { it.toInt() }

        val regex = "[0-9]+".toRegex()
        val boards = input.drop(2).windowed(size = 5, step = 6).map { boardStr ->
            boardStr.map { lineStr ->
                regex.findAll(lineStr).map { it.value.toInt() }.toList()
            }
        }

        for (i in drawnNumbers.indices) {
            val numbers = drawnNumbers.take(drawnNumbers.size - i)
            val loserBoard = boards.firstOrNull { board ->
                val pattern = checkWin(board, numbers)
                return@firstOrNull pattern == null
            }
            if (loserBoard != null) {
                val previousNumbers = drawnNumbers.take(drawnNumbers.size - i + 1)
                val unmarkedNumbers = loserBoard.flatten().filter { it !in previousNumbers }
                return unmarkedNumbers.sum() * previousNumbers.last()
            }
        }

        throw IllegalStateException("No loser found")
    }

    check(part1(readInput("$FOLDER/test")) == 4512)
    check(part2(readInput("$FOLDER/test")) == 1924)

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