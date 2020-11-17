package dev.mzarnowski.isarch.arm.thumb

import java.io.InputStream


class Instruction()

class ThumbDecoder() {
    fun parse(input: InputStream) {
        val first = input.read()
        val half = (first shl 8) + input.read()

        when {
            first < 0b00011000 -> TODO("Shift by immediate, move register")
            first < 0b00011100 -> TODO("Add/Subtract register")
            first < 0b00100000 -> TODO("Add/Subtract immediate")
            first < 0b01000000 -> TODO("Add/Subtract/Compare/Move immediate")
            first < 0b01000100 -> TODO("Data-processing register")
            first < 0b01000111 -> TODO("Special data processing")
            first < 0b01001000 -> TODO("Branch/Exchange")
            first < 0b01010000 -> TODO("Load from literal pool")
            first < 0b01100000 -> TODO("Load/Store register offset")
            first < 0b10000000 -> TODO("Load/Store word/byte immediate offset")
            first < 0b10010000 -> TODO("Load/Store half-word immediate offset")
            first < 0b10100000 -> TODO("Load/Store with stack")
            first < 0b11000000 -> TODO("Miscellaneous")
            first < 0b11010000 -> TODO("Load/Store multiple")
            first < 0b11011110 -> TODO("Conditional branch")
            first < 0b11011111 -> TODO("Undefined")
            first < 0b11100000 -> TODO("Service/System call")
            first < 0b11101000 -> TODO("Unconditional branch")
            else -> parse32(half, input)
        }
    }

    private fun parse16(first: Int, input: InputStream) {

    }

    private fun parse32(half: Int, input: InputStream) {
        val encoded = (half shl 16) + (input.read() shl 8) + input.read()
    }
}

fun startsWith(a: Int, that: Int): Int {
    that.countLeadingZeroBits()
}