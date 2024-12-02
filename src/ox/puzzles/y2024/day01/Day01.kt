package ox.puzzles.y2024.day01
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*
import java.util.stream.Collectors
import kotlin.math.abs

fun parseText(s: Scanner): Pair<Int, Int> = Pair(s.nextInt(), s.nextInt())

class Day01 : Day {
    override fun part1i(): Int {
        val (list1, list2) = ResourceIterable(getInputName(), ::parseText).unzip()
        val sorted1 = list1.sorted()
        val sorted2 = list2.sorted()
        return (sorted1 zip sorted2).sumOf { abs(it.first - it.second) }
    }

    override fun part2(): Long {
        val (list1, list2) = ResourceIterable(getInputName(), ::parseText).unzip()
        val counts1 = list1.stream().collect(Collectors.groupingBy({a: Int -> a}, Collectors.counting()))
        val counts2 = list2.stream().collect(Collectors.groupingBy({a: Int -> a}, Collectors.counting()))

        return counts1.map { it.key * it.value * counts2.getOrDefault(it.key, 0) }.sum()
    }
}

fun main() {
    println(Day01().part1())
    println(Day01().part2())
}