package ox.puzzles.y2024.day06

import ox.lib.util.Grid2D
import ox.lib.util.GridIndex
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import ox.puzzles.y2021.Grid

import java.util.*

fun findStart(grid: Grid2D<Int>) = grid.indices().find { (i, j) -> grid[i, j] == START }!!

const val NONE = 0
const val UP = 1 shl 1
const val DOWN = 1 shl 2
const val LEFT = 1 shl 3
const val RIGHT = 1 shl 4
const val OBSTACLE = 1 shl 5
const val START = 1 shl 6
const val VISITED = UP or DOWN or LEFT or RIGHT
const val VERTICAL = UP or DOWN
const val HORIZONTAL = RIGHT or LEFT

fun inputToVal(c: Char): Int {
    return when (c) {
        '.' -> NONE
        '#' -> OBSTACLE
        '^' -> START
        else -> {
            throw IllegalArgumentException("Bad Char $c")
        }
    }
}

fun turn90(dir: Int): Int {
    return when (dir) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
        else -> {
            throw IllegalArgumentException("Bad Dir $dir")
        }
    }
}

fun moveForward(dir: Int, pos: GridIndex): GridIndex {
    val (i, j) = pos
    return when (dir) {
        UP -> pos.up()
        RIGHT -> pos.right()
        DOWN -> pos.down()
        LEFT -> pos.left()
        else -> {
            throw IllegalArgumentException("$i,$j -> $dir")
        }
    }
}

fun printGrid(g: Grid2D<Int>, color: Int = 31) {
    val esc = Char(27)
    g.listData.chunked(g.width).forEach { row ->
        row.forEach {
            if (it == OBSTACLE)
                print('#')
            else if ((it and VISITED) != 0) {
                val isV = (it and VERTICAL) != 0
                val isH = (it and HORIZONTAL) != 0
                if (isV && isH)
                    print("$esc[${color}m+$esc[0m")
                else if (isV)
                    print("$esc[${color}m|$esc[0m")
                else
                    print("$esc[${color}m-$esc[0m")
            } else {
                print('.')
            }
        }
        println()
    }
    println()
}

fun step(grid: Grid2D<Int>, pos: GridIndex, dir: Int): Pair<Int, GridIndex?> {
    val (i, j) = pos
    val curr = grid[i, j]
    val (i2, j2) = moveForward(dir, pos)
    val next = grid.getOrNull(i2, j2)

    grid[i, j] = curr or dir

    return when (next) {
        null -> dir to null
        OBSTACLE -> turn90(dir) to pos
        else -> dir to GridIndex(i2, j2)
    }
}

fun move(grid: Grid2D<Int>, pos: GridIndex, dir: Int, check: Boolean = false): Long {
    var curPos: GridIndex? = pos
    var curDir = dir
    var loopCount = 0L

    while (curPos != null) {
        val (newDir, newPos) = step(grid, curPos, curDir)
        curPos = newPos
        curDir = newDir

        if (newPos == null)
            continue

        if (!check) {
            if ((grid[newPos.x, newPos.y] and curDir) != 0) {
                return 1
            }
        } else if (checkLoop(grid, newPos, curDir)) {
            loopCount++
        }
    }

    return loopCount
}

fun checkLoop(grid: Grid2D<Int>, pos: GridIndex, dir: Int): Boolean {
    val (i2, j2) = moveForward(dir, pos)
    val next = grid.getOrNull(i2, j2)
    if (next == OBSTACLE || next == null || ((next and VISITED) != 0))
        return false

    val g2 = grid.copy()
    g2[i2, j2] = OBSTACLE
    val turnedRight = turn90(dir)
    return move(g2, pos, turnedRight) == 1L
}

class Day06 : Day {
    private fun getGrid(): Grid2D<Int> {
        val input =
            ResourceIterable(getInputName(), Scanner::nextLine)
                .asSequence()
                .map { it.asSequence().map(::inputToVal).toList() }
                .asIterable()

        return Grid2D(input)
    }

    override fun part1(): Long {
        val grid = getGrid()
        val pos: GridIndex = findStart(grid)
        move(grid, pos, UP)
        return grid.listData.count { (it and VISITED) != 0 }.toLong()
    }

    override fun part2(): Long {
        val grid = getGrid()
        val pos: GridIndex = findStart(grid)
        return move(grid, pos, UP, true)
    }
}

fun main() {
    val day = Day06()
    println(day.part1())
    println(day.part2())
}