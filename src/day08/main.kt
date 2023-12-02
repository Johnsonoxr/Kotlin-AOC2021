package day08

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day08"

fun main() {
    fun part1(input: List<String>): Int {
        val regex = "[a-g]+".toRegex()
        val segments = input.map { regex.findAll(it.split(" | ")[1]).map { s -> s.value }.toList() }

        val uniqueSegmentSize = listOf(2, 3, 4, 7)
        return segments.flatten().count { it.length in uniqueSegmentSize }
    }

    fun part2(input: List<String>): Int {

        val regex = "[a-g]+".toRegex()
        val segmentsList = input.map { regex.findAll(it).map { s -> s.value.toCharArray().sorted().joinToString("") }.toList() }

        val numbers = segmentsList.map { segments ->
            val lastFourDigits = segments.takeLast(4).map { it.toCharArray().toSet() }
            val digits = segments.groupBy { it }.keys.map { it.toCharArray().toSet() }
            val twoThreeFive = digits.filter { it.size == 5 }.toMutableSet()
            val zeroSixNine = digits.filter { it.size == 6 }.toMutableSet()
            val one = digits.first { it.size == 2 }
            val seven = digits.first { it.size == 3 }
            val four = digits.first { it.size == 4 }
            val eight = digits.first { it.size == 7 }
            val (five, twoThree) = twoThreeFive.partition { it.containsAll(four - one) }
            val (three, two) = twoThree.partition { it.containsAll(one) }
            val (nine, zeroSix) = zeroSixNine.partition { it.containsAll(four) }
            val (zero, six) = zeroSix.partition { it.containsAll(one) }
            val mapping: Map<Set<Char>, Int> = mapOf(
                zero.first() to 0,
                one to 1,
                two.first() to 2,
                three.first() to 3,
                four to 4,
                five.first() to 5,
                six.first() to 6,
                seven to 7,
                eight to 8,
                nine.first() to 9
            )
            return@map lastFourDigits.map { mapping[it] }.joinToString("").toInt()
        }

        return numbers.sum()
    }

    check(part1(readInput("$FOLDER/test")) == 26)
    check(part2(readInput("$FOLDER/test")) == 61229)

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