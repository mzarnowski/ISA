package dev.mzarnowski.isarch.x64

internal fun Int.prefixGroup(): Int = when (this) {
    0xF0 or 0xF2 or 0xF3 -> 0
    0x26 or 0x2E or 0x36 or 0x3E or 0x64 or 0x65 -> 1
    0x66 -> 2
    0x67 -> 3
    else -> Int.MIN_VALUE
}