package ox.puzzles.y2015.day02

import ox.puzzles.Day
import ox.puzzles.ResourceIterable
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

fun parsePrism(s: Scanner) : Prism {
    s.useDelimiter("[x\n]")
    return Prism(s.nextInt(), s.nextInt(), s.nextInt())
}

class Day02 : Day {
    override fun part1i(): Int {
        return ResourceIterable(getInputName(), ::parsePrism).map(Prism::wrappingPaper).sum()
    }
    override fun part2i(): Int {
        return ResourceIterable(getInputName(), ::parsePrism).map(Prism::ribbon).sum()
    }
}

fun main() {
    val day = Day02()
    println(day.part1())
    println(day.part2())
}