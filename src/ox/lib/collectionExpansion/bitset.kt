package ox.lib.collectionExpansion

import java.util.BitSet

fun BitSet.toULong(): ULong {
    return (0..<size()).filter { this[it] }.sumOf { 1uL shl it }
}
fun BitSet.toUInt(): UInt {
    return (0..<size()).filter { this[it] }.sumOf { 1u shl it }
}