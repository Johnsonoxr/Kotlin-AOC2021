package day03

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day03"

fun main() {
    fun part1(input: List<String>): Int {
        val len = input.first().length
        var gamma = ""
        var epsilon = ""
        for (i in 0..<len) {
            val grouping = input.map { it[i] }.groupBy { it }
            gamma += grouping.maxByOrNull { it.value.size }!!.key
            epsilon += grouping.minByOrNull { it.value.size }!!.key
        }
        return gamma.toInt(2) * epsilon.toInt(2)
    }

    fun part2(input: List<String>): Int {
        val len = input.first().length
        var gamma = ""
        var epsilon = ""

        val gammaList = input.toMutableList()
        val epsilonList = input.toMutableList()

        for (i in 0..<len) {
            if (gammaList.size > 1) {
                val group = gammaList.groupBy { it[i] }
                val count0 = group['0']?.size ?: 0
                val count1 = group['1']?.size ?: 0
                val g = if (count0 > count1) '0' else '1'
                gamma += g
                gammaList.removeIf { it[i] != g }
            } else {
                gamma += gammaList.first()[i]
            }
        }

        for (i in 0..<len) {
            if (epsilonList.size > 1) {
                val group = epsilonList.groupBy { it[i] }
                val count0 = group['0']?.size ?: 0
                val count1 = group['1']?.size ?: 0
                val e = if (count0 <= count1) '0' else '1'
                epsilon += e
                epsilonList.removeIf { it[i] != e }
            } else {
                epsilon += epsilonList.first()[i]
            }
        }

        return gamma.toInt(2) * epsilon.toInt(2)
    }

    check(part1(readInput("$FOLDER/test")) == 198)
    check(part2(readInput("$FOLDER/test")) == 230)

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