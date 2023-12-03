package day18

import readInput
import kotlin.math.floor
import kotlin.system.measureNanoTime

private const val FOLDER = "day18"

fun main() {

    val numRegex = Regex("\\d+")

    fun String.explode(): String? {
        var depth = 0
        this.forEachIndexed { index, c ->
            when (c) {
                '[' -> depth++
                ']' -> depth--
            }
            if (depth <= 0) return null
            if (depth <= 4) return@forEachIndexed
            //  explode
            var resultString = this
            val closeIndex = this.substring(index + 1).indexOf(']') + index + 1
            val (left, right) = this.substring(index + 1, closeIndex).split(',').map { it.toInt() }
            numRegex.findAll(this, startIndex = closeIndex + 1).firstOrNull()?.let { rightReceiver ->
                resultString = resultString.replaceRange(rightReceiver.range, (rightReceiver.value.toInt() + right).toString())
            }
            resultString = resultString.replaceRange(index..closeIndex, "0")
            numRegex.findAll(this.substring(0, index)).lastOrNull()?.let { leftReceiver ->
                resultString = resultString.replaceRange(leftReceiver.range, (leftReceiver.value.toInt() + left).toString())
            }
            return resultString
        }
        return null
    }

    fun String.split(): String? {
        "[0-9]{2,}".toRegex().findAll(this).firstOrNull()?.let { match ->
            val value = match.value.toInt()
            val left = floor(value / 2.0).toInt()
            val right = value - left
            return this.replaceRange(match.range, "[$left,$right]")
        }
        return null
    }

    fun combine(snailNumberExpression1: String, snailNumberExpression2: String): String {
        var rst = "[$snailNumberExpression1,$snailNumberExpression2]"
        while (true) {
            if (rst.explode()?.also { rst = it } != null) {
                continue
            }
            if (rst.split()?.also { rst = it } != null) {
                continue
            }
            break
        }
        return rst
    }

    fun String.magnitude(): Int {
        if (',' !in this) return this.toInt()
        val content = this.substring(1, this.length - 1)
        var depth = 0
        content.forEachIndexed { index, c ->
            if (c == '[') depth++
            if (c == ']') depth--
            if (c == ',' && depth == 0) {
                val left = content.substring(0, index).magnitude()
                val right = content.substring(index + 1).magnitude()
                return 3 * left + 2 * right
            }
        }
        throw Exception("Invalid input: $this")
    }

    fun part1(input: List<String>): Int {
        return input.reduce { acc, s -> combine(acc, s) }.magnitude()
    }

    fun part2(input: List<String>): Int {

        var highestMagnitude = 0

        for (i in input.indices) {
            for (j in input.indices) {
                if (i == j) continue
                val snailNumberExpression = combine(input[i], input[j])
                val magnitude = snailNumberExpression.magnitude()
                if (magnitude > highestMagnitude) {
                    highestMagnitude = magnitude
                }
            }
        }

        return highestMagnitude
    }

    check(part1(readInput("$FOLDER/test")) == 4140)
    check(part2(readInput("$FOLDER/test")) == 3993)

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