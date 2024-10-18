package ox.puzzles.y2021

import ox.lib.util.Grid2D
import ox.puzzles.Day
import ox.puzzles.FileIterable
import ox.puzzles.ScanIterable
import java.util.*

val bingoSize = 5
val poolSize = 99
typealias BingoInputs = ArrayList<Int>

var numberPool = MutableList(poolSize + 1) { false }

fun resetPool() {
    numberPool = MutableList(poolSize + 1) { false }
}

class BingoCard : Grid2D<Int>(bingoSize, bingoSize, { 0 }) {
    private fun checkVertical(): Boolean {
        return (0..<bingoSize).any { x: Int -> (0..<bingoSize).all { y: Int -> numberPool[this[x, y]] } }
    }
    private fun checkHorizontal(): Boolean {
        return (0..<bingoSize).any { y: Int -> (0..<bingoSize).all { x: Int -> numberPool[this[x, y]] } }
    }
    fun checkWin() = checkHorizontal() || checkVertical()
    fun getScore() = listData.filter { !numberPool[it] }.sum()
}

fun extractRoller(s: Scanner): BingoInputs {
    val inputsString = s.nextLine()
    s.nextLine()

    val sScan = Scanner(inputsString)
    sScan.useDelimiter(",")

    val toRet = BingoInputs()
    while (sScan.hasNextInt()) {
        toRet.add(sScan.nextInt())
    }
    return toRet
}

fun parseBingoCard(s: Scanner): BingoCard {
    val toRet = BingoCard()
    for (i in 0..<bingoSize) {
        for (j in 0..<bingoSize) {
            toRet[i, j] = s.nextInt()
        }
    }
    return toRet
}

fun init(filename: String): Pair<BingoInputs, MutableList<BingoCard>> {
    resetPool()
    val inputScanner = FileIterable(filename, ::parseBingoCard)
    return Pair(extractRoller(inputScanner.scan), inputScanner.toMutableList())
}

class Day04(val filename: String) : Day {
    override fun part1(): Int {
        val (roller, cards) = init(filename)
        for (ball in roller) {
            numberPool[ball] = true
            for (card in cards) {
                if (card.checkWin())
                    return ball * card.getScore()
            }
        }
        return -1
    }

    override fun part2(): Int {
        val (roller, cards) = init(filename)
        for (ball in roller) {
            numberPool[ball] = true
            if (cards.size == 1 && cards[0].checkWin())
                return ball * cards[0].getScore()
            cards.removeIf { it.checkWin() }
        }
        return -1
    }
}