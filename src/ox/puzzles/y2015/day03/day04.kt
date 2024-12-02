package ox.puzzles.y2015.day03

import ox.lib.util.InfiniteGrid2D
import ox.puzzles.Day

class Houses : InfiniteGrid2D<Int>() {
    init {
        listData = listData.withDefault { 0 }
    }

    fun countHouses(): Int {
        return listData.count { it.value > 0 }
    }
}

class Day03 : Day {
    override fun part1i(): Int {
        val houses = Houses()
        var currentX = 0
        var currentY = 0
        houses[currentX, currentY] += 1

        val line = getBufferedReader(getInputName()).readText()
        for (c in line) {
            when (c) {
                '>' -> currentX += 1
                '<' -> currentX -= 1
                'v' -> currentY += 1
                '^' -> currentY -= 1
            }
            houses[currentX, currentY] += 1
        }

        return houses.countHouses()
    }

    override fun part2i(): Int {
        val houses = Houses()
        var currentSantaX = 0
        var currentSantaY = 0
        var currentRobotX = 0
        var currentRobotY = 0
        houses[currentSantaX, currentSantaY] += 1

        val line = getBufferedReader(getInputName()).readText()
        for (c in line.asSequence().chunked(2)) {
            when (c[0]) {
                '>' -> currentSantaX += 1
                '<' -> currentSantaX -= 1
                'v' -> currentSantaY += 1
                '^' -> currentSantaY -= 1
            }
            when (c[1]) {
                '>' -> currentRobotX += 1
                '<' -> currentRobotX -= 1
                'v' -> currentRobotY += 1
                '^' -> currentRobotY -= 1
            }
            houses[currentSantaX, currentSantaY] += 1
            houses[currentRobotX, currentRobotY] += 1
        }

        return houses.countHouses()
    }
}

fun main() {
    val day = Day03()
    println(day.part1())
    println(day.part2())
}