package dev.mzarnowski.isarch.x64

import dev.mzarnowski.isarch.get
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

    private fun nextByte(): Int = bytes.get().toInt() and 0xFF
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

        var op = byte(at = offset++)
        if (op == 0x0F) {
            op = (op shl 8) + byte(at = offset++)
        }

        bytes.position(offset)
        return when (op) {
            in (0x50..0x57) -> parse120(op - 0x50)
            in (0x58..0x5f) -> parse130(op - 0x58)
            in (0x90..0x97) -> parse220(op - 0x90)
            0xe8 -> parse350()
            0x0f_1f -> parse17_037()
            else -> TODO("Unsupported op: ${op.toHex()}")
        }
    }

    private fun parse17_037(): String {
        val byte = nextByte()
        val operand = byte.addressOrRegister()
        val ignored = byte.opcodeExtension()
        return "NOP $operand"
    }

    private fun parse120(regBase: Int): String {
        val reg = regBase + (extension[0] shl 3)
        return "PUSH r$reg"
    }

    private fun parse130(regBase: Int): String {
        val reg = regBase + (extension[0] shl 3)
        return "POP r$reg"
    }

    private fun parse220(regBase: Int): String {
        val reg = regBase + (extension[0] shl 3)
        return "XCHG r$reg, r0"
    }

    private fun parse350(): String {
        val target = (bytes.int + bytes.position()).toLong()
        return "CALL ${target.toHex()}"
    }

    private fun Int.opcodeExtension(): Int = this[3..6]

    private fun Int.addressOrRegister(): Operand {
        val mod = this[6..8]
        val name = this[0..2] + (extension[0] shl 3)

        if (mod == 3) return Register(name)
        if (name == 5 && mod == 1) return Address.IpRelative(bytes.int.toLong())

        val sib = if (name == 4) nextByte() else -1
        val displacement = when (mod) {
            0 -> 0
            1 -> nextByte().toLong()
            else -> bytes.int.toLong()
        }

        if (sib < 0) return Address.Indirect(name, displacement, scale = 0, index = 0)

        val scale = 1 shl sib[6..8]
        val index = sib[3..6] + (extension[1] shl 3)
        val base = sib[0..3] + (extension[2] shl 3)

        val noIndex = index == 4

        return when {
            (base and 5) != 5 -> Address.Indirect(base, displacement, scale, index)
            mod != 0 -> Address.Indirect(base, displacement, scale, index)
            else -> {
                val displacement = bytes.int.toLong() // only case when it has not yet been read
                if (noIndex) Address.Direct(displacement)
                else Address.Direct(displacement, scale, index)
            }
        }
    }
}

sealed class Operand {
    final override fun toString(): String = when (this) {
        is Register -> "r$name"
        is Address.Direct -> if (scale == 0) "[$offset]" else "[$offset + r$index * $scale]"
        is Address.Indirect -> when {
            (offset == 0L) and (scale == 0) -> "[r$base]"
            (offset == 0L) -> "[r$base + r$index * $scale]"
            (scale == 0) -> "[r$base + $offset]"
            else -> "[r$base + $offset + r$index * $scale]"
        }
        is Address.IpRelative -> "[ip + $offset]"
    }
}

sealed class Address : Operand() {
    data class Direct(val offset: Long, val scale: Int = 0, val index: Int = 0) : Address()
    data class Indirect(val base: Int, val offset: Long, val scale: Int, val index: Int) : Address()
    data class IpRelative(val offset: Long) : Address()
}

data class Register(val name: Int) : Operand()