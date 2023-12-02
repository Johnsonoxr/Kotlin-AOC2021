package day14

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day14"

fun main() {
    fun part1(input: List<String>): Int {
        val polymerCountMap = input.first().toCharArray().groupBy { it }.mapValues { it.value.size }.toMutableMap()
        var polymerGenerationMap = input.first().toCharArray().toList().windowed(2).groupBy { it }.mapValues { it.value.size }

        val formulaMap = input.drop(2).associate { line ->
            val (ipt, opt) = line.split(" -> ")
            ipt.toList() to opt.first()
        }

        repeat(10) {
            val newPolymerGenerationMap = mutableMapOf<List<Char>, Int>()
            polymerGenerationMap.forEach { (polyGen, cnt) ->
                val generatedPolymer = formulaMap[polyGen]!!
                polymerCountMap[generatedPolymer] = polymerCountMap.getOrDefault(generatedPolymer, 0) + cnt
                val polyGen1 = listOf(polyGen.first(), generatedPolymer)
                val polyGen2 = listOf(generatedPolymer, polyGen.last())
                newPolymerGenerationMap[polyGen1] = newPolymerGenerationMap.getOrDefault(polyGen1, 0) + cnt
                newPolymerGenerationMap[polyGen2] = newPolymerGenerationMap.getOrDefault(polyGen2, 0) + cnt
            }
            polymerGenerationMap = newPolymerGenerationMap
        }

        return polymerCountMap.values.max() - polymerCountMap.values.min()
    }

    fun part2(input: List<String>): Long {
        val polymerCountMap = input.first().toCharArray().groupBy { it }.mapValues { it.value.size.toLong() }.toMutableMap()
        var polymerGenerationMap = input.first().toCharArray().toList().windowed(2).groupBy { it }.mapValues { it.value.size.toLong() }

        val formulaMap = input.drop(2).associate { line ->
            val (ipt, opt) = line.split(" -> ")
            ipt.toList() to opt.first()
        }

        repeat(40) {
            val newPolymerGenerationMap = mutableMapOf<List<Char>, Long>()
            polymerGenerationMap.forEach { (polyGen, cnt) ->
                val generatedPolymer = formulaMap[polyGen]!!
                polymerCountMap[generatedPolymer] = polymerCountMap.getOrDefault(generatedPolymer, 0L) + cnt
                val polyGen1 = listOf(polyGen.first(), generatedPolymer)
                val polyGen2 = listOf(generatedPolymer, polyGen.last())
                newPolymerGenerationMap[polyGen1] = newPolymerGenerationMap.getOrDefault(polyGen1, 0L) + cnt
                newPolymerGenerationMap[polyGen2] = newPolymerGenerationMap.getOrDefault(polyGen2, 0L) + cnt
            }
            polymerGenerationMap = newPolymerGenerationMap
        }

        return polymerCountMap.values.max() - polymerCountMap.values.min()
    }

    check(part1(readInput("$FOLDER/test")) == 1588)
    check(part2(readInput("$FOLDER/test")) == 2188189693529L)

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