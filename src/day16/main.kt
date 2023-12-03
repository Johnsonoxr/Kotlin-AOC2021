package day16

import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day16"

sealed class Pkg {
    data class Value(val version: Int, val value: Long) : Pkg()
    data class Operator(val version: Int, val op: Int, val subPackages: List<Pkg>) : Pkg()
}

fun main() {

    class MessageQueue(private var bits: String) {

        fun hasNext(): Boolean = bits.any { it != '0' }

        fun pop(n: Int): String {
            val result = bits.take(n)
            bits = bits.removeRange(0, n)
            return result
        }
    }

    fun parsePackages(messageQueue: MessageQueue, pkgCount: Int = Int.MAX_VALUE): List<Pkg> {
        val packages = mutableListOf<Pkg>()
        while (packages.size < pkgCount && messageQueue.hasNext()) {
            val version = messageQueue.pop(3).toInt(2)
            val op = messageQueue.pop(3).toInt(2)
            if (op == 4) {  // value
                var numStr = ""
                while (true) {
                    val finish = messageQueue.pop(1) == "0"
                    numStr += messageQueue.pop(4)
                    if (finish) {
                        break
                    }
                }
                packages.add(Pkg.Value(version, numStr.toLong(2)))
            } else {    // operator
                val lengthTypeId = messageQueue.pop(1).first()
                val subPackages = if (lengthTypeId == '0') {
                    val subPkgIptSize = messageQueue.pop(15).toInt(2)
                    val subPkgIpt = messageQueue.pop(subPkgIptSize)
                    parsePackages(MessageQueue(subPkgIpt))
                } else {
                    val subPkgCount = messageQueue.pop(11).toInt(2)
                    parsePackages(messageQueue, subPkgCount)
                }
                packages.add(Pkg.Operator(version, op, subPackages))
            }
        }

        return packages
    }

    fun part1(input: List<String>): Int {
        val bitInput = input.first().map { it.digitToInt(16).toString(2).padStart(4, '0') }.joinToString("")
        val packages = parsePackages(MessageQueue(bitInput))

        fun Pkg.version(): Int = when (this) {
            is Pkg.Value -> version
            is Pkg.Operator -> version + subPackages.sumOf { it.version() }
        }

        return packages.sumOf { it.version() }
    }

    fun part2(input: List<String>): Long {

        val bitInput = input.first().map { it.digitToInt(16).toString(2).padStart(4, '0') }.joinToString("")
        val packages = parsePackages(MessageQueue(bitInput))

        fun Pkg.value(): Long = when (this) {
            is Pkg.Value -> value
            is Pkg.Operator -> {
                val subValues = subPackages.map { it.value() }
                when (op) {
                    0 -> subValues.sum()
                    1 -> subValues.reduce { acc, i -> acc * i }
                    2 -> subValues.min()
                    3 -> subValues.max()
                    5 -> if (subValues[0] > subValues[1]) 1 else 0
                    6 -> if (subValues[0] < subValues[1]) 1 else 0
                    7 -> if (subValues[0] == subValues[1]) 1 else 0
                    else -> throw Exception("Invalid operator: $op")
                }
            }
        }

        return packages.sumOf { it.value() }
    }

    check(part1(readInput("$FOLDER/test1")) == 16)
    check(part1(readInput("$FOLDER/test2")) == 12)
    check(part1(readInput("$FOLDER/test3")) == 23)
    check(part1(readInput("$FOLDER/test4")) == 31)
    check(part2(readInput("$FOLDER/test5")) == 1L)

    val input = readInput("$FOLDER/input")
    val part1Result: Int
    val part1Time = measureNanoTime {
        part1Result = part1(input)
    }
    val part2Result: Long
    val part2Time = measureNanoTime {
        part2Result = part2(input)
    }

    println("Part 1 result: $part1Result")
    println("Part 2 result: $part2Result")
    println("Part 1 takes ${part1Time / 1e6f} milliseconds.")
    println("Part 2 takes ${part2Time / 1e6f} milliseconds.")
}