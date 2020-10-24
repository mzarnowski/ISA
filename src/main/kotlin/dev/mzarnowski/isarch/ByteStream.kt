package dev.mzarnowski.isarch

class ByteStream(private val bytes: IntArray) {
    private var current = 0
    fun current(): Int = bytes[current]
    fun advance():Int = bytes[current++]
}