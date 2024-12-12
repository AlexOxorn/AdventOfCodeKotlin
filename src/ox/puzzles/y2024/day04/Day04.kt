package ox.puzzles.y2024.day04

import ox.lib.util.CharacterGrid
import ox.lib.util.GridIndex
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*
import kotlin.collections.HashSet

var posSet1: HashSet<Pair<Int, Int>> = HashSet()
var posSet2: HashSet<Pair<Int, Int>> = HashSet()

fun printBoard(cross: CharacterGrid, set: HashSet<Pair<Int, Int>>) {
    val esc = (27).toChar()
    val toRed = "$esc[31m"
    val toNorm = "$esc[0m"

    cross.indices().forEach { (i, j) ->
        if (i == 0 && j != 0)
            println()
        if (set.contains(i to j))
            print(toRed)
        print("${cross[i, j]}$toNorm")
    }
    println()
}

fun checkSubWordPart(cross: CharacterGrid, start: GridIndex, dir: Pair<Int, Int>, wordLength: Int = 4): Boolean {
    val indices = (0..<wordLength).map { start.x + dir.first * it to start.y + dir.second * it }
    val chars = indices.map { (i, j) -> cross.getOrNull(i, j) }
    if (chars.any { it == null })
        return false
    val word = chars.joinToString(separator = "")
    if (word != "XMAS")
        return false

    posSet1.addAll(indices)
    return true
}

fun checkCrossWordPart(cross: CharacterGrid, start: GridIndex, dir1: Pair<Int, Int>): Boolean {
    val indices1 = (-1..1).map { start.x + dir1.first * it to start.y + dir1.second * it }
    val chars1 = indices1.map { (i, j) -> cross.getOrNull(i, j) }
    if (chars1.any { it == null })
        return false
    val word1 = chars1.joinToString(separator = "")
    if (word1 != "MAS" && word1 != "SAM")
        return false

    posSet2.addAll(indices1)
    return true
}

fun checkSubWord(cross: CharacterGrid, start: GridIndex): Int {
    return listOf(
        1 to 0,
        1 to 1,
        0 to 1,
        -1 to 1,
        -1 to 0,
        -1 to -1,
        0 to -1,
        1 to -1,
    ).count { checkSubWordPart(cross, start, it) }
}

fun checkCrossWord(cross: CharacterGrid, start: GridIndex): Int {
    return if (listOf(1 to 1, 1 to -1).all { checkCrossWordPart(cross, start, it) }) 1 else 0
}

fun solve1(cross: CharacterGrid) = solve(cross, 'X', ::checkSubWord)

fun solve2(cross: CharacterGrid) = solve(cross, 'A', ::checkCrossWord)

fun solve(cross: CharacterGrid, checkChar: Char, checkFunc: (CharacterGrid, GridIndex) -> Int): Int {
    val indices = cross.indices()
    val startsWithX = indices.filter { (i, j) -> cross.getOrNull(i, j) == checkChar }
    return startsWithX
        .sumOf { start -> checkFunc(cross, start) }
}

class Day04 : Day {
    override fun part1(): Long {
        val input = ResourceIterable(getInputName(), Scanner::nextLine)
        val grid = CharacterGrid(input)
        val res = solve1(grid).toLong()
        return res
    }


    override fun part2(): Long {
        val input = ResourceIterable(getInputName(), Scanner::nextLine)
        val grid = CharacterGrid(input)
        val res = solve2(grid).toLong()
        return res
    }
}

fun main() {
    println(Day04().part1())
    println(Day04().part2())
}