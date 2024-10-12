package ox.puzzles

interface Day {
    val filename: String
    fun part1s(): String {
        return part1().toString()
    }
    fun part2s(): String {
        return part2().toString()
    }
    fun part1(): Int { return 0 }
    fun part2(): Int { return 0 }
}