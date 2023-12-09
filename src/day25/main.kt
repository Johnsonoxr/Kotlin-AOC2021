package day25

import println
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day25"

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

    val graph = graph.toMutableList()
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

data class Move(val from: TwoDimenGraph<Char>.Position, val to: TwoDimenGraph<Char>.Position)

fun main() {
    fun part1(input: List<String>): Int {

        val graph = TwoDimenGraph(input.joinToString("").toCharArray().toList(), stride = input[0].length)
        val eastForwardCucumbers = mutableListOf<TwoDimenGraph<Char>.Position>()
        val southForwardCucumbers = mutableListOf<TwoDimenGraph<Char>.Position>()

        for (y in 0..<graph.h) {
            for (x in 0..<graph.w) {
                val position = graph.createPosition(y, x)
                if (graph[position] == '>') {
                    eastForwardCucumbers.add(position)
                } else if (graph[position] == 'v') {
                    southForwardCucumbers.add(position)
                }
            }
        }

        var steps = 0
        while (true) {
            steps++

            val movedEastForwardCucumbers = mutableSetOf<Move>()
            eastForwardCucumbers.forEach { eastForwardCucumber ->
                val nextPosition = eastForwardCucumber.right() ?: graph.createPosition(eastForwardCucumber.y, 0)
                if (graph[nextPosition] == '.') {
                    movedEastForwardCucumbers.add(Move(eastForwardCucumber, nextPosition))
                }
            }

            movedEastForwardCucumbers.forEach { move ->
                graph[move.from] = '.'
                graph[move.to] = '>'
                eastForwardCucumbers.remove(move.from)
                eastForwardCucumbers.add(move.to)
            }

            val movedSouthForwardCucumbers = mutableSetOf<Move>()
            southForwardCucumbers.forEach { southForwardCucumber ->
                val nextPosition = southForwardCucumber.down() ?: graph.createPosition(0, southForwardCucumber.x)
                if (graph[nextPosition] == '.') {
                    movedSouthForwardCucumbers.add(Move(southForwardCucumber, nextPosition))
                }
            }

            movedSouthForwardCucumbers.forEach { move ->
                graph[move.from] = '.'
                graph[move.to] = 'v'
                southForwardCucumbers.remove(move.from)
                southForwardCucumbers.add(move.to)
            }

            if (movedSouthForwardCucumbers.isEmpty() && movedEastForwardCucumbers.isEmpty()) {
                break
            }
        }

        return steps
    }

    check(part1(readInput("$FOLDER/test")) == 58)

    val input = readInput("$FOLDER/input")
    val part1Result: Int
    val part1Time = measureNanoTime {
        part1Result = part1(input)
    }

    println("Part 1 result: $part1Result")
    println("Part 1 takes ${part1Time / 1e6f} milliseconds.")
}