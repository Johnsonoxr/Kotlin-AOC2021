package day21

import println
import readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureNanoTime

private const val FOLDER = "day21"

fun main() {
    fun part1(input: List<String>): Int {

        class Player(val id: Int, var position: Int) {
            var score = 0
            fun move(distance: Int) {
                position = (position + distance - 1) % 10 + 1
                score += position
            }
        }

        class Dice(private var value: Int) {
            var rollCount = 0
            fun roll(): Int {
                val tmp = value
                value = value % 100 + 1
                rollCount++
                return tmp
            }
        }

        val player1 = "\\d".toRegex().findAll(input[0]).toList().let { Player(it[0].value.toInt(), it[1].value.toInt()) }
        val player2 = "\\d".toRegex().findAll(input[1]).toList().let { Player(it[0].value.toInt(), it[1].value.toInt()) }

        val dice = Dice(1)

        var nextPlayer = player1
        while (player1.score < 1000 && player2.score < 1000) {
            val diceSum = dice.roll() + dice.roll() + dice.roll()
            nextPlayer.move(diceSum)
            nextPlayer = if (nextPlayer == player1) player2 else player1
        }

        return dice.rollCount * nextPlayer.score
    }

    fun part2(input: List<String>): Long {

        data class GameState(val p1Location: Int, val p2Location: Int, val p1Score: Int, val p2Score: Int)

        fun MutableMap<GameState, Long>.add(key: GameState, value: Long) {
            this[key] = this.getOrDefault(key, 0L) + value
        }

        val location1 = "\\d".toRegex().findAll(input[0]).toList().let { it[1].value.toInt() }
        val location2 = "\\d".toRegex().findAll(input[1]).toList().let { it[1].value.toInt() }

        var mapUndone = mapOf<GameState, Long>(GameState(location1, location2, 0, 0) to 1)
        var playerId = 1

        val diceRollCopyMap = mapOf(
            3 to 1,
            4 to 3,
            5 to 6,
            6 to 7,
            7 to 6,
            8 to 3,
            9 to 1
        )

        val mapDone = mutableMapOf<GameState, Long>()
        var iteration = 0
        while (mapUndone.isNotEmpty()) {
            "\nIteration ${++iteration}, gameState count = ${mapUndone.size}".println()
            mapUndone.keys.maxBy { max(it.p1Score, it.p2Score) }.let { it.println() }
            mapUndone.keys.minBy { min(it.p1Score, it.p2Score) }.let { it.println() }

            val map = mutableMapOf<GameState, Long>()
            mapUndone.forEach { (gameState, gameStateCount) ->
                diceRollCopyMap.forEach { (diceRoll, copyCount) ->
                    if (playerId == 1) {
                        val newLocation = (gameState.p1Location + diceRoll - 1) % 10 + 1
                        val newGameState = gameState.copy(p1Location = newLocation, p1Score = gameState.p1Score + newLocation)
                        map.add(newGameState, gameStateCount * copyCount)
                    } else {
                        val newLocation = (gameState.p2Location + diceRoll - 1) % 10 + 1
                        val newGameState = gameState.copy(p2Location = newLocation, p2Score = gameState.p2Score + newLocation)
                        map.add(newGameState, gameStateCount * copyCount)
                    }
                }
            }
            mapDone.putAll(map.filter { it.key.p1Score >= 21 || it.key.p2Score >= 21 })
            mapUndone = map.filterNot { it.key.p1Score >= 21 || it.key.p2Score >= 21 }

            playerId = if (playerId == 1) 2 else 1
        }
        val p1WinUniverses = mapDone.filter { (gameState, _) -> gameState.p1Score > gameState.p2Score }.values.sum()
        val p2WinUniverses = mapDone.filter { (gameState, _) -> gameState.p1Score < gameState.p2Score }.values.sum()
        "p1WinUniverses: $p1WinUniverses".println()
        "p2WinUniverses: $p2WinUniverses".println()

        return maxOf(p1WinUniverses, p2WinUniverses)
    }

    check(part1(readInput("$FOLDER/test")) == 739785)
    check(part2(readInput("$FOLDER/test")) == 444356092776315L)

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