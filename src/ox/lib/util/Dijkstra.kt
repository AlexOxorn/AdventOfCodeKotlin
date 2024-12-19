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
            if (cmpResult > 0) {
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

    fun setDebugFunction(f: (Node, Cost, PriorityQueue<Pair<Node, Cost>>, Map<Node, Cost>, Map<Node, Node>) -> Unit) {
        debugFunc = f
    }

    fun trackPath() {
        trackCameFrom = true
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
            debug(current, currentCost, openSet, gScore, cameFrom)
            processNeighbours(current)
            if (sentinel(current)) {
                yield(generateResult(current))
            }
        }
    }

    operator fun invoke(): List<Pair<Node, Cost>> {
        return getSequence().take(1).first()
    }

    override fun iterator(): Iterator<List<Pair<Node, Cost>>> {
        return getSequence().iterator()
    }
}