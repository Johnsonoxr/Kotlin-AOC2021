package day15

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day15"

class TwoDimenGraph<T>(graph: Collection<T>, private val stride: Int) {

    inner class Position(val y: Int, val x: Int) {

        val info: MutableMap<String, Any> by lazy { mutableMapOf() }

        fun up() = if (y == 0) null else Position(y - 1, x)
        fun down() = if (y == h - 1) null else Position(y + 1, x)
        fun left() = if (x == 0) null else Position(y, x - 1)
        fun right() = if (x == w - 1) null else Position(y, x + 1)

        fun neighbors() = listOfNotNull(up(), down(), left(), right())

        override fun toString() = "P($y, $x)"

        fun positionString() = "$y,$x"
    }

    fun createPosition(y: Int, x: Int) = Position(y, x)

    private val graph = graph.toMutableList()
    val w = stride
    val h = graph.size / stride

    operator fun get(position: Position): T {
        return graph[position.y * stride + position.x]
    }

    operator fun set(position: Position, value: T): Boolean {
        graph[position.y * stride + position.x] = value
        return true
    }

    override fun toString(): String {
        return graph.chunked(stride).joinToString("\n") { it.joinToString("") }
    }
}

fun main() {

    fun TwoDimenGraph<Int>.Position.risk(): Int = info["risk"] as Int
    fun TwoDimenGraph<Int>.Position.setRisk(risk: Int) {
        info["risk"] = risk
    }

    fun part1(input: List<String>): Int {

        val riskGraph = TwoDimenGraph(input.flatMap { it.map { c -> c.toString().toInt() } }, input[0].length)
        val accRiskGraph = TwoDimenGraph(List(riskGraph.w * riskGraph.h) { Int.MAX_VALUE }, riskGraph.w)

        var updaters = listOf(accRiskGraph.createPosition(0, 0).apply { setRisk(0) })

        var iteration = 0
        while (updaters.isNotEmpty()) {
            if (++iteration % 100 == 0) {
                "Part1 iteration: $iteration, ${updaters.size} updaters working...".println()
            }
            val nextUpdaterMap = mutableMapOf<String, TwoDimenGraph<Int>.Position>()
            for (updater in updaters) {
                val updaterRisk = updater.risk()
                val neighbors = updater.neighbors()
                neighbors.forEach { it.setRisk(updaterRisk + riskGraph[it]) }
                neighbors.filter { it.risk() < accRiskGraph[it] }.forEach {
                    accRiskGraph[it] = it.risk()
                    nextUpdaterMap[it.positionString()] = it
                }
            }
            updaters = nextUpdaterMap.values.toList()
        }

        return accRiskGraph[accRiskGraph.createPosition(riskGraph.h - 1, riskGraph.w - 1)]
    }

    fun part2(input: List<String>): Int {

        fun List<Int>.increaseBy(n: Int) = this.map { (it + n - 1) % 9 + 1 }

        val srcIpt = input.map { line -> line.map { c -> c.toString().toInt() } }
        val horizontalExtendedIpt = srcIpt.map { line ->
            line + line.increaseBy(1) + line.increaseBy(2) + line.increaseBy(3) + line.increaseBy(4)
        }
        val fullyExtendedIpt = horizontalExtendedIpt +
                horizontalExtendedIpt.map { it.increaseBy(1) } +
                horizontalExtendedIpt.map { it.increaseBy(2) } +
                horizontalExtendedIpt.map { it.increaseBy(3) } +
                horizontalExtendedIpt.map { it.increaseBy(4) }

        val riskGraph = TwoDimenGraph(fullyExtendedIpt.flatten(), fullyExtendedIpt[0].size)
        val accumulatedRiskGraph = TwoDimenGraph(List(riskGraph.w * riskGraph.h) { Int.MAX_VALUE }, riskGraph.w)

        var updaters = listOf(accumulatedRiskGraph.createPosition(0, 0).apply { setRisk(0) })

        var iteration = 0
        while (updaters.isNotEmpty()) {
            if (++iteration % 100 == 0) {
                "Part2 iteration: $iteration, ${updaters.size} updaters working...".println()
            }
            val nextUpdaterMap = mutableMapOf<String, TwoDimenGraph<Int>.Position>()
            for (updater in updaters) {
                val updaterRisk = updater.risk()
                val neighbors = updater.neighbors()
                neighbors.forEach { it.setRisk(updaterRisk + riskGraph[it]) }
                neighbors.filter { it.risk() < accumulatedRiskGraph[it] }.forEach {
                    accumulatedRiskGraph[it] = it.risk()
                    nextUpdaterMap[it.positionString()] = it
                }
            }
            updaters = nextUpdaterMap.values.toList()
        }

        return accumulatedRiskGraph[accumulatedRiskGraph.createPosition(riskGraph.h - 1, riskGraph.w - 1)]
    }

    check(part1(readInput("$FOLDER/test")) == 40)
    check(part2(readInput("$FOLDER/test")) == 315)

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