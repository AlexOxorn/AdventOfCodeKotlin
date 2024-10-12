package ox.lib.collectionExpansion

import java.util.BitSet

fun BitSet.toULong(): ULong {
    return (0..<size()).filter { this[it] }.map { 1uL shl it }.sum()
}
fun BitSet.toUInt(): UInt {
    return (0..<size()).filter { this[it] }.map { 1u shl it }.sum()
}