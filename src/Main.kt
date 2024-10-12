import ox.puzzles.y2021.*

fun main() {
    val days = listOf(
        Day01("inputs/2021/day01_input.txt"),
        Day02("inputs/2021/day02_input.txt"),
        Day03("inputs/2021/day03_input.txt"),
        Day04("inputs/2021/day04_input.txt"),
        Day05("inputs/2021/day05_input.txt"),
    )
    for (d in days) {
        println(d.part1s())
        println(d.part2s())
        println()
    }
}