package ox.puzzles.y2024.day23

import ox.lib.itertools.cross
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

typealias Connection = Pair<String, String>

fun parseLine(s: Scanner): Connection {
    val line = s.nextLine()
    val (l, r) = line.split('-')
    return l to r
}

fun bronKerbosch(
    graph: Map<String, Set<String>>,
    r: Set<String> = emptySet(),
    p: MutableSet<String> = graph.keys.toMutableSet(),
    x: MutableSet<String> = mutableSetOf()
): Sequence<Set<String>> = sequence {
    if (p.isEmpty() && x.isEmpty()) {
        yield(r)
        return@sequence
    }

    val u = p.firstOrNull() ?: x.first()
    for (v in p - graph.getValue(u)) {
        yieldAll(
            bronKerbosch(
                graph,
                r union setOf(v),
                (p intersect graph.getValue(v)).toMutableSet(),
                (x intersect graph.getValue(v)).toMutableSet()
            )
        )
        p.remove(v)
        x.add(v)
    }
}

class Day23 : Day {
    private val connections = ResourceIterable(getInputName(), ::parseLine)
        .flatMap { (l, r) -> listOf(l to r, r to l) }
        .groupBy(Connection::first, Connection::second)
        .mapValues { (k, v) -> v.toSet() }

    override fun part1i() = connections
        .filterKeys { it.startsWith('t') }
        .flatMap { (start, conn) ->
            (conn cross conn)
                .filter { (l, r) -> connections.getValue(l).contains(r) }
                .map { (l, r) -> setOf(start, l, r) }
        }
        .toSet()
        .count()

    override fun part2s() =
        bronKerbosch(connections)
            .maxBy { it.size.toLong() }
            .sorted()
            .joinToString(",")
}

fun main() {
    val d = Day23()
    println(d.part1())
    println(d.part2s())
}