package day24

import println
import readInput
import kotlin.math.min
import kotlin.system.measureNanoTime

private const val FOLDER = "day24"

class WxyzHolder {
    var w = 0L
    var x = 0L
    var y = 0L
    var z = 0L

    private fun String.getV(): Long = when (this) {
        "w" -> w
        "x" -> x
        "y" -> y
        "z" -> z
        else -> toLong()
    }

    private fun String.setV(v: Long) {
        when (this) {
            "w" -> w = v
            "x" -> x = v
            "y" -> y = v
            "z" -> z = v
            else -> throw Exception("Unknown variable: $this")
        }
    }

    fun inp(a: String, v: Long) {
        a.setV(v)
    }

    fun add(a: String, b: String) {
        a.setV(a.getV() + b.getV())
    }

    fun mul(a: String, b: String) {
        a.setV(a.getV() * b.getV())
    }

    fun mod(a: String, b: String) {
        a.setV(a.getV() % b.getV())
    }

    fun div(a: String, b: String) {
        a.setV(a.getV() / b.getV())
    }

    fun eql(a: String, b: String) {
        a.setV(if (a.getV() == b.getV()) 1 else 0)
    }
}

fun main() {

    fun solve(input: List<String>, irrelevantPatternChooser: (List<Int>) -> Int): String {
        val opList = input.map { it.split(" ") }

        fun runPatternTest(pattern: String): Map<String, Long> {
            if (pattern.length != 14) {
                throw Exception("Invalid pattern size: ${pattern.length}")
            }
            val xIdx = pattern.indexOf("?")
            if (xIdx != -1) {
                val resMap = mutableMapOf<String, Long>()
                (1..9).map { n ->
                    val result = runPatternTest(pattern.replaceRange(xIdx, xIdx + 1, n.toString()))
                    for (entry in result) {
                        resMap[entry.key] = entry.value
                    }
                }
                return resMap
            }
            val wxyzHolder = WxyzHolder()
            var iptIdx = 0
            for (op in opList) {
                when (op[0]) {
                    "inp" -> {
                        val modelN = try {
                            pattern[iptIdx++].toString().toLong()
                        } catch (e: Exception) {
                            break
                        }
                        wxyzHolder.inp(op[1], modelN)
                    }

                    "add" -> wxyzHolder.add(op[1], op[2])
                    "mul" -> wxyzHolder.mul(op[1], op[2])
                    "mod" -> wxyzHolder.mod(op[1], op[2])
                    "div" -> wxyzHolder.div(op[1], op[2])
                    "eql" -> wxyzHolder.eql(op[1], op[2])
                    else -> throw Exception("Unknown op: $op")
                }
            }
            return mapOf(pattern to wxyzHolder.z)
        }

        var testPattern = "?????........."

        while (testPattern.any { !it.isDigit() }) {
            "\n===Testing pattern: $testPattern===\n".println()
            val outputMap = runPatternTest(testPattern)

            var outputCriteria = Long.MAX_VALUE
            val sortedOutputs = outputMap.values.sortedBy { it }
            for (o in sortedOutputs) {
                outputCriteria = if (outputCriteria == Long.MAX_VALUE) {
                    o
                } else {
                    if (o > outputCriteria * 2) break
                    o
                }
            }

            val possibleOutputs = outputMap.filter { it.value <= outputCriteria }
            for (patternIdx in testPattern.indices) {
                if (testPattern[patternIdx] != '?') {
                    continue
                }
                val patterns = possibleOutputs.keys.map { it.substring(patternIdx, patternIdx + 1) }.distinct()
                if (patterns.size == 1) {
                    testPattern = testPattern.replaceRange(patternIdx, patternIdx + 1, patterns[0])
                    "Essential pattern found: $testPattern".println()
                }
            }
            //  All of these patterns has the same output, for irrelevant test.
            val irrelevantTestInputPatterns = possibleOutputs.entries.groupBy { it.value }.entries.first().value.map { it.key }

            for (patternIdx in testPattern.indices) {
                if (testPattern[patternIdx] != '?') {
                    continue
                }
                val irrelevantPattern = irrelevantTestInputPatterns.map { it[patternIdx].toString().toInt() }.distinct()
                if (irrelevantPattern.size > 1) {  // Seems like it makes no difference what we put here. So here we can put any number.
                    testPattern = testPattern.replaceRange(patternIdx, patternIdx + 1, irrelevantPatternChooser(irrelevantPattern).toString())
                    "Irrelevant pattern found: $testPattern".println()
                    break
                }
            }

            val patternAdd = min(testPattern.count { it == '.' }, 5 - testPattern.count { it == '?' })
            for (i in 1..patternAdd) {
                testPattern = testPattern.replaceFirst(".", "?")
            }
        }

        return testPattern
    }

    fun part1(input: List<String>): String {
        return solve(input) { it.max() }
    }

    fun part2(input: List<String>): String {
        return solve(input) { it.min() }
    }

    val input = readInput("$FOLDER/input")
    val part1Result: String
    val part1Time = measureNanoTime {
        part1Result = part1(input)
    }
    val part2Result: String
    val part2Time = measureNanoTime {
        part2Result = part2(input)
    }

    println("Part 1 result: $part1Result")
    println("Part 2 result: $part2Result")
    println("Part 1 takes ${part1Time / 1e6f} milliseconds.")
    println("Part 2 takes ${part2Time / 1e6f} milliseconds.")
}