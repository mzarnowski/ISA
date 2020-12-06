package dev.mzarnowski.isarch

operator fun Int.get(bit: Int): Int = (this and (1 shl bit)) shr bit
operator fun Int.get(mask: IntRange): Int = (this and mask.toBitMask()) shr mask.first

fun IntRange.toBitMask(): Int {
    var mask = 0
    this.forEach { bit -> mask += (0b1 shl bit) }
    return mask
}

internal fun Int.toBinary() = Integer.toBinaryString(this)
internal fun Int.toHex() = Integer.toHexString(this)
internal fun Long.toHex() = java.lang.Long.toHexString(this)