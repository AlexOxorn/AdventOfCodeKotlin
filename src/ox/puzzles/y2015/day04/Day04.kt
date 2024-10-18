package ox.puzzles.y2015.day04

import ox.puzzles.Day
import java.security.MessageDigest

@OptIn(ExperimentalStdlibApi::class)
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.toHexString()
}


class Day04 : Day {
    private fun solve(n: Int): Int {
        val key = getBufferedReader(getInputName()).readText()
        val zeroes = "0".repeat(n)
        for (i in generateSequence(1) { it + 1 }) {
            val msg = key + i
            val hash = msg.md5()
            if (hash.take(n) == zeroes)
                return i
        }
        return 0
    }

    override fun part1() = solve(5)

    override fun part2() = solve(6)
}

fun main() {
    val day = Day04()
    println(day.part1())
    println(day.part2())
}