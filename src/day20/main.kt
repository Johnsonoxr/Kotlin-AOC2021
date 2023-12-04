package day20

import kotlinx.coroutines.*
import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day20"

fun main() {

    fun Array<Array<Char>>.padding(n: Int): Array<Array<Char>> {
        val paddingHorizontal = Array(n) { '0' }
        val paddingVertical = Array(n * 2 + this.first().size) { '0' }
        return Array(n) { paddingVertical } + map { paddingHorizontal + it + paddingHorizontal } + Array(n) { paddingVertical }
    }

    fun enhance(graph: Array<Array<Char>>, enhanceAlgorithm: String, enhanceIteration: Int): Array<Array<Char>> {
        var enhancedGraph = graph.padding(enhanceIteration * 2)

        val scope = CoroutineScope(Dispatchers.Default)

        repeat(enhanceIteration) {
            val targetGraph = Array(enhancedGraph.size - 2) { Array(enhancedGraph.first().size - 2) { '0' } }

            val jobs = (0..enhancedGraph.lastIndex - 2).map { y ->
                scope.launch {
                    for (x in 0..enhancedGraph.first().lastIndex - 2) {
                        val pattern = enhancedGraph.slice(y..y + 2).joinToString("") { it.slice(x..x + 2).joinToString("") }
                        val decoded = enhanceAlgorithm[pattern.toInt(2)]
                        targetGraph[y][x] = decoded
                    }
                }
            }

            runBlocking {
                jobs.joinAll()
            }

            enhancedGraph = targetGraph
        }

        return enhancedGraph
    }

    fun part1(input: List<String>): Int {
        val enhanceAlgorithm = input.first().replace('#', '1').replace('.', '0')

        val graph = input.drop(2).map { line -> line.replace('#', '1').replace('.', '0') }
        val graphArray = Array(graph.size) { y -> Array(graph.size) { x -> graph[y][x] } }

        val enhancedGraph = enhance(graphArray, enhanceAlgorithm, 2)

        return enhancedGraph.sumOf { it.count { c -> c == '1' } }
    }

    fun part2(input: List<String>): Int {
        val enhanceAlgorithm = input.first().replace('#', '1').replace('.', '0')

        val graph = input.drop(2).map { line -> line.replace('#', '1').replace('.', '0') }
        val graphArray = Array(graph.size) { y -> Array(graph.size) { x -> graph[y][x] } }

        val enhancedGraph = enhance(graphArray, enhanceAlgorithm, 50)

        return enhancedGraph.sumOf { it.count { c -> c == '1' } }
    }

    check(part1(readInput("$FOLDER/test")) == 35)
    check(part2(readInput("$FOLDER/test")) == 3351)

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