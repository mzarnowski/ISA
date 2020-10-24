package dev.mzarnowski.isarch.x64

import dev.mzarnowski.isarch.get
import dev.mzarnowski.isarch.toHex

data class Prefixes(private var value: Int = 0) {
    fun add(prefix: Int): Boolean {
        val group = prefix.group()
        if (group !in 0..3) return false

        setPrefix(group, prefix)
        return true
    }

    operator fun get(group: Int): Int =
        if (group in 0..3) value[mask(group)]
        else throw IllegalArgumentException("Invalid group $group")

    private fun setPrefix(group: Int, prefix: Int) {
        value = (value - value and mask(group)) + prefix shl (group * 8)
    }

    private fun mask(group: Int) = 0xFF shl (group * 8)

    override fun toString(): String = value.toHex()
}

private fun Int.group(): Int = when (this) {
    0xF0 or 0xF2 or 0xF3 -> 0
    0x26 or 0x2E or 0x36 or 0x3E or 0x64 or 0x65 -> 1
    0x66 -> 2
    0x67 -> 3
    else -> Int.MIN_VALUE
}

object Prefix {
    // first group
    const val LOCK = 0xF0
    const val REPNE = 0xF2
    const val REPNZ = 0xF2

    // second group
    const val OverrideES = 0x26
    const val OverrideCS = 0x2E
    const val OverrideSS = 0x36
    const val OverrideDS = 0x3E
    const val OverrideFS = 0x64
    const val OverrideGS = 0x65
    const val BranchNotTaken = 0x2E
    const val BranchTaken = 0x3E

    // third group
    const val OverrideOperandSize = 0x66

    // fourth group
    const val OverrideAddressSize = 0x67
}