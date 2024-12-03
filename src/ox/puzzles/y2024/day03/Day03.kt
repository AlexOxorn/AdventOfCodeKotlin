package ox.puzzles.y2024.day03

import ox.puzzles.Day

class Day03 : Day {
    override fun part1(): Long {
        val line = getResource(getInputName()).reader().readText()
        val p = Regex("""mul\((\d\d?\d?),(\d\d?\d?)\)""")
        return p.findAll(line).map { it.destructured }.sumOf { (first, second) -> first.toLong() * second.toLong() }
    }


    override fun part2(): Long {
        val line = getResource(getInputName()).reader().readText()
        val p = Regex("""mul\((\d\d?\d?),(\d\d?\d?)\)|do\(\)|don't\(\)""")

        var enabled = true
        var result = 0L
        for (match in p.findAll(line)) {
            when {
                match.value == "do()" -> enabled = true
                match.value == "don't()" -> enabled = false
                enabled -> result += match.groupValues[1].toLong() * match.groupValues[2].toLong()
            }
        }
        return result
    }
}

fun main() {
    println(Day03().part1())
    println(Day03().part2())
}