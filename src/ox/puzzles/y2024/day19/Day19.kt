package ox.puzzles.y2024.day19

import ox.lib.itertools.cartesianProduct
import ox.puzzles.Day
import ox.puzzles.ScanIterable
import java.util.*

class Day19 : Day {
    private val scanner = getScanner(getInputName())
    private val allPatterns = scanner.nextLine().split(", ").toSet()
    private val basisPatterns = getPatterns()
    private val designs: List<String>
    private val subStrDP: MutableMap<String, List<String>> = mutableMapOf()
    private val countDP: MutableMap<String, Long> = mutableMapOf("" to 1)

    init {
        scanner.nextLine()
        designs = ScanIterable(scanner, Scanner::nextLine).toList()
    }

    private fun getPatterns(): Set<String> {
        val toRemove = allPatterns.cartesianProduct(allPatterns)
            .map { (i, j) -> i + j }
            .filter(allPatterns::contains)
            .toSet()
        return (allPatterns - toRemove)
    }

    private fun calculateCombinationMap(design: String) {
        if (subStrDP.contains(design)) return
        if (design.isEmpty()) { subStrDP[design] = emptyList(); return }
        val substrings = allPatterns.asSequence()
            .filter { design.startsWith(it) }
            .map { design.substring(it.length) }
            .toList()
        if (substrings.isEmpty()) return
        subStrDP[design] = substrings
        substrings.forEach(::calculateCombinationMap)
    }

    private fun combinationCount(design: String): Long {
        calculateCombinationMap(design)
        if (countDP.contains(design)) return countDP.getValue(design)
        val sum = subStrDP[design]?.sumOf { combinationCount(it) } ?: 0
        countDP[design] = sum
        return sum
    }

    private fun hasCombination(design: String): Boolean {
        if (design.isEmpty()) return true
        return basisPatterns.asSequence()
            .filter { design.startsWith(it) }
            .any { hasCombination(design.substring(it.length)) }
    }

    override fun part1i(): Int {
        return designs.count(::hasCombination)
    }

    override fun part2(): Long {
        return designs.sumOf(::combinationCount)
    }
}

fun main() {
    val d = Day19()
    println(d.part1())
    println(d.part2())
}