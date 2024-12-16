package ox.puzzles.y2024.day14

import ox.lib.util.GridIndex
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*
import java.util.stream.Collectors

typealias Coord = GridIndex

class Robot {
    private val init: Coord
    private val speed: Coord
    var position: Coord

    constructor(r: Robot) {
        init = r.init
        speed = r.speed
        position = r.position
    }

    constructor(s: Scanner) {
        val re = Regex("""p=(\d+),(\d+) v=([\d\-]+),([\d\-]+)""")
        val line = s.nextLine()
        val matches = re.matchEntire(line)!!
        val (full, ix, iy, vx, vy) = matches.groupValues
        init = GridIndex(ix.toInt(), iy.toInt())
        speed = GridIndex(vx.toInt(), vy.toInt())
        position = init
    }

    fun move(bounds: GridIndex) {
        val (x, y) = position
        position = GridIndex(
            (x + speed.x + bounds.x) % bounds.x,
            (y + speed.y + bounds.y) % bounds.y,
        )
    }
}

class Day14 : Day {
    private val bounds = GridIndex(101, 103)
    private val machines = ResourceIterable(getInputName(), ::Robot).toList()

    override fun part1i(): Int {
        val copy = machines.map(::Robot).toList()
        for (i in 0..<100) {
            copy.forEach { it.move(bounds) }
        }

        val finalPositions = copy.map(Robot::position)
        val horizontalSplit = finalPositions.groupBy { it.x.compareTo(bounds.x / 2) }
        val leftVertical = horizontalSplit.getOrDefault(-1, emptyList()).groupBy { it.y.compareTo(bounds.y / 2) }
        val rightVertical = horizontalSplit.getOrDefault(1, emptyList()).groupBy { it.y.compareTo(bounds.y / 2) }

        return leftVertical.getOrDefault(-1, emptyList()).count() *
                leftVertical.getOrDefault(1, emptyList()).count() *
                rightVertical.getOrDefault(-1, emptyList()).count() *
                rightVertical.getOrDefault(1, emptyList()).count()
    }

    override fun part2i(): Int {
        val copy = machines.map(::Robot).toList()
        val answer = 7051

        for (i in 0..<answer) {
            copy.forEach { it.move(bounds) }
        }

        val positions = copy
            .stream()
            .map(Robot::position)
            .collect(Collectors.groupingBy({ it }, Collectors.counting()))

        for (y in 0..<bounds.y) {
            for (x in 0..<bounds.x) {
                val count = positions.getOrDefault(GridIndex(x, y), 0)
                print(if(count == 0L) " " else count.toString())
            }
            println()
        }
        return answer
    }
}

fun main() {
    val d = Day14()
    println(d.part1())
    println(d.part2())
}