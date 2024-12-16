package ox.puzzles.y2024.day15

import ox.lib.util.CharacterGrid
import ox.lib.util.GridIndex
import ox.puzzles.Day

typealias DirFunc = (GridIndex) -> GridIndex
typealias Dir = Char

fun movementFunc(c: Char): DirFunc = when (c) {
    '^' -> GridIndex::up
    '>' -> GridIndex::right
    'v' -> GridIndex::down
    '<' -> GridIndex::left
    else -> throw IllegalArgumentException()
}

fun entityUp(dist: IntRange, pos: GridIndex) = dist.map { pos.right(it) }.map { it.up() }
fun entityDown(dist: IntRange, pos: GridIndex) = dist.map { pos.right(it) }.map { it.down() }
fun entityLeft(pos: GridIndex) = listOf(pos.left(2))
fun entityRight(dist: Int, pos: GridIndex) = listOf(pos.right(dist))

fun defineFindEntity(dist: Int = 2) = { e: Entity, dir: Dir ->
    when (dir) {
        '^' -> entityUp(-1..<dist, e.position)
        'v' -> entityDown(-1..<dist, e.position)
        '<' -> entityLeft(e.position)
        '>' -> entityRight(dist, e.position)
        else -> throw IllegalArgumentException()
    }.asSequence()
        .map { newPos -> e.grid.find { newPos == it.position } }
        .filterNotNull()
        .toList()
}

interface Entity {
    var grid: List<Entity>
    var position: GridIndex
    fun findEntity(dir: Dir): List<Entity>
    fun setList(gg: List<Entity>) {
        grid = gg
    }
    fun canPush(dir: Dir): Boolean
    fun push(dir: Dir)
}

open class Box(override var position: GridIndex) : Entity {
    override lateinit var grid: List<Entity>
    override fun findEntity(dir: Dir) = defineFindEntity(2)(this, dir)

    override fun canPush(dir: Dir): Boolean {
        val next = findEntity(dir)
        return next.all { it.canPush(dir) }
    }
    override fun push(dir: Dir) {
        val next = findEntity(dir)
        next.forEach { it.push(dir) }
        position = movementFunc(dir)(position)
    }
}

class Wall(override var position: GridIndex) : Entity {
    override fun findEntity(dir: Dir) = emptyList<Entity>()
    override lateinit var grid: List<Entity>
    override fun canPush(dir: Dir) = false
    override fun push(dir: Dir) {
        throw IllegalArgumentException()
    }
}

class Robot(position: GridIndex) : Box(position) {
    override fun findEntity(dir: Dir) = defineFindEntity(1)(this, dir)
}

fun printWideGrid(move: Dir, ll: List<Entity>, color: Int = 0) {
    val toCursor = { (i, j): GridIndex -> "${27.toChar()}[${j+2};${i+1}H" }
    val clearScreen = "${27.toChar()}[2J"
    val textColor = { it: Int -> "${27.toChar()}[${it}m" }
    print(clearScreen)
    print(textColor(color))
    print(toCursor(GridIndex(0, -1)))
    print(move)
    for (l in ll) {
        print(toCursor(l.position))
        when (l) {
            is Wall -> print("##")
            is Robot -> print("@")
            is Box -> print("[]")
        }
    }
    System.out.flush()
}

class Day15 : Day {
    private val grid: CharacterGrid
    private val movements: List<Dir>

    init {
        val lines = getBufferedReader(getInputName()).readLines()
        val emptyLine = lines.indexOf("")
        grid = CharacterGrid(lines.subList(0, emptyLine))
        movements = lines.subList(emptyLine + 1, lines.size).flatMap { it.asSequence() }
    }

    override fun part1i(): Int {
        var robot = grid.indexOf('@')!!
        for (move in movements.map(::movementFunc)) {
            val candidate = move(robot)
            var head = candidate
            while (grid[head] == 'O') {
                head = move(head)
            }
            if (grid[head] == '.') {
                grid[head] = 'O'
                grid[candidate] = '@'
                grid[robot] = '.'
                robot = candidate
            }
        }
        return grid.withIndex()
            .filter { (i, c) -> c == 'O' }
            .sumOf { (i, c) -> i.x + i.y * 100 }
    }

    override fun part2i(): Int {
        val list = grid.withIndex()
            .filterNot { (i, c) -> c == '.' }
            .map { (i, c) ->
                when (c) {
                    '#' -> Wall(GridIndex(i.x * 2, i.y))
                    'O' -> Box(GridIndex(i.x * 2, i.y))
                    '@' -> Robot(GridIndex(i.x * 2, i.y))
                    else -> throw IllegalArgumentException()
                }
            }
        list.forEach { it.setList(list) }
        val robot = list.find { it is Robot }!!
        for (move in movements) {
            if (robot.canPush(move)) {
                robot.push(move)
            }
        }

        return list.filter { it is Box && it !is Robot }
            .map(Entity::position)
            .sumOf {
                it.x + it.y * 100
            }
    }
}

fun main() {
    val d = Day15()
    println(d.part1())
    println(d.part2())
}