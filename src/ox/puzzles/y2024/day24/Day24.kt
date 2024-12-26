package ox.puzzles.y2024.day24

import ox.puzzles.Day
import ox.puzzles.ScanIterable
import java.util.*

data class Connection(
    val output: String,
    var result: Boolean?,
    val input1: String?,
    val input2: String?,
    val op: ((Boolean, Boolean) -> Boolean)?,
    val opString: String?
) {
    companion object {
        fun opFromStr(op: String) = when (op) {
            "OR" -> Boolean::or
            "AND" -> Boolean::and
            "XOR" -> Boolean::xor
            else -> throw IllegalArgumentException()
        }
    }

    constructor(o: String, res: Boolean) : this(o, res, null, null, null, null)
    constructor(o: String, in1: String, in2: String, opStr: String) : this(o, null, in1, in2, opFromStr(opStr), opStr)

    fun getOutput(connections: Map<String, Connection>): Boolean {
        if (result != null)
            return result!!

        result = op!!(
            connections.getValue(input1!!).getOutput(connections),
            connections.getValue(input2!!).getOutput(connections)
        )
        return result!!
    }
}

fun parseConnection(s: Scanner): Connection {
    var line = s.nextLine()
    if (line.isBlank())
        line = s.nextLine()

    if (line.contains(':')) {
        val (name, value) = line.split(": ")
        return Connection(name, value == "1")
    }
    val (in1, opStr, in2, _, out) = line.split(' ')
    return Connection(out, in1, in2, opStr)
}

fun makeGraph(connections: Map<String, Connection>): String {
    val opColor = { op: String ->
        when (op) {
            "OR" -> "green"
            "AND" -> "red"
            "XOR" -> "blue"
            else -> throw IllegalArgumentException()
        }
    }
    return buildString {
        appendLine("digraph G {")
        appendLine("\tnode [style=filled]")
        for ((out, node) in connections) {
            appendLine("\t$out")
            if (node.input1 != null && node.input2 != null) {
                appendLine("\t${node.input1} -> $out [color=${opColor(node.opString!!)}]")
                appendLine("\t${node.input2} -> $out [color=${opColor(node.opString)}]")
            }
        }
        val keys = connections.keys
            .filter { key -> "xyz".any { key.startsWith(it) } }
            .groupBy {
                if (it.startsWith('z'))
                    it.substring(1).toInt() + 1
                else
                    it.substring(1).toInt()
            }

        for ((rank, nodes) in keys) {
            appendLine("\t{rank=same; ${nodes.joinToString("; ")}}")
        }
        appendLine("}")
    }
}

fun toNumber(l: Sequence<Boolean>) =
    l.withIndex()
        .filter(IndexedValue<Boolean>::value)
        .sumOf { (i, v) -> 1L shl i }

fun getValueFunc(connection: Map<String, Connection>) = { char: Char ->
    val bits = connection.keys.filter { it.startsWith(char) }.sorted()
    toNumber(bits.asSequence().map { connection.getValue(it).getOutput(connection) })
}

class Day24 : Day {
    private val scanner = getScanner(getInputName())
    private val connections = ScanIterable(scanner, ::parseConnection).associateBy { it.output }

    override fun part1(): Long {
        return getValueFunc(connections)('z')
    }

    override fun part2s(): String {
        val swaps = listOf(
            listOf("z31", "hpc"),
            listOf("z06", "hwk"),
            listOf("z37", "cgr"),
            listOf("tnt", "qmd")
        )

        val fixedConnections = connections.toMutableMap()
        for ((x, y) in swaps) {
            val tmp = fixedConnections[x]!!
            fixedConnections[x] = fixedConnections[y]!!
            fixedConnections[y] = tmp
        }

        val getValue = getValueFunc(fixedConnections)
        assert(getValue('x') + getValue('y') == getValue('z'))
        return swaps.flatten().sorted().joinToString(",")
    }
}

fun main() {
    val d = Day24()
    println(d.part1s())
    println(d.part2s())
}