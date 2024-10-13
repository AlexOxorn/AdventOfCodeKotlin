package ox.puzzles.y2015

import ox.puzzles.Day
import ox.puzzles.ScanIterable
import java.util.Scanner

data class Prism(val x: Int, val y: Int, val z: Int) {
    private fun side() = intArrayOf(x, y, z)
    private fun area() = intArrayOf(x*y, y*z, z*x)
    private fun perimeter() = intArrayOf(2*x + 2*y, 2*y + 2*z, 2*z + 2*x)

    private fun surface() = area().sumOf { 2 * it }
    private fun volume() = side().fold(1, Int::times)

    fun wrappingPaper() = surface() + area().min()
    fun ribbon() = volume() + perimeter().min()
}

fun parsePrism(s: Scanner) : Prism{
    s.useDelimiter("[x\n]")
    return Prism(s.nextInt(), s.nextInt(), s.nextInt())
}

class Day02(override val filename: String) : Day {
    override fun part1(): Int {
        return ScanIterable(filename, ::parsePrism).map(Prism::wrappingPaper).sum()
    }
    override fun part2(): Int {
        return ScanIterable(filename, ::parsePrism).map(Prism::ribbon).sum()
    }
}

fun main() {
    println(Day02("inputs/2015/day02_input.txt").part1())
    println(Day02("inputs/2015/day02_input.txt").part2())
}