package ox.puzzles.y2024.day22

import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*
import java.util.stream.Collectors
import kotlin.streams.asStream

infix fun Long.mix(new: Long) = this xor new
fun Long.prune() = this and ((2 shl 23) - 1)

fun next(secret: Long): Long {
    return listOf(
        { it: Long -> it shl 6 },
        { it: Long -> it shr 5 },
        { it: Long -> it shl 11 },
    ).fold(secret) { part, n -> (part mix (n(part))).prune() }
}

fun getDiffMap(seed: Long): Map<List<Long>, Long> =
    generateSequence(seed, ::next)
    .take(2001)
    .map { it % 10 }
    .zipWithNext { l, r -> r to (r - l) }
    .windowed(4)
    .asStream()
    .parallel()
    .collect(
        Collectors.toMap(
        { it.map(Pair<Long, Long>::second) },
        { it.last().first },
        { l, _ -> l }
    )).withDefault { 0 }

class Day22 : Day {
    private val startingSecrets = ResourceIterable(getInputName(), Scanner::nextLong).toList()

    override fun part1(): Long {
        return startingSecrets.sumOf { generateSequence(it, ::next).take(2001).last() }
    }

    override fun part2(): Long {
        val maps = startingSecrets.map(::getDiffMap)
        val keys = maps.flatMap { it.keys }.toSet()
        return keys.parallelStream()
            .mapToLong { diff -> maps.sumOf { map -> map.getValue(diff) } }
            .max()
            .orElseThrow()
    }
}

fun main() {
    val d = Day22()
    println(d.part2())
}