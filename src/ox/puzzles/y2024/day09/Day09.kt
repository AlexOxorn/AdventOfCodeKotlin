package ox.puzzles.y2024.day09
import ox.puzzles.Day
import java.util.*
import kotlin.jvm.optionals.getOrDefault

typealias DiskData = List<Optional<Int>>

fun parseLine(line: String): DiskData {
    val ids = line.indices.map { it / 2 }
    return line.asSequence()
        .withIndex()
        .zip(ids.asSequence())
        .flatMap { (char, id) ->
            if (char.index % 2 == 0)
                List(char.value - '0') { Optional.of(id) }
            else
                List(char.value - '0') { Optional.empty() }
        }.toList()
}

fun defrag(data: DiskData) : DiskData {
    val mulData = data.toMutableList()
    var head = 0
    var tail = data.lastIndex
    while (head <= tail) {
        if (mulData[head].isPresent) {
            head++
            continue
        }

        if (mulData[tail].isEmpty) {
            tail--
            continue
        }

        mulData[head] = mulData[tail]
        mulData[tail] = Optional.empty()
    }

    return mulData
}

fun findSubListOfSize(data: DiskData, target: Int): Optional<Int> {
    val start = data.indexOf(Optional.empty())
    if (start < 0)
        return Optional.empty()

    val size = data.subList(start, data.size).indexOfFirst { it.isPresent }
    if (size >= target)
        return Optional.of(start)

    return findSubListOfSize(data.subList(start + size, data.size), target).map { it + size + start }
}

fun superDefrag(data: DiskData): DiskData {
    val mulData = data.toMutableList()
    for (currId in (data.last().get()downTo 1)) {
        val idIndex = mulData.indexOf(Optional.of(currId))
        val size = mulData.lastIndexOf(Optional.of(currId)) - idIndex + 1
        val emptySpace = findSubListOfSize(mulData.subList(0, idIndex+1), size)
        if (emptySpace.isEmpty) {
            continue
        }
        for (i in 0..<size) {
            mulData[emptySpace.get() + i] = mulData[idIndex + i]
            mulData[idIndex + i] = Optional.empty()
        }
    }

    return mulData
}

fun checksum(data: DiskData): Long {
    return data.asSequence()
        .withIndex()
        .sumOf { (i, v) -> i.toLong() * v.getOrDefault(0).toLong() }
}

class Day09 : Day {
    val line: String = getBufferedReader(getInputName()).readLine()
    private val data: DiskData = parseLine(line)

    override fun part1(): Long {
        val newData = defrag(data)
        return checksum(newData)
    }

    override fun part2(): Long {
        val newData = superDefrag(data)
        return checksum(newData)
    }

}

fun main() {
    val d = Day09()
    println(d.part1())
    println(d.part2())
}