package com.kDg.xamlStyleDiagram

import java.io.*
import java.nio.charset.StandardCharsets
import java.util.zip.DeflaterOutputStream

class PlantUmlTextEncoder {
    fun encode(reader: Reader): String {
        val output = ByteArrayOutputStream()
        DeflaterOutputStream(output).use { deflaterStream ->
            OutputStreamWriter(deflaterStream, StandardCharsets.UTF_8).use { writer ->
                reader.copyTo(writer)
            }
        }
        return encode(output.toByteArray())
    }

    fun encode(plantUmlText: String): String {
        return encode(StringReader(plantUmlText))
    }

    private fun encode(bytes: ByteArray): String {
        val s = StringBuilder()
        var i = 0
        while (i < bytes.size) {
            val b1 = bytes[i].toInt() and 0xFF
            val b2 = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else 0
            val b3 = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else 0
            s.append(append3Bytes(b1, b2, b3))
            i += 3
        }
        return s.toString()
    }

    private fun append3Bytes(b1: Int, b2: Int, b3: Int): CharArray {
        val c1 = b1 shr 2
        val c2 = ((b1 and 0x3) shl 4) or (b2 shr 4)
        val c3 = ((b2 and 0xF) shl 2) or (b3 shr 6)
        val c4 = b3 and 0x3F
        return charArrayOf(
            encodeByte(c1 and 0x3F),
            encodeByte(c2 and 0x3F),
            encodeByte(c3 and 0x3F),
            encodeByte(c4 and 0x3F)
        )
    }

    private fun encodeByte(b: Int): Char {
        return when {
            b < 10 -> ('0'.code + b).toChar()
            b < 36 -> ('A'.code + b - 10).toChar()
            b < 62 -> ('a'.code + b - 36).toChar()
            b == 62 -> '-'
            b == 63 -> '_'
            else -> '?'
        }
    }
}
