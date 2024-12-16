package ox.puzzles.y2024.day13

import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

import org.jetbrains.kotlinx.multik.api.linalg.solve
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.operations.toList
import kotlin.math.roundToLong

typealias Coord = Pair<Long, Long>

fun solveLinear(a: Coord, b: Coord, prize: Coord) : Coord {
    val claw: D2Array<Double> = mk.ndarray(
        arrayOf(
            doubleArrayOf(a.first.toDouble(), b.first.toDouble()),
            doubleArrayOf(a.second.toDouble(), b.second.toDouble()),
        )
    )
    val dest = mk.ndarray(doubleArrayOf(prize.first.toDouble(), prize.second.toDouble()))

    val (x, y) = mk.linalg.solve(claw, dest).toList()
    return x.roundToLong() to y.roundToLong()
}

fun parse(r: Regex) = { s: Scanner ->
    val matches = r.matchEntire(s.nextLine())!!
    matches.groupValues[1].toLong() to matches.groupValues[2].toLong()
}

val parseButton = parse(Regex("""Button [AB]: X\+(\d+), Y\+(\d+)"""))
val parsePrize = parse(Regex("""Prize: X=(\d+), Y=(\d+)"""))

data class ClawMachine(val buttonA: Coord, val buttonB: Coord, val prize: Coord) {
    constructor(s: Scanner) : this(parseButton(s), parseButton(s), parsePrize(s)) {
        if (s.hasNextLine())
            s.nextLine()
    }
    fun part2(upgrade: Long = 10000000000000) =
        ClawMachine(buttonA, buttonB, (upgrade + prize.first to upgrade + prize.second))
}

fun verify(c: ClawMachine, sol: Coord, proj: (Coord) -> Long): Boolean {
    val left = proj(c.buttonA) * sol.first + proj(c.buttonB) * sol.second
    return left == proj(c.prize)
}

class Day13 : Day {
    private val machines = ResourceIterable(getInputName(), ::ClawMachine).toList()

    private fun solve(part2: Boolean) : Long {
        return machines
            .asSequence()
            .map { if (part2) it.part2() else it }
            .map { it to solveLinear(it.buttonA, it.buttonB, it.prize) }
            .filter { (init, sol) -> verify(init, sol, Coord::first) }
            .filter { (init, sol) -> verify(init, sol, Coord::second) }
            .map(Pair<ClawMachine, Coord>::second)
            .sumOf { (a, b) -> 3 * a + b }
    }

    override fun part1(): Long {
        return solve(false)
    }

    override fun part2(): Long {
        return solve(true)
    }
}

fun main() {
    val d = Day13()
    println(d.part1())
    println(d.part2())
}