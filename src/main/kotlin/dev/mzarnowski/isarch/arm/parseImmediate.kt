package dev.mzarnowski.isarch.arm

import dev.mzarnowski.isarch.get

internal fun dataProcessingImmediate(instruction: Int): String {
    return when (instruction[23..25]) {
        0b000 or 0b001 -> pcRelativeAddressing(instruction)
        0b010 -> addOrSubtract(instruction)
        0b011 -> addOrSubtractWithTags(instruction)
        0b100 -> TODO("Logical immediate")
        0b101 -> TODO("Move wide immediate")
        0b110 -> TODO("Bitfield")
        0b111 -> TODO("Extract")
        else -> TODO("Mathematics has failed")
    }
}

private fun pcRelativeAddressing(instruction: Int): String {
    val rd = instruction[0..4]
    val imm_hi = instruction[5..23]
    val imm_lo = instruction[29..30]
    val op = instruction[31]

    return if (op == 0) {
        val imm = (imm_hi shl 2) + imm_lo
        "ADR $rd, $imm"
    } else {
        val imm = (imm_hi shl 14) + (imm_lo shl 12)
        "ADRP $rd, $imm"
    }
}

private fun addOrSubtract(instruction: Int): String {
    val rd = instruction[0..4]
    val rn = instruction[5..9]
    val imm12 = instruction[10..21]
    val shift = instruction[22] == 1
    val updateStatusFlag = instruction[28] == 1
    val op = instruction[29]
    val dataSize = if (instruction[30] == 0) 32 else 64
    val value = if (shift) imm12 shl 12 else imm12

    val mnemonic = if (op == 1) "SUB" else "ADD" + if (updateStatusFlag) "S" else ""
    return "$mnemonic $rd, $rn, $value"
}

// TODO 268 and 1337
private fun addOrSubtractWithTags(instruction: Int): String {
    val rd = instruction[0..4]
    val rn = instruction[5..9]
    val uimm4 = instruction[10..13]
    val op3 = instruction[14..15]
    val uimm6 = instruction[16..21]
    val o2 = instruction[22]
    val S = instruction[29]
    val op = instruction[30]
    val sf = instruction[31]

    return when {
        o2 == 1 -> unallocated(instruction)
        sf == 0 -> unallocated(instruction)
        S == 1 -> unallocated(instruction)
        else -> {
            // TODO if not have MTE_ext undefined
            if (op == 1) "SUBG" else "ADDG"
        }
    }
}

private fun logical(instruction: Int): String {
    val rd = instruction[0..4]
    val rn = instruction[5..9]
    val imms = instruction[10..15]
    val immr = instruction[16..21]
    val N = instruction[22]
    val op = instruction[29..30]
    val dataSize = if (instruction[31] == 0) 32 else 64

    if (dataSize == 32 && N == 1) return unallocated(instruction)
    return when (op) {
        0b00 -> "AND"
        0b01 -> "OR"
        0b10 -> "XOR"
        0b11 -> "ANDS"
        else -> unallocated(instruction)
    }
}

private fun moveWide(instruction: Int): String {
    val rd = instruction[0..4]
    val imm16 = instruction[5..20]
    val hw = instruction[21..22]
    val opc = instruction[29..30]
    val dataSize = if (instruction[31] == 0) 32 else 64

    if (opc == 0b01) return unallocated(instruction)
    if (dataSize == 32 && (hw == 0b10 || hw == 0b11)) return unallocated(instruction)

    return when (opc) {
        0b00 -> "MOVN"
        0b01 -> unallocated(instruction)
        0b10 -> "MOVZ"
        0b11 -> "MOVK"
        else -> unallocated(instruction)

    }
}

private fun bitfield(instruction: Int): String {
    val rd = instruction[0..4]
    val rn = instruction[5..9]
    val imms = instruction[10..15]
    val immr = instruction[16..21]
    val N = instruction[22]
    val opc = instruction[29..30]
    val dataSize = if (instruction[31] == 0) 32 else 64

    if (dataSize == 32 && N == 1) return unallocated(instruction)
    if (dataSize == 64 && N == 0) return unallocated(instruction)
    return when (opc) {
        0b00 -> TODO("SBFM")
        0b01 -> TODO("BFM")
        0b10 -> TODO("UBFM")
        else -> unallocated(instruction)
    }
}

private fun extract(instruction: Int): String {
    val rd = instruction[0..4]
    val rn = instruction[5..9]
    val imms = instruction[10..15]
    val rm = instruction[16..20]
    val o0 = instruction[21]
    val N = instruction[22]
    val op21 = instruction[29..30]
    val dataSize = if (instruction[31] == 0) 32 else 64

    TODO("Extract") // at page 270
}