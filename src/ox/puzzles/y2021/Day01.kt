package ox.puzzles.y2021

import ox.puzzles.Day
import ox.puzzles.ScanIterable
import ox.lib.itertools.zipWithNext
import java.util.Scanner

class Day01(override val filename: String) : Day {
    override fun part1() : Int {
        val input = ScanIterable<Int>(filename, Scanner::nextInt)
        val deltas = input.zipWithNext { a, b -> b > a }
        return deltas.count { it };
    }

    override fun part2() : Int {
        val input = ScanIterable<Int>(filename, Scanner::nextInt)
        val deltas = input.zipWithNext(3) { a, b -> b > a }
        return deltas.count { it };
    }
}