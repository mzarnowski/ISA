package dev.mzarnowski.isarch.x64

import dev.mzarnowski.isarch.ByteStream
import dev.mzarnowski.isarch.TestCase
import dev.mzarnowski.isarch.tokenize
import dev.mzarnowski.isarch.x64.parse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

class ParserTest {
    @ParameterizedTest
    @MethodSource("x64_test_cases")
    fun parse_x64_binary(testCase: TestCase) {
        val parsed = parse(ByteStream(testCase.binary))
        val tokens = tokenize(parsed)
        assertThat(tokens).isEqualTo(testCase.expected)
    }

    companion object {
        @JvmStatic
        fun x64_test_cases(): List<Arguments> {
            val file = ParserTest::class.java.getResource("x64").file
            val stream = Files.list(Paths.get(file))
            val files = stream.collect(Collectors.toList()).also {
                stream.close()
            }
            return files
                .map(Files::newInputStream)
                .flatMap { it.use(TestCase::from) }
                .map { Arguments.of(it) }
        }
    }
}