package ox.puzzles.y2015.day05

import ox.puzzles.Day

val vowels = arrayOf('a', 'e', 'i', 'o', 'u')
val badWords = arrayOf("ab", "cd", "pq", "xy")

fun String.threeVowels(): Boolean {
    return this.count { it in vowels } >= 3
}

fun String.doubleLetters(): Boolean {
    return this.asSequence().windowed(2).any { it[0] == it[1] }
}

fun String.badWords(): Boolean {
    return this.asSequence().windowed(2).map { "${it[0]}${it[1]}" }.any { it in badWords }
}

fun String.goodWords() = !badWords()

fun String.repeatPair(): Boolean {
    val pairs = HashSet<String>()
    val pairIndex = HashSet<Pair<Int, String>>()
    for (it in this.asSequence()
        .withIndex()
        .windowed(2)
        .map { it[0].index to "${it[0].value}${it[1].value}" }) {

        if (pairIndex.contains(it.first - 1 to it.second))
            continue
        if (pairs.contains(it.second))
            return true
        pairs.add(it.second)
        pairIndex.add(it)
    }
    return false
}

fun String.sandwich(): Boolean {
    return this.asSequence().windowed(3).any { it[0] == it[2] }
}

class Day05 : Day {
    override fun part1(): Long {
        return getBufferedReader(getInputName())
            .lines()
            .filter(String::threeVowels)
            .filter(String::doubleLetters)
            .filter(String::goodWords)
            .count()
    }

    override fun part2(): Long {
        return getBufferedReader(getInputName())
            .lines()
            .filter(String::repeatPair)
            .filter(String::sandwich)
            .count()
    }
}

fun main() {
    val d = Day05()
    println(d.part2())
}