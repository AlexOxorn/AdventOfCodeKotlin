package ox.puzzles.y2015.day07

import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*
import java.util.regex.Pattern

enum class OP {
    MOV, AND, OR, LSHIFT, RSHIFT, NOT
}

data class Instruction(val op: OP, val input1: String? = null, val input2: String? = null, val output: String) {
    companion object {
        fun parse(s: Scanner): Instruction {
            val instruction: Pattern = Pattern.compile("(\\d+|\\w+) (?:(\\d+|\\w+)? )?(?:(\\d+|\\w+)? )?-> (\\d+|\\w+)")
            val line = s.nextLine()
            val m = instruction.matcher(line)
            m.find()

            if (m.group(2) == null) {
                // MOV
                return Instruction(op = OP.MOV, input1 = m.group(1), output = m.group(4))
            }
            if (m.group(3) == null) {
                // UNARY (NOT)
                return Instruction(op = OP.NOT, input1 = m.group(2), output = m.group(4))
            }
            val operator = when (m.group(2)) {
                "AND" -> OP.AND
                "OR" -> OP.OR
                "LSHIFT" -> OP.LSHIFT
                "RSHIFT" -> OP.RSHIFT
                else -> throw IllegalArgumentException("Bad Operator")
            }
            return Instruction(operator, m.group(1), m.group(3), m.group(4))
        }
    }
}

class MiniLanguage {
    private val variables = HashMap<String, UShort>()
    fun variablesView(): Map<String, UShort> = variables

    fun clear() = variables.clear()

    private fun getValue(v: String?): UShort? {
        if (v == null)
            return null
        return try {
            v.toUShort()
        } catch (_: IllegalArgumentException) {
            variables[v]
        }
    }

    private fun runInstruction(inst: Instruction) {
        val v1 = getValue(inst.input1)
        val v2 = getValue(inst.input2)
        if (v1 == null)
            return
        if (inst.input2 != null && v2 == null)
            return

        when (inst.op) {
            OP.MOV -> variables[inst.output] = v1
            OP.AND -> variables[inst.output] = v1 and v2!!
            OP.OR -> variables[inst.output] = v1 or v2!!
            OP.LSHIFT -> variables[inst.output] = v1 shl v2!!
            OP.RSHIFT -> variables[inst.output] = v1 ushr v2!!
            OP.NOT -> variables[inst.output] = v1.inv()
        }
    }

    fun runInstructions(insts: Iterable<Instruction>) = insts.forEach(this::runInstruction)
}

private infix fun UShort.shl(value: UShort) = (this.toUInt() shl value.toInt()).toUShort()
private infix fun UShort.ushr(value: UShort) = (this.toUInt() shr value.toInt()).toUShort()

class Day07 : Day {
    private val instructions = ResourceIterable(getInputName(), Instruction::parse).toList()
    private val wires = MiniLanguage()

    private fun solve(inst: Iterable<Instruction> = instructions): Int {
        while (wires.variablesView()["a"] == null)
            wires.runInstructions(inst)
        return wires.variablesView()["a"]!!.toInt()
    }

    override fun part1i(): Int {
        wires.clear()
        return solve()
    }

    override fun part2i(): Int {
        val old = solve()
        wires.clear()
        val newInstructions = instructions.toMutableList()
        newInstructions.removeIf { it.output == "b" }
        newInstructions.add(0, Instruction(op = OP.MOV, input1 = old.toString(), output = "b"))
        return solve(newInstructions)
    }
}

fun main() {
    val d = Day07()
    println(d.part1())
    println(d.part2())
}