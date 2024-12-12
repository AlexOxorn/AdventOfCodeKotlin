package ox.puzzles.y2024.day11

import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

data class Stone(val value: String = "0") {
    companion object {
        val dp = HashMap<Stone, List<Stone>>()
        val dpCounts = HashMap<Pair<Stone, Int>, Long>()
    }
    private fun applyRule(): List<Stone> {
        if (dp.contains(this)) {
            return dp[this]!!
        }

        val res = when {
            value == "0" -> listOf(Stone("1"))
            value.length % 2 == 0 -> listOf(
                Stone(value.substring(0..<value.length/2)),
                Stone(value.substring(value.length/2..<value.length).toLong().toString())
            )
            else -> listOf(Stone((value.toLong() * 2024).toString()))
        }

        dp[this] = res
        return res
    }

    fun getCount(iterations: Int): Long {
        if (dpCounts.contains(this to iterations)) {
            return dpCounts[this to iterations]!!
        }

        val res = when(iterations) {
            0 -> 1L
            1 -> applyRule().size.toLong()
            else -> applyRule().sumOf { it.getCount(iterations - 1) }
        }

        dpCounts[this to iterations] = res
        return res
    }
}

class Day11 : Day {
    val data: List<Stone> = ResourceIterable(getInputName(), Scanner::next).map(::Stone)

    private fun solve(iterations: Int): Long {
        return data.sumOf { it.getCount(iterations) }
    }

    override fun part1(): Long {
        return solve(25)
    }

    override fun part2(): Long {
        return solve(75)
    }
}

fun main() {
    val d = Day11()
    println(d.part1())
    println(d.part2())
}