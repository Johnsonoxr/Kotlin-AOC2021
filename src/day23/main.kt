package day23

import println
import readInput
import kotlin.math.abs
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime

private const val FOLDER = "day23"

/**
 *     00 01 02 03 04 05 06 07 08 09 10
 *  00  *  *  *  *  *  *  *  *  *  *  *
 *  01        *     *     *     *
 *  02        *     *     *     *
 */

data class Position(val x: Int, val y: Int)
data class Move(val from: Position, val to: Position)

operator fun Map<String, Position>.get(position: Position): String? {
    return this.entries.firstOrNull { it.value == position }?.key
}

val strIdxMap = mutableMapOf<String, Int>()

fun String.withIndex(): String {
    when (this) {
        in strIdxMap -> strIdxMap[this] = strIdxMap[this]!! + 1
        else -> strIdxMap[this] = 1
    }
    return "$this${strIdxMap[this]}"
}

fun Int.rangeHeading(to: Int): IntRange {
    return when {
        this < to -> this + 1..to
        else -> to..<this
    }
}

data class Game(val state: Map<String, Position>, val energy: Int) {
    fun isFinished(): Boolean {
        return state.keys.all { it[0] == 'X' }
    }

    fun simpleString(): String {
        val arr = arrayOf("...........".toCharArray(), "  . . . .  ".toCharArray(), "  . . . .  ".toCharArray())
        state.forEach { (player, position) ->
            arr[position.y][position.x] = player[0]
        }
        return "#".repeat(11) + "\n" + arr.joinToString("\n") { it.joinToString("") } + "\n" + "#".repeat(11)
    }
}

fun Map<String, Position>.stateKey(): String {
    return entries.map { "${it.key[0]}${it.value.x},${it.value.y}" }.sorted().joinToString("|")
}

