package ox.puzzles.y2024.day17

import ox.lib.itertools.cartesianProduct
import ox.puzzles.Day

/*
    TMP = A & 0b111
    PRINT([A >> (TMP ^ 0b001) ^ (TMP ^ 0b100)] & 0b111)
    A = A >> 3
 */

/*
    B = A % 8;
    B = B ^ 1;
    C = A >> B;
    B = B ^ 5;
    B = B ^ C;
    PRINT(B & 0b111)
    A = A >> 3
 */

fun stepPrecompiledPure(a: Long): Pair<Long, Int> {
    val tmp = a and 7L
    val print = (a shr (tmp xor 1L).toInt() xor (tmp xor 4L)) and 7L
    val newA = a shr 3
    return newA to print.toInt()
}

fun runPrecompiled(a: Long): String {
    var ref = a
    val output = mutableListOf<Int>()
    while (ref != 0L) {
        val (newA, print) = stepPrecompiledPure(ref)
        ref = newA
        output.add(print)
    }
    return output.joinToString(",")
}

infix fun List<Boolean>.xor(other: List<Boolean>) = (this zip other).map { (i, j) -> i xor j }
val listOf1 = listOf(true, false, false)
val listOf4 = listOf(false, false, true)
fun toNumber(l: List<Boolean>) =
    l.withIndex()
    .filter(IndexedValue<Boolean>::value)
    .sumOf{ (i, v) -> 1L shl i }

fun forEachPossible(reg: MutableList<Boolean?>, start: Int = 0) = sequence {
    val valid = { b: Boolean? -> if (b != null) listOf(b) else listOf(false, true) }
    val tests = cartesianProduct(
        *(2 downTo 0).map{ valid(reg[start+it]) }.toTypedArray()
    ).map(List<Boolean>::asReversed)

    for ((i, j, k) in tests) {
        reg[start] = i
        reg[start+1] = j
        reg[start+2] = k
        yield(reg.subList(start, start + 3) as List<Boolean>)
    }
}

fun solve(regA: List<Boolean?>, solution: List<Int>): Sequence<List<Boolean>> = sequence {
    try {
        if (solution.isEmpty()) {
            yield(regA.map { it ?: false })
            return@sequence
        }
        val target = solution.first()
        val regA2 = regA.toMutableList()

        val possible = forEachPossible(regA2)
            .map { (it xor listOf4) to toNumber(it xor listOf1) }
            .flatMap { (windowX4, shiftWindow) ->
                val regA3 = regA2.toMutableList()
                forEachPossible(regA3, shiftWindow.toInt())
                    .map { it xor windowX4 }
                    .map(::toNumber)
                    .filter { it.toInt() == target }
                    .flatMap { solve(regA3.subList(3, regA3.size), solution.subList(1, solution.size)) }
                    .map { (regA3.subList(0, 3) as List<Boolean>) + it }
            }
        yieldAll(possible)
    } catch (e: IndexOutOfBoundsException) {
        return@sequence
    }
}

class Day17 : Day {
    private val lines = getBufferedReader(getInputName()).readLines()
    private val regA = lines[0].substring("Register A: ".length).toLong()
    private val program = lines[4]
            .substring("Program: ".length)
            .splitToSequence(',')
            .map(String::toInt)
            .toList()

    override fun part1s(): String {
        return runPrecompiled(regA)
    }

    override fun part2(): Long {
        val answers = solve(List<Boolean?>(program.size * 3 + 7) { null }, program)
        val answerBits = answers.minBy(::toNumber)
        val answer = toNumber(answerBits)
        println(runPrecompiled(answer))
        return answer
    }
}

fun main() {
    val d = Day17()
    println(d.part1s())
    println(d.part2())
}