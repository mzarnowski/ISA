package dev.mzarnowski.isarch.x64

import dev.mzarnowski.isarch.ByteStream
import dev.mzarnowski.isarch.toHex

fun parse(stream: ByteStream): String {
    val prefixes = Prefixes()
    while (prefixes.add(stream.current())) stream.advance()

    val rex = stream.readRex()

    when (val op = stream.advance()) {
        0x00 -> {
            val mod = stream.readModRM()
            val dataSize = if (prefixes[3] == 0x67) 32 else 64
            val source = register(8, mod.firstOperand())
            val target = register(dataSize, mod.secondOperand())

            if (mod.rm == 0b100 && mod.mod != 0b11) {
                // SIB is not supported
                return "UNALLOCATED"
            }

            when (mod.mod) {
                0b00 -> {

                }
                0b01 -> print("")
                0b10 -> print("")
                0b11 -> print("")
            }

            return "ADD BYTE PTR [$target], $source"
        }
        else -> TODO("Unsupported opcode: ${op.toHex()}")
    }

    return prefixes.toString()
}

private fun ByteStream.readRex(): Rex? {
    return if (current() !in 0x40..0x48) null
    else Rex(current()).also { advance() }
}

private fun ByteStream.readModRM(): ModRM = ModRM(advance())
private fun ByteStream.readSIB(): SIB = SIB(advance())