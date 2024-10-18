package ox.puzzles.y2021

import ox.lib.collectionExpansion.toUInt
import ox.lib.itertools.inplaceFold
import ox.puzzles.Day
import ox.puzzles.FileIterable
import ox.puzzles.ScanIterable
import java.util.*

val bitWidth = 12

fun bitSetFromInput(s: Scanner): BitSet {
    val l = s.nextInt(2)
    val ret = BitSet(bitWidth)
    (0..<bitWidth).filter { (1 shl it) and l != 0 }.forEach { ret.set(it) }
    return ret;
}

class BitCounter : ArrayList<Int>(MutableList(bitWidth) { 0 }) {
    private var count = 0

    operator fun plusAssign(other: BitSet) {
        (0..<bitWidth).filter { other[it] }.forEach { this[it]++ }
        count++
    }

    private fun commonInteger(): UInt {
        var ret = 0u
        (0..<bitWidth).filter { this[it] > (count + 1) / 2 }.forEach { ret += (1u shl it) }
        return ret
    }

    fun gammaEpsilon(): Pair<UInt, UInt> {
        val l = commonInteger()
        return Pair(l, (l.inv() % (1u shl bitWidth)))
    }
}

fun addReading(total: MutableList<Int>, reading: BitSet) {
    total[reading.toUInt().toInt()]++
}

fun recursiveDecent(l: List<Int>, trueBegin: Int, trueEnd: Int, c: (Int) -> Boolean): Int {
    var begin = trueBegin
    var end = trueEnd
    var dist = 0
    while (true) {
        dist = end - begin
        if (dist < 1)
            break
        val mid = begin + dist / 2
        val sum1 = (begin..<mid).map { l[it] }.sum()
        val sum2 = (mid..<end).map { l[it] }.sum()
        if (sum1 + sum2 == 1) {
            return l.subList(begin, end).indexOf(1) + begin - trueBegin
        }
        if (c(sum1.compareTo(sum2)))
            end = mid
        else
            begin = mid
    }
    return begin - trueBegin
}

class Day03(val filename: String) : Day {
    override fun part1(): Int {
        val lines = FileIterable(filename, ::bitSetFromInput)
        val result = lines.inplaceFold(BitCounter(), BitCounter::plusAssign)
        val (gamma, epsilon) = result.gammaEpsilon()
        return (gamma * epsilon).toInt()
    }

    override fun part2(): Int {
        val lines = FileIterable(filename, ::bitSetFromInput)
        val binaryTree = lines.inplaceFold(MutableList(1 shl bitWidth) { 0 }, ::addReading)
        val o2 = recursiveDecent(binaryTree, 0, binaryTree.size) { it > 0 }
        val co2 = recursiveDecent(binaryTree, 0, binaryTree.size) { it <= 0 }
        return o2 * co2
    }
}