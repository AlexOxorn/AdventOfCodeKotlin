package ox.puzzles.y2024.day07

import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

typealias Row = Pair<Long, List<Long>>

fun parseData(s: Scanner): Row {
    val line = s.nextLine()
    val parts = line.split(Regex(":? ")).map(String::toLong)
    val first = parts.first()
    val rest = parts.subList(1, parts.size)
    return first to rest
}

infix fun Long.cat(other: Long) = (this.toString() + other.toString()).toLong()

fun canEval(target: Long, rest: List<Long>, part2: Boolean, acc: Long = 0): Boolean {
    if (rest.isEmpty()) {
        return target == acc
    }
    if (acc > target)
        return false

    if (acc == 0L) {
        return canEval(target, rest.subList(1, rest.size), part2, rest.first())
    }

    return canEval(target, rest.subList(1, rest.size), part2, acc + rest.first()) ||
            (canEval(target, rest.subList(1, rest.size), part2, acc * rest.first())) ||
            (part2 && canEval(target, rest.subList(1, rest.size), true, acc cat rest.first()))
}

class Day07 : Day {
    override fun part1(): Long {
        return ResourceIterable(getInputName(), ::parseData).asSequence()
            .filter { (target, data) -> canEval(target, data, false) }
            .sumOf(Row::first)
    }

    override fun part2(): Long {
        return ResourceIterable(getInputName(), ::parseData).asSequence()
            .filter { (target, data) -> canEval(target, data, true) }
            .sumOf(Row::first)
    }
}

fun main() {
    val d = Day07()
    println(d.part1())
    println(d.part2())
}