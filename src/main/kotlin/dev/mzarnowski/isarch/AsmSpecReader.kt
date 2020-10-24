package dev.mzarnowski.isarch

import java.io.InputStream
import java.lang.Character.digit

class TestCase(val binary: IntArray, val expected: List<String>) {
    companion object {
        operator fun invoke(stream: InputStream) = TestCaseParser(Input(stream)).parse()
        fun from(stream: InputStream) = invoke(stream)
    }
}

private class TestCaseParser(val input: Input) {
    fun parse(): List<TestCase> {
        val cases = mutableListOf<TestCase>()

        while (input.hasNext()) {
            if (input.advance() == '{') cases += parseTestCase()
        }

        return cases
    }

    private fun parseTestCase(): TestCase {
        var bytes = IntArray(2)
        var count = 0

        while (input.hasNext()) {
            val current = input.advance()
            when {
                current.isWhitespace() -> continue
                current.isDigit() || current in ('a'..'f') || current in ('A'..'F') -> {
                    if (count == bytes.size) bytes = bytes.copyOf(bytes.size * 2)
                    bytes[count++] = (digit(current, 16) shl 8) + digit(input.advance(), 16)
                }
                current == '|' -> break
            }
        }

        val expected = StringBuilder()
        while (input.hasNext()) {
            val current = input.advance()
            if (current == '}') break
            else expected.append(current)
        }

        return TestCase(bytes.copyOf(count), tokenize(expected.toString()))
    }
}