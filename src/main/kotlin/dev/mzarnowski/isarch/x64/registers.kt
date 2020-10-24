package dev.mzarnowski.isarch.x64

private val registers8 = arrayOf(
    "al", "cl", "dl", "bl",
    "ah", "ch", "dh", "bh"
)

private val registers32 = arrayOf(
    "eax", "ecx", "edx", "ebx",
    "esp", "ebp", "esi", "edi",
    "r8d", "r9d", "r10d", "r11d",
    "r12d", "r13d", "r14d", "r15d"
)
private val registers64 = arrayOf(
    "rax", "rcx", "rdx", "rbx",
    "rsp", "rbp", "rsi", "rdi",
    "r8", "r9", "r10", "r11",
    "r12", "r13", "r14", "r15"
)

fun register(datasize: Int, index: Int): String = when (datasize) {
    8 -> registers8[index]
    32 -> registers32[index]
    64 -> registers64[index]
    else -> throw IllegalArgumentException(datasize.toString())
}