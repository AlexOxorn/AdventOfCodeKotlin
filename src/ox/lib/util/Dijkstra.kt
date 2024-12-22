package ox.lib.util

import java.util.*

open class DijkstraSolver<Node, Cost>(
    aStart: List<Node>,
    aEnd: (Node) -> Boolean,
    aGetNeighbours: (Node) -> Iterable<Pair<Node, Cost>>,
    aNullCost: Cost,
    aCostCmp: (Cost, Cost) -> Int,
    aCostAdd: (Cost, Cost) -> Cost,
    aGetHeuristic: ((Node) -> Cost)? = null,
) : Sequence<List<Pair<Node, Cost>>> {
    companion object {
        fun <Node> universalSentinel(n: Node) = true
    }

    protected val nullCost: Cost = aNullCost
    protected val start: List<Node> = aStart
    protected var sentinel: (Node) -> Boolean = aEnd
    protected val getNeighbours: (Node) -> Iterable<Pair<Node, Cost>> = aGetNeighbours
    protected var heuristic: ((Node) -> Cost)? = null
    protected val costCmp: (Cost, Cost) -> Int = aCostCmp
    protected val costAdd: (Cost, Cost) -> Cost = aCostAdd

    protected val openSet: PriorityQueue<Pair<Node, Cost>> = PriorityQueue { i, j -> costCmp(i.second, j.second) }
    protected val cameFrom: MutableMap<Node, Node> = mutableMapOf()
    protected val gScore: MutableMap<Node, Cost>
    private var debugFunc: ((Node, Cost, PriorityQueue<Pair<Node, Cost>>, Map<Node, Cost>, Map<Node, Node>) -> Unit)? =
        null

    protected var trackCameFrom = false

    protected fun getHeuristic(i: Node) =
        heuristic?.let { it(i) } ?: nullCost

    protected open fun debug(
        n: Node,
        c: Cost,
        os: PriorityQueue<Pair<Node, Cost>>,
        gs: MutableMap<Node, Cost>,
        cf: MutableMap<Node, Node>
    ) =
        debugFunc?.let { it(n, c, os, gs, cf) }

    protected fun reset() {
        gScore.clear()
        start.forEach { gScore[it] = nullCost }
        cameFrom.clear()
        openSet.clear()
        start.forEach { openSet.add(it to getHeuristic(it)) }

    }

    private fun processNeighbours(current: Node) {
        for ((neighbour, cost) in getNeighbours(current)) {
            val tentativeScore = costAdd(gScore.getValue(current), cost)
            val alreadyChecked = gScore.contains(neighbour)
            val cmpResult = if (alreadyChecked) costCmp(tentativeScore, gScore.getValue(neighbour)) else -1
            if (cmpResult >= 0) {
                continue
            }
            if (trackCameFrom) {
                cameFrom[neighbour] = current
            }
            gScore[neighbour] = tentativeScore
            openSet.add(neighbour to costAdd(tentativeScore, getHeuristic(neighbour)))
        }
    }

    init {
        heuristic = aGetHeuristic ?: { i -> nullCost }
        gScore = mutableMapOf()
    }

    fun setDebugFunction(f: (Node, Cost, PriorityQueue<Pair<Node, Cost>>, Map<Node, Cost>, Map<Node, Node>) -> Unit):
            DijkstraSolver<Node, Cost> {
        debugFunc = f
        return this
    }

    fun trackPath(): DijkstraSolver<Node, Cost> {
        trackCameFrom = true
        return this
    }

    private fun generateResult(current: Node): List<Pair<Node, Cost>> {
        val toReturn = mutableListOf(current to gScore.getValue(current))
        val pathSequence: Sequence<Node?> =
            generateSequence(cameFrom[toReturn.last().first]) { cameFrom[it] }
        for (i in pathSequence) {
            if (i == null)
                break
            toReturn.add(i to gScore.getValue(i))
        }
        toReturn.reverse()
        return toReturn
    }

    private fun getSequence() = sequence {
        reset()
        while (openSet.isNotEmpty()) {
            val (current, currentCost) = openSet.remove()
            if (costCmp(currentCost, gScore[current]!!) < -1)
                continue
            debug(current, currentCost, openSet, gScore, cameFrom)
            processNeighbours(current)
            if (sentinel(current)) {
                yield(generateResult(current))
            }
        }
    }

    operator fun invoke(): List<Pair<Node, Cost>> {
        return getSequence().take(1).firstOrNull() ?: emptyList()
    }

    override fun iterator(): Iterator<List<Pair<Node, Cost>>> {
        return getSequence().iterator()
    }
}

class SuperDijkstraSolver<Node, Cost>(
    aStart: List<Node>,
    aEnd: (Node) -> Boolean,
    aGetNeighbours: (Node) -> Iterable<Pair<Node, Cost>>,
    aNullCost: Cost,
    aCostCmp: (Cost, Cost) -> Int,
    aCostAdd: (Cost, Cost) -> Cost,
    aGetHeuristic: ((Node) -> Cost)? = null,
) : DijkstraSolver<Node, Cost>(aStart, aEnd, aGetNeighbours, aNullCost, aCostCmp, aCostAdd, aGetHeuristic) {
    private val cameFrom2: MutableMap<Node, MutableList<Node>> = mutableMapOf()

    private fun processNeighbours2(current: Node) {
        for ((neighbour, cost) in getNeighbours(current)) {
            val tentativeScore = costAdd(gScore.getValue(current), cost)
            val alreadyChecked = gScore.contains(neighbour)
            val cmpResult = if (alreadyChecked) costCmp(tentativeScore, gScore.getValue(neighbour)) else -1
            if (cmpResult > 0) {
                continue
            } else if (cmpResult == 0) {
                cameFrom2[neighbour]?.add(current)
            } else {
                cameFrom2[neighbour] = mutableListOf(current)
            }

            gScore[neighbour] = tentativeScore
            openSet.add(neighbour to costAdd(tentativeScore, getHeuristic(neighbour)))
        }
    }

    private fun allBestResults(from: Node, seen: MutableSet<Node> = mutableSetOf()): Set<Node> {
        if (seen.contains(from))
            return seen
        seen.add(from)
        cameFrom2[from]?.forEach { allBestResults(it, seen) }
        return seen
    }

    fun getBestPathsResult(from: Node): Set<List<Node>> {
        return cameFrom2[from]
            ?.flatMap {
                getBestPathsResult(it)
                    .map {
                        sub -> sub + listOf(from)
                    }
            }
            ?.toSet() ?: setOf(emptyList())
    }

    fun <R> solve2(res: (Node) -> R): R {
        reset()
        while (openSet.isNotEmpty()) {
            val (current, currentCost) = openSet.remove()
            debug(current, currentCost, openSet, gScore, cameFrom)
            processNeighbours2(current)
            if (sentinel(current)) {
                return res(current)
            }
        }
        throw Exception()
    }

    fun getBestPositions() = solve2(::allBestResults)
    fun getBestPaths() = solve2(::getBestPathsResult)
}