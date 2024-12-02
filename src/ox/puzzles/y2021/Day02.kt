package ox.puzzles.y2021

import ox.puzzles.Day
import ox.puzzles.FileIterable
import java.util.Scanner
import java.util.regex.Pattern

enum class DIR {
    Forward, Down, Up
}

data class Step(val dir: DIR, val magnitude: Int) {
    companion object {
        fun parse(s: Scanner): Step {
            val r: Pattern = Pattern.compile("(\\w+) (\\d+)")
            val line = s.nextLine()
            val m = r.matcher(line)
            m.find()
            return when(m.group(1)) {
                "forward" -> Step(DIR.Forward, m.group(2).toInt())
                "down" -> Step(DIR.Down, m.group(2).toInt())
                "up" -> Step(DIR.Up, m.group(2).toInt())
                else -> throw IllegalArgumentException()
            }
        }
    }
}
data class Coords(val forward: Int = 0, val depth: Int = 0, val aim: Int = 0) {
    fun result(): Int {
        return forward * depth
    }
}

fun moveVer1(c: Coords, d: Step): Coords {
    val (dir, mag) = d
    return when (dir) {
        DIR.Forward -> c.copy(forward = c.forward + mag)
        DIR.Down -> c.copy(depth = c.depth + mag)
        DIR.Up -> c.copy(depth = c.depth - mag)
    }
}

fun moveVer2(c: Coords, d: Step): Coords {
    val (dir, mag) = d
    return when (dir) {
        DIR.Forward -> c.copy(forward = c.forward + mag, depth = c.depth + c.aim * mag)
        DIR.Down -> c.copy(aim = c.aim + mag)
        DIR.Up -> c.copy(aim = c.aim - mag)
    }
}

class Day02(private val filename: String) : Day {
    override fun part1i(): Int {
        val steps = FileIterable(filename, Step::parse)
        val res = steps.fold(Coords(), ::moveVer1)
        return res.result()
    }
    override fun part2i(): Int {
        val steps = FileIterable(filename, Step::parse)
        val res = steps.fold(Coords(), ::moveVer2)
        return res.result()
    }
}