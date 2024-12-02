package ox.puzzles.y2015.day06

import ox.lib.util.Grid2D
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*
import java.util.regex.Pattern
import kotlin.math.max

const val LightsSize = 1000

class Lights : Grid2D<Int>(LightsSize, LightsSize, { 0 }) {
    private val apply1 = { on: LightInst, x: Int, y: Int ->
        when (on) {
            LightInst.ON -> this[x, y] = 1
            LightInst.OFF -> this[x, y] = 0
            LightInst.TOGGLE -> this[x, y] = if (this[x, y] == 1) 0 else 1
            LightInst.ERROR -> throw IllegalArgumentException("Bad Instruction")
        }
    }
    private val apply2 = { on: LightInst, x: Int, y: Int ->
        when (on) {
            LightInst.ON -> this[x, y] += 1
            LightInst.OFF -> this[x, y] = max(0, this[x, y] - 1)
            LightInst.TOGGLE -> this[x, y] += 2
            LightInst.ERROR -> throw IllegalArgumentException("Bad Instruction")
        }
    }

    private fun applyInstructionImp(inst: Instruction, applyPart: (LightInst, Int, Int) -> Unit) {
        val (fromX, fromY) = inst.from
        val (toX, toY) = inst.to
        for (x in fromX..toX) {
            for (y in fromY..toY) {
                applyPart(inst.on, x, y)
            }
        }
    }

    fun applyInstruction1(inst: Instruction) = applyInstructionImp(inst, apply1)
    fun applyInstruction2(inst: Instruction) = applyInstructionImp(inst, apply2)

    fun count() = listData.count { it == 1 }
    fun total() = listData.sum()
}

enum class LightInst {
    ON, OFF, TOGGLE, ERROR;

    companion object {
        fun fromMessage(msg: String): LightInst {
            return when (msg) {
                "turn on" -> ON
                "turn off" -> OFF
                "toggle" -> TOGGLE
                else -> ERROR
            }
        }
    }
}

data class Instruction(val on: LightInst, val from: Pair<Int, Int>, val to: Pair<Int, Int>) {
    companion object {
        fun parse(s: Scanner): Instruction {
            val r: Pattern = Pattern.compile("(turn on|turn off|toggle) (\\d+),(\\d+) through (\\d+),(\\d+)")
            val line = s.nextLine()
            val m = r.matcher(line)
            m.find()
            return Instruction(
                LightInst.fromMessage(m.group(1)),
                m.group(2).toInt() to m.group(3).toInt(),
                m.group(4).toInt() to m.group(5).toInt(),
            )
        }
    }
}

class Day06 : Day {
    override fun part1i(): Int {
        val lightGrid = Lights()
        ResourceIterable(getInputName(), Instruction::parse).forEach(lightGrid::applyInstruction1)
        return lightGrid.count()
    }

    override fun part2i(): Int {
        val lightGrid = Lights()
        ResourceIterable(getInputName(), Instruction::parse).forEach(lightGrid::applyInstruction2)
        return lightGrid.total()
    }
}

fun main() {
    val d = Day06()
    println(d.part1())
    println(d.part2())
}