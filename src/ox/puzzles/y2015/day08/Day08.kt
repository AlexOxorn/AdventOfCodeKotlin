package ox.puzzles.y2015.day08

import ox.puzzles.Day
fun String.eval(): String {
    val proc = ProcessBuilder("python", "-c", "print($this, end='')")
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .start()
    return proc.inputStream.bufferedReader().readText()
}
fun String.repr(): String {
    val proc = ProcessBuilder("python", "-c", "import json; print(json.dumps(r'$this'), end='')")
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .start()
    return proc.inputStream.bufferedReader().readText()
}

class Day08 : Day {
    override fun part1i() = getBufferedReader(getInputName())
        .lineSequence()
        .map { it.length - it.eval().length }
        .sum()
    override fun part2i() = getBufferedReader(getInputName())
        .lineSequence()
        .map { it.repr().length - it.length }
        .sum()
}

fun main() {
    val d = Day08()
    println(d.part1i())
    println(d.part2i())
}

