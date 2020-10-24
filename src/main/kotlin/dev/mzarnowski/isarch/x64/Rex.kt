package dev.mzarnowski.isarch.x64

import dev.mzarnowski.isarch.get

// https://bob.cs.sonoma.edu/IntroCompOrg-x64/bookch9.html

// [0|1|0|0|W|R|X|B]
class Rex(byte: Int) {
    /**
     * If set, indicates 64-bit operand size
     */
    val W: Int = byte[3]
    val operandSize = if(W == 1) 64 else 32

    /**
     * Used as an extra bit in the Reg field of the ModR/M byte.
     */
    val R: Int = byte[2]

    /**
     * Used as an extra bit in the Index field of the SIB byte.
     */
    val X: Int = byte[1]

    /**
     * Used as an extra bit in the Base field of the SIB byte
     * and sometimes as an extra bit in the R/M field of the ModR/M byte.
     */
    val B: Int = byte[0]
}


fun modrm(mode: Int, regop: Int, rm: Int) = (mode shl 6) + (regop shl 3) + rm

/**
 * Format: [m|m|r|r|r|b|b|b]
 */
class ModRM(byte: Int) {
    /**
     * 00	memory operand; address in register specified by bbb
     * 01	memory operand; address in register specified by bbb plus 8-bit offset
     * 10	memory operand; address in register specified by bbb plus 16-bit offset
     * 11	register operand; register specified by bbb
     */
    val mod: Int = byte[6..7]

    /**
     * Reg/Opcode field encodes either a register or is an extension to the main opcode.
     */
    private val reg: Int = byte[3..5]
    fun opcodeExtension() = reg
    fun firstOperand() = reg

    /**
     * R/M field encodes either a register or, when combined with mod, an addressing mode
     */
    val rm: Int = byte[0..2]
//    fun onlyOperand() = RM
    fun secondOperand() = rm

}

/**
 *  * Format: [s|s|i|i|i|b|b|b]
 */
class SIB(byte: Int) {
    val scale = byte[6..7]

    /**
     * Number of the index register. Extended by the [Rex.X]
     */
    val index = byte[3..5]

    /**
     * Number of the base register. Extended by the [Rex.B]
     */
    val base = byte[0..2]
}