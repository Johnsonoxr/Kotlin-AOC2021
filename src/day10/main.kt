package day10

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day10"

fun main() {

    val chunkPairs = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>'
    )

    fun part1(input: List<String>): Int {

        val scoreMap = mapOf(
            ')' to 3,
            ']' to 57,
            '}' to 1197,
            '>' to 25137
        )

        val corruptedChars = input.mapNotNull line@{ line ->
            val closeStack = mutableListOf<Char>()
            line.forEach { c ->
                when (c) {
                    in chunkPairs.keys -> closeStack.add(chunkPairs[c]!!)
                    else -> if (closeStack.lastOrNull() == c) {
                        closeStack.removeLast()
                    } else {
                        return@line c
                    }
                }
            }
            return@line null
        }

        return corruptedChars.sumOf { scoreMap[it]!! }
    }

    fun part2(input: List<String>): Long {

        val scoreMap = mapOf(
            ')' to 1L,
            ']' to 2L,
            '}' to 3L,
            '>' to 4L
        )

        val incompleteCharsList = input.mapNotNull line@{ line ->
            val closeStack = mutableListOf<Char>()
            line.forEach { c ->
                when (c) {
                    in chunkPairs.keys -> closeStack.add(chunkPairs[c]!!)
                    else -> if (closeStack.lastOrNull() == c) {
                        closeStack.removeLast()
                    } else {
                        return@line null    //  corrupted
                    }
                }
            }
            return@line closeStack.reversed()
        }

        val scoreList = incompleteCharsList.map { incompleteChars ->
            var score = 0L
            incompleteChars.forEach { incompleteChar ->
                score = score * 5 + scoreMap[incompleteChar]!!
            }
            score
        }

        return scoreList.sorted()[scoreList.size / 2]
    }

    check(part1(readInput("$FOLDER/test")) == 26397)
    check(part2(readInput("$FOLDER/test")) == 288957L)

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