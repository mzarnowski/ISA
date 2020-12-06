package dev.mzarnowski.isarch.x64

import dev.mzarnowski.isarch.toHex
import dev.mzarnowski.os.elf.Executable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Paths

fun main() {
    val code = Executable.from(Paths.get("/Users/mzarnowski/tmp/as")).section(".text").order(ByteOrder.LITTLE_ENDIAN)
    val iterator = InstructionIterator(code)
    while (iterator.hasNext()) {
        val since = code.position()
        try {
            val instruction = iterator.next()
            val binary = (since until code.position()).joinToString(" ") { "%02x".format(code[it]) }
            println("$binary: $instruction")
        } catch (e: Throwable) {
            val binary = (since until since + 17).joinToString(" ") { "%02x".format(code[it]) }
            println("Failed to parse: $binary")
            throw e
        }
    }
}

class InstructionIterator(private val bytes: ByteBuffer) {
    fun hasNext() = bytes.position() < bytes.limit()
    private var prefix = IntArray(4)
    private var extension = 0

    private fun byte(at: Int): Int = bytes.get(at).toInt() and 0xFF

    private fun readPrefix(value: Int): Boolean {
        val group = value.prefixGroup()
        if (group !in 0..3) return false
        prefix[group] = value
        return true
    }

    private fun readExtension(value: Int): Boolean {
        return (value in 0x40..0x4f).also {
            extension = if (it) value else 0
        }
    }

    fun next(): String {
        if (!hasNext()) throw NoSuchElementException()
        var offset = bytes.position()

        prefix = IntArray(4)
        while (readPrefix(byte(at = offset))) offset++
        if (readExtension(byte(at = offset))) offset++

        val op = byte(at = offset++)

        bytes.position(offset)
        return when (op) {
            0xe8 -> parse350()
            else -> TODO()
        }
    }

    private fun parse350(): String {
        val target = (bytes.int + bytes.position()).toLong()
        return "CALL ${target.toHex()}"
    }
}