fun main() {

    val hallwayStoppablePositions = listOf(0, 1, 3, 5, 7, 9, 10)
    var targetPositions = mapOf<Char, List<Position>>()
    val energyPerStep = mapOf(
        'A' to 1,
        'B' to 10,
        'C' to 100,
        'D' to 1000,
    )

    fun calcStep(from: Position, to: Position): Int {
        return when {
            from.y > 0 && to.y > 0 -> abs(from.x - to.x) + from.y + to.y
            else -> abs(from.x - to.x) + abs(from.y - to.y)
        }
    }

    fun getNextMove(game: Game): List<Move> {
        val state = game.state
        val moves = state.entries.map { (player, position) ->
            if (player[0] == 'X') {    // Finished.
                return@map emptyList()
            }
            if (state.values.any { it.x == position.x && it.y < position.y }) {
                return@map emptyList()    // Blocked by others.
            }

            val targetPs = targetPositions[player[0]]!!

            if (position.y == 0) {
                if (state.any { it.key != player && it.key[0] != 'X' && it.value in targetPs }) {  // Others in the room. No move.
                    return@map emptyList()
                } else if (state.values.none { it.y == 0 && it.x in position.x.rangeHeading(targetPs.first().x) }) {    // This is the definitive move.
                    val targetP = targetPs.last { state[it] == null }
                    return listOf(Move(position, targetP))
                } else {
                    return@map emptyList()    // Blocked, no move.
                }
            }

            if (state.any { it.key != player && it.key[0] != 'X' && it.value in targetPs }) {  // Others in the room. Targeting hallway.
                val unblockedHallwayPositions = hallwayStoppablePositions.filter { hsp ->
                    val path = position.x.rangeHeading(hsp)
                    return@filter state.values.none { it.y == 0 && it.x in path }
                }
                return@map unblockedHallwayPositions.map { Move(position, Position(it, 0)) }
            }
            if (state.values.none { it.x in position.x.rangeHeading(targetPs.first().x) }) {   //  This is the definitive move.
                val targetP = targetPs.last { state[it] == null }
                return listOf(Move(position, targetP))
            }
            val unblockedHallwayPositions = hallwayStoppablePositions.filter { hsp ->
                val path = position.x.rangeHeading(hsp)
                return@filter state.values.none { it.y == 0 && it.x in path }
            }
            return@map unblockedHallwayPositions.map { Move(position, Position(it, 0)) }
        }
        return moves.flatten()
    }

    fun shouldLockPosition(position: Position, state: Map<String, Position>): Boolean {
        val player = state[position] ?: return false
        val playerTargetPositions = targetPositions[player[0]] ?: return true // 'X' is not a player.
        if (position !in playerTargetPositions) {
            return false
        }
        val rev = playerTargetPositions.asReversed()
        for (p in rev) {
            if (p == position) {
                return true
            }
            val s = state.entries.firstOrNull { it.value == p }?.key?.get(0) ?: return false
            if (s != 'X' && s != player[0]) {
                return false
            }
        }
        return true
    }

    fun calcEnergyIfCheat(game: Game): Int {
        return game.energy + targetPositions.entries.map { (player, positions) ->
            val unfinishedPs = positions.filter { p -> game.state.entries.firstOrNull { it.value == p }?.key?.get(0) != 'X' }

            if (unfinishedPs.isEmpty()) {
                return@map 0
            }

            val playerPs = game.state.filter { it.key[0] == player }.map { it.value }

            return@map playerPs.zip(unfinishedPs).sumOf { (from, to) -> calcStep(from, to) } * energyPerStep[player]!!
        }.sum()
    }

    fun solve(
        game: Game,
        lowestEnergy: IntArray = intArrayOf(Int.MAX_VALUE),
        energyCachedMap: MutableMap<String, Int> = mutableMapOf()
    ): Int {
        getNextMove(game).forEach { move ->
            val player = game.state[move.from]!!
            val newEnergy = game.energy + calcStep(move.from, move.to) * energyPerStep[player[0]]!!
            if (newEnergy >= lowestEnergy[0]) {
                return@forEach
            }

            val newState = game.state.toMutableMap()
            newState[player] = move.to
            if (shouldLockPosition(move.to, newState)) {
                newState.remove(player)
                newState["X".withIndex()] = move.to
            }

            val stateKey = newState.stateKey()
            if (newEnergy >= energyCachedMap.getOrDefault(stateKey, Int.MAX_VALUE)) {
                return@forEach
            } else {
                energyCachedMap[stateKey] = newEnergy
            }

            val newGame = Game(newState, newEnergy)

            if (calcEnergyIfCheat(game) > lowestEnergy[0]) {
                return@forEach
            }

            if (newGame.isFinished()) {
                if (newEnergy < lowestEnergy[0]) {
                    lowestEnergy[0] = newEnergy
                    "Found new lowest energy: $newEnergy".println()
                }
                return newEnergy
            }
            solve(newGame, lowestEnergy, energyCachedMap)
        }
        return lowestEnergy[0]
    }

    fun part1(input: List<String>): Int {
        val (p12, p14, p16, p18) = "[A-Z]".toRegex().findAll(input[2]).map { it.value.withIndex() }.toList()
        val (p22, p24, p26, p28) = "[A-Z]".toRegex().findAll(input[3]).map { it.value.withIndex() }.toList()

        val state = mutableMapOf(
            p12 to Position(2, 1), p14 to Position(4, 1), p16 to Position(6, 1), p18 to Position(8, 1),
            p22 to Position(2, 2), p24 to Position(4, 2), p26 to Position(6, 2), p28 to Position(8, 2)
        )
        targetPositions = mapOf(
            'A' to listOf(Position(2, 1), Position(2, 2)),
            'B' to listOf(Position(4, 1), Position(4, 2)),
            'C' to listOf(Position(6, 1), Position(6, 2)),
            'D' to listOf(Position(8, 1), Position(8, 2)),
        )

        val lockedPositions = mutableSetOf<Position>()
        for (positions in state.values) {
            if (shouldLockPosition(positions, state)) {
                lockedPositions.add(positions)
            }
        }
        lockedPositions.forEach { lockedPosition ->
            val player = state[lockedPosition]!!
            state.remove(player)
            state["X".withIndex()] = lockedPosition
        }

        val game = Game(
            state = state,
            energy = 0
        )

        return solve(game)
    }

    fun part2(input: List<String>): Int {

        val extendedInput = input.take(3) + listOf("  #D#C#B#A#  ", "  #D#B#A#C#  ") + input.drop(3)

        val state = mutableMapOf<String, Position>()
        extendedInput.drop(2).forEachIndexed { lineIdx, line ->
            val players = "[A-Z]".toRegex().findAll(line).map { it.value.withIndex() }.toList()
            players.forEachIndexed { playerIdx, player ->
                state[player] = Position((playerIdx + 1) * 2, lineIdx + 1)
            }
        }
        targetPositions = mapOf(
            'A' to listOf(Position(2, 1), Position(2, 2), Position(2, 3), Position(2, 4)),
            'B' to listOf(Position(4, 1), Position(4, 2), Position(4, 3), Position(4, 4)),
            'C' to listOf(Position(6, 1), Position(6, 2), Position(6, 3), Position(6, 4)),
            'D' to listOf(Position(8, 1), Position(8, 2), Position(8, 3), Position(8, 4)),
        )

        val lockedPositions = mutableSetOf<Position>()
        for (positions in state.values) {
            if (shouldLockPosition(positions, state)) {
                lockedPositions.add(positions)
            }
        }
        lockedPositions.forEach { lockedPosition ->
            val player = state[lockedPosition]!!
            state.remove(player)
            state["X".withIndex()] = lockedPosition
        }

        val game = Game(
            state = state,
            energy = 0
        )

        return solve(game)
    }

    check(part1(readInput("$FOLDER/test")) == 12521)
    check(part2(readInput("$FOLDER/test")) == 44169)

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