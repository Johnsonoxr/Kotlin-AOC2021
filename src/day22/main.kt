package day22

import readInput
import kotlin.system.measureNanoTime

private const val FOLDER = "day22"

data class Aabb(val value: Int, val isStart: Boolean)

data class Range(val start: Int, val endExclude: Int) {

    fun toAabb(subtract: Boolean = false): List<Aabb> {
        return listOf(Aabb(start, !subtract), Aabb(endExclude, subtract))
    }

    fun split(vararg values: Int): List<Range> {
        val result = mutableListOf<Range>()
        var start = this.start
        for (value in values) {
            if (value in (start + 1)..<endExclude) {
                result.add(Range(start, value))
                start = value
            }
        }
        if (start < endExclude) {
            result.add(Range(start, endExclude))
        }
        return result
    }

    fun cutout(other: Range): List<Range> {
        return (toAabb() + other.toAabb(subtract = true)).toRanges()
    }

    fun cutout(otherRanges: Collection<Range>): List<Range> {
        return (toAabb() + otherRanges.toAabb(subtract = true)).toRanges()
    }

    fun union(other: Range): List<Range> {
        return (toAabb() + other.toAabb()).toRanges()
    }

    fun union(otherRanges: Collection<Range>): List<Range> {
        return (toAabb() + otherRanges.toAabb()).toRanges()
    }

    fun intersect(other: Range): List<Range> {
        return (toAabb() + other.toAabb()).toRanges(depthCriteria = 2)
    }

    fun intersect(otherRanges: Collection<Range>): List<Range> {
        return (toAabb() + otherRanges.toAabb()).toRanges(depthCriteria = 2)
    }
}

fun Collection<Range>.toAabb(subtract: Boolean = false): List<Aabb> {
    return flatMap { it.toAabb(subtract) }
}

fun Collection<Aabb>.toRanges(depthCriteria: Int = 1): List<Range> {
    var start: Int? = null
    var depth = 0
    val result = mutableListOf<Range>()
    for (aabb in sortedBy { it.value }) {
        if (aabb.isStart) {
            depth++
            if (depth == depthCriteria && start == null) {
                start = aabb.value
            }
        } else {
            depth--
            if (depth == depthCriteria - 1 && start != null) {
                result.add(Range(start, aabb.value))
            }
            start = null
        }
    }
    return result
}

fun Collection<Range>.cutout(other: Range): List<Range> {
    return (toAabb() + other.toAabb(subtract = true)).toRanges()
}

fun Collection<Range>.cutout(others: Collection<Range>): List<Range> {
    return (toAabb() + others.toAabb(subtract = true)).toRanges()
}

data class Cube(val xRange: Range, val yRange: Range, val zRange: Range) {

    fun volume(): Long {
        return (xRange.endExclude - xRange.start).toLong() * (yRange.endExclude - yRange.start).toLong() * (zRange.endExclude - zRange.start).toLong()
    }

    fun intersect(other: Cube): Cube? {
        val xIntersect = xRange.intersect(other.xRange).takeIf { it.isNotEmpty() } ?: return null
        val yIntersect = yRange.intersect(other.yRange).takeIf { it.isNotEmpty() } ?: return null
        val zIntersect = zRange.intersect(other.zRange).takeIf { it.isNotEmpty() } ?: return null
        return Cube(xIntersect.first(), yIntersect.first(), zIntersect.first())
    }

    fun cutout(other: Cube): List<Cube> {
        val intersectCube = intersect(other) ?: return listOf(this)
        val xDestructedRanges = xRange.split(intersectCube.xRange.start, intersectCube.xRange.endExclude)
        val yDestructedRanges = yRange.split(intersectCube.yRange.start, intersectCube.yRange.endExclude)
        val zDestructedRanges = zRange.split(intersectCube.zRange.start, intersectCube.zRange.endExclude)
        val destructedCubes = xDestructedRanges.flatMap { xDestructedRange ->
            yDestructedRanges.flatMap { yDestructedRange ->
                zDestructedRanges.map { zDestructedRange ->
                    Cube(xDestructedRange, yDestructedRange, zDestructedRange)
                }
            }
        }
        return destructedCubes - intersectCube
    }
}

data class Operation(val cube: Cube, val isTurnOn: Boolean)

fun main() {

    fun parseOperations(input: List<String>): List<Operation> {
        return input.map { line ->
            val isTurnOn = line.startsWith("on")
            val rangeNumbers = "(-?\\d+)".toRegex().findAll(line).map { it.value.toInt() }.toList()
            Operation(
                Cube(
                    Range(rangeNumbers[0], rangeNumbers[1] + 1),
                    Range(rangeNumbers[2], rangeNumbers[3] + 1),
                    Range(rangeNumbers[4], rangeNumbers[5] + 1)
                ),
                isTurnOn
            )
        }
    }

    fun part1(input: List<String>): Long {

        val operations = parseOperations(input)

        val limitedArea = Cube(
            Range(-50, 51),
            Range(-50, 51),
            Range(-50, 51)
        )

        var cubes = listOf<Cube>()
        operations.forEach { op ->
            cubes = if (op.isTurnOn) {
                if (cubes.isEmpty()) {
                    listOf(op.cube)
                } else {
                    cubes.flatMap { it.cutout(op.cube) } + op.cube
                }
            } else {
                cubes.flatMap { it.cutout(op.cube) }
            }
            cubes = cubes.mapNotNull { it.intersect(limitedArea) }
        }

        return cubes.sumOf { it.volume() }
    }

    fun part2(input: List<String>): Long {

        val operations = parseOperations(input)

        var cubes = listOf<Cube>()
        operations.forEach { op ->
            cubes = if (op.isTurnOn) {
                if (cubes.isEmpty()) {
                    listOf(op.cube)
                } else {
                    cubes.flatMap { it.cutout(op.cube) } + op.cube
                }
            } else {
                cubes.flatMap { it.cutout(op.cube) }
            }
        }

        return cubes.sumOf { it.volume() }
    }

    check(part1(readInput("$FOLDER/test")) == 590784L)
    check(part2(readInput("$FOLDER/test2")) == 2758514936282235L)

    val input = readInput("$FOLDER/input")
    val part1Result: Long
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