package dev.mzarnowski.isarch

import java.io.InputStream

class Input(private val source: InputStream) {
    private var current = source.read()

    fun hasNext() = current >= 0
    fun current(): Char = current.toChar()
    fun advance(): Char = current.toChar().also { current = source.read() }
}