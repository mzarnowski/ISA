package dev.mzarnowski.isarch.x64

interface Operand

sealed class Address : Operand {
    data class Direct(val offset: Int) : Address()
    data class Indirect(val base: Int, val offset: Int) : Address()
    data class Indexed(val base: Int, val index: Int, val scale: Int, val offset: Int) : Address()
    data class Foo(val offset: Int) : Address() // instruction pointer
}

data class Immediate(val value: Int) : Operand
data class Register(val id: Int) : Operand

data class Instruction(val op: String, val dataSize: Int, val addressSize: Int, val operands: List<Operand>) {
    companion object {
        operator fun invoke(op: String, opSize: Int, addrSize: Int, vararg ops: Operand): Instruction {
            return Instruction(op, opSize, addrSize, ops.toList())
        }
    }
}