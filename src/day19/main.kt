package day19

import readInput
import kotlin.math.abs
import kotlin.system.measureNanoTime

private const val FOLDER = "day19"

data class Position(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Position): Position {
        return Position(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Position): Position {
        return Position(x - other.x, y - other.y, z - other.z)
    }
}

class Scanner(val id: Int, private val beacons: List<Position>) {
    private val _lazyBeacons = Array<Position?>(beacons.size) { beacons[it] }
    private var _lazyCenter: Position? = null
    val beaconsCount get() = beacons.size

    var offset: Position = Position(0, 0, 0)
        set(value) {
            field = value
            _lazyCenter = null
            _lazyBeacons.fill(null)
        }

    var rotationType: Int = 0
        set(value) {
            field = value
            _lazyCenter = null
            _lazyBeacons.fill(null)
        }

    val center: Position
        get() = _lazyCenter ?: transform(Position(0, 0, 0)).also { _lazyCenter = it }

    fun getBeacons(): List<Position> {
        return beacons.indices.map { getBeacon(it) }
    }

    fun getBeaconIterator(): Iterator<Position> {
        return object : Iterator<Position> {
            private var idx = 0
            override fun hasNext(): Boolean {
                return idx < beacons.size
            }

            override fun next(): Position {
                return getBeacon(idx++)
            }
        }
    }

    fun getBeacon(idx: Int): Position {
        return _lazyBeacons[idx] ?: transform(beacons[idx]).also { _lazyBeacons[idx] = it }
    }

    override fun toString(): String {
        return "Scanner(id=$id, offset=$offset, rotationType=$rotationType, beaconsCount=$beaconsCount)"
    }

    private fun transform(position: Position): Position {
        return when (rotationType) {
            //  x, y, z
            0 -> Position(position.x + offset.x, position.y + offset.y, position.z + offset.z)
            //  x, z, -y
            1 -> Position(position.x + offset.x, position.z + offset.y, -position.y + offset.z)
            //  x, -y, -z
            2 -> Position(position.x + offset.x, -position.y + offset.y, -position.z + offset.z)
            //  x, -z, y
            3 -> Position(position.x + offset.x, -position.z + offset.y, position.y + offset.z)
            //  -x, y, -z
            4 -> Position(-position.x + offset.x, position.y + offset.y, -position.z + offset.z)
            //  -x, z, y
            5 -> Position(-position.x + offset.x, position.z + offset.y, position.y + offset.z)
            //  -x, -y, z
            6 -> Position(-position.x + offset.x, -position.y + offset.y, position.z + offset.z)
            //  -x, -z, -y
            7 -> Position(-position.x + offset.x, -position.z + offset.y, -position.y + offset.z)
            //  y, z, x
            8 -> Position(position.y + offset.x, position.z + offset.y, position.x + offset.z)
            //  y, x, -z
            9 -> Position(position.y + offset.x, position.x + offset.y, -position.z + offset.z)
            //  y, -z, -x
            10 -> Position(position.y + offset.x, -position.z + offset.y, -position.x + offset.z)
            //  y, -x, z
            11 -> Position(position.y + offset.x, -position.x + offset.y, position.z + offset.z)
            //  -y, z, -x
            12 -> Position(-position.y + offset.x, position.z + offset.y, -position.x + offset.z)
            //  -y, x, z
            13 -> Position(-position.y + offset.x, position.x + offset.y, position.z + offset.z)
            //  -y, -z, x
            14 -> Position(-position.y + offset.x, -position.z + offset.y, position.x + offset.z)
            //  -y, -x, -z
            15 -> Position(-position.y + offset.x, -position.x + offset.y, -position.z + offset.z)
            //  z, x, y
            16 -> Position(position.z + offset.x, position.x + offset.y, position.y + offset.z)
            //  z, y, -x
            17 -> Position(position.z + offset.x, position.y + offset.y, -position.x + offset.z)
            //  z, -x, -y
            18 -> Position(position.z + offset.x, -position.x + offset.y, -position.y + offset.z)
            //  z, -y, x
            19 -> Position(position.z + offset.x, -position.y + offset.y, position.x + offset.z)
            //  -z, x, -y
            20 -> Position(-position.z + offset.x, position.x + offset.y, -position.y + offset.z)
            //  -z, y, x
            21 -> Position(-position.z + offset.x, position.y + offset.y, position.x + offset.z)
            //  -z, -x, y
            22 -> Position(-position.z + offset.x, -position.x + offset.y, position.y + offset.z)
            //  -z, -y, -x
            23 -> Position(-position.z + offset.x, -position.y + offset.y, -position.x + offset.z)
            else -> throw IllegalArgumentException("Invalid rotation type: $rotationType")
        }
    }
}

fun main() {

    fun parseScanners(input: List<String>): List<Scanner> {
        val scanners = mutableListOf<Scanner>()
        var id: Int? = null
        var beacons = mutableListOf<Position>()
        for (line in input) {
            if (line.isEmpty() && id != null) {
                scanners.add(Scanner(id, beacons))
                id = null
                beacons = mutableListOf()
                continue
            }
            val scannerId = "--- scanner ([0-9]+) ---".toRegex().find(line)?.groupValues?.last()?.toInt()
            if (scannerId != null) {
                id = scannerId
                continue
            }
            val beacon = line.split(",").map { it.toInt() }.let { Position(it[0], it[1], it[2]) }
            beacons.add(beacon)
        }

        if (id != null) {
            scanners.add(Scanner(id, beacons))
        }

        return scanners
    }

    fun resolveScanners(input: List<String>): List<Scanner> {
        val scanners = parseScanners(input)

        //  Helper class for fast lookup, preprocessed with the transformed beacons.
        data class TransformedScanner(val center: Position, val beacons: Set<Position>)

        val unconfirmedScanners = scanners.toMutableList()
        val confirmedScanners = mutableListOf<Scanner>()

        while (unconfirmedScanners.isNotEmpty()) {

            if (confirmedScanners.isEmpty()) {
                val scanner = unconfirmedScanners.removeFirst()
                scanner.offset = Position(0, 0, 0)
                scanner.rotationType = 0
                confirmedScanners.add(scanner)
            }

            val confirmedBeacons = confirmedScanners.map { it.getBeacons() }.flatten().toSet()
            val transformedConfirmedScanners = confirmedScanners.map { TransformedScanner(it.center, it.getBeacons().toSet()) }

            var matchedScanner: Scanner? = null

            unconfirmedScanners.forEach outerMostForeach@{ scanner ->
                if (matchedScanner != null) {
                    return@outerMostForeach
                }
                confirmedBeacons.forEach { dBeacon ->
                    (0..<scanner.beaconsCount).forEach { beaconIdx ->
                        (0..<24).forEach { rotateType ->
                            scanner.offset = Position(0, 0, 0)
                            scanner.rotationType = rotateType
                            val beacon = scanner.getBeacon(beaconIdx)
                            val offset = dBeacon - beacon
                            scanner.offset = offset

                            var matchedBeaconCount = 0
                            var foundUnmatchedBeacon = false
                            for (b in scanner.getBeaconIterator()) {

                                for (confirmedScanner in transformedConfirmedScanners) {
                                    if (confirmedScanner.center.let { maxOf(abs(it.x - b.x), abs(it.y - b.y), abs(it.z - b.z)) } > 1000) {
                                        //  Skip if the distance is too far.
                                        continue
                                    }
                                    if (b !in confirmedScanner.beacons) {
                                        foundUnmatchedBeacon = true
                                        break
                                    }
                                    matchedBeaconCount++
                                }

                                if (foundUnmatchedBeacon) {
                                    break
                                }
                            }

                            if (!foundUnmatchedBeacon && matchedBeaconCount >= 12) {
                                println("Found matched scanner-${scanner.id}, rotate type $rotateType, offset $offset")
                                matchedScanner = scanner
                                return@outerMostForeach
                            }
                        }
                    }
                }
            }

            if (matchedScanner != null) {
                confirmedScanners.add(matchedScanner!!)
                unconfirmedScanners.remove(matchedScanner!!)
            } else if (confirmedScanners.size == 1) {
                unconfirmedScanners.add(confirmedScanners.removeFirst())
            }
        }

        return confirmedScanners
    }

    fun part1(input: List<String>): Int {
        val resolvedScanners = resolveScanners(input)
        return resolvedScanners.map { it.getBeacons() }.flatten().toSet().size
    }

    fun part2(input: List<String>): Int {
        val resolvedScanners = resolveScanners(input)
        val scannerCenters = resolvedScanners.map { it.center }
        var largestManhattanDistance = 0
        for (center1 in scannerCenters) {
            for (center2 in scannerCenters) {
                if (center1 == center2) {
                    continue
                }
                val manhattanDistance = (center1 - center2).let { abs(it.x) + abs(it.y) + abs(it.z) }
                if (manhattanDistance > largestManhattanDistance) {
                    largestManhattanDistance = manhattanDistance
                }
            }
        }
        return largestManhattanDistance
    }

    check(part1(readInput("$FOLDER/test")) == 79)
    check(part2(readInput("$FOLDER/test")) == 3621)

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