package ox.puzzles.y2021

import ox.puzzles.Day
import ox.lib.itertools.zipWithNext
import ox.puzzles.FileIterable
import java.util.Scanner

class Day01(private val filename: String) : Day {
    override fun part1i() : Int {
        val input = FileIterable(filename, Scanner::nextInt)
        val deltas = input.zipWithNext { a, b -> b > a }
        return deltas.count { it }
    }

    override fun part2i() : Int {
        val input = FileIterable(filename, Scanner::nextInt)
        val deltas = input.zipWithNext(3) { a, b -> b > a }
        return deltas.count { it }
    }
}