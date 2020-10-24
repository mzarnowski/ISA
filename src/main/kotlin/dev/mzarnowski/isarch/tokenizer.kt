package dev.mzarnowski.isarch

fun tokenize(instruction: String): List<String> {
    val text = instruction.trim().replace("\\W+", " ")
    val (mnemonic, operands) = text.indexOf(" ").let {
        if (it < 0) text to emptyList()
        else text.substring(0, it) to text.substring(it).split(",").map(String::trim)
    }
    return mutableListOf<String>().apply {
        add(mnemonic)
        addAll(operands)
    }
}

fun main() {
    println(tokenize(" ADD BYTE PTR [rcx], al   "))
}
