package dev.mzarnowski.isarch.arm

import dev.mzarnowski.isarch.get
import dev.mzarnowski.isarch.toBinary

fun parse(instruction: Int): String {
    return when (instruction[25..28]) {
        0b0000 -> reserved(instruction)
        0b0001 -> unallocated(instruction)
        0b0010 -> TODO("Scalable Vector Extension")
        0b0011 -> unallocated(instruction)
        0b0100 -> TODO("Loads and stores")
        0b0101 -> TODO("Data processing - register")
        0b0110 -> TODO("Loads and stores")
        0b0111 -> TODO("Data processing - Scalar FP & Advanced SIMD")
        0b1000 -> dataProcessingImmediate(instruction)
        0b1001 -> dataProcessingImmediate(instruction)
        0b1010 -> TODO("Branches, Exceptions, System")
        0b1011 -> TODO("Branches, Exceptions, System")
        0b1100 -> TODO("Loads and stores")
        0b1101 -> TODO("Data processing - register")
        0b1110 -> TODO("Loads and stores")
        0b1111 -> TODO("Data processing - Scalar FP & Advanced SIMD")
        else -> TODO("Mathematics has failed")
    }
}

private fun reserved(instruction: Int): String {
    val op0 = instruction[29..31]
    val op1 = instruction[16..24]

    return when {
        op0 == 0 && op1 == 0 -> "UDF"
        else -> unallocated(instruction)
    }
}

internal fun unallocated(instruction: Int): String {
    return "Unallocated ${instruction.toBinary()}"
}
