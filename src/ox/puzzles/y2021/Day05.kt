package ox.puzzles.y2021

import ox.lib.util.Grid2D
import ox.puzzles.Day
import ox.puzzles.FileIterable
import ox.puzzles.Parsable
import ox.puzzles.ScanIterable
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Point(var x: Int, var y: Int) : Comparable<Point> {
    override operator fun compareTo(other: Point): Int {
        val first = x.compareTo(other.x)
        if (first != 0)
            return first
        return y.compareTo(other.y)
    }
}

data class Line(var p1: Point, var p2: Point) {
    fun vertical(): Boolean {
        return p1.x == p2.x;
    }

    fun horizontal(): Boolean {
        return p1.y == p2.y;
    }

    companion object {
        fun parse(s: Scanner): Line {
            s.useDelimiter(Pattern.compile(",| -> |\n"))
            return Line(
                Point(
                    s.nextInt(),
                    s.nextInt()
                ),
                Point(
                    s.nextInt(),
                    s.nextInt()
                )
            )
        }
    }
}

class Grid(b: Boolean = false) : Grid2D<Int>(1000, MutableList(1000 * 1000) { 0 }) {
    var diag = b;

    fun addLine(l: Line) {
        if (l.vertical()) {
            val horizontal = l.p1.x;
            val minY = min(l.p1.y, l.p2.y)
            val maxY = max(l.p1.y, l.p2.y)
            for (vertical in minY..maxY) {
                this[horizontal, vertical] = this[horizontal, vertical] + 1
            }
        } else if (l.horizontal()) {
            val vertical = l.p1.y;
            val minX = min(l.p1.x, l.p2.x)
            val maxX = max(l.p1.x, l.p2.x)
            for (horizontal in minX..maxX) {
                this[horizontal, vertical] = this[horizontal, vertical] + 1
            }
        } else if (this.diag) {
            val left = minOf(l.p1, l.p2)
            val right = maxOf(l.p1, l.p2)
            var sign = right.y - left.y
            val length = abs(sign)
            sign /= length
            for (i in 0..length) {
                this[left.x + i, left.y + sign * i] = this[left.x + i, left.y + sign * i] + 1;
            }
        }
    }

    fun countScore() : Int {
        return listData.count { it >= 2 }
    }
}

class Day05(val filename: String) : Day {
    override fun part1() : Int {
        val g = Grid()
        val lines = FileIterable(filename, Line::parse)
        for (l in lines) {
            g.addLine(l)
        }
        return g.countScore();
    }

    override fun part2() : Int {
        val g = Grid(true)
        val lines = FileIterable(filename, Line::parse)
        for (l in lines) {
            g.addLine(l)
        }
        return g.countScore();
    }
}