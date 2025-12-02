package com.kDg.xamlStyleDiagram

import com.intellij.openapi.diagnostic.Logger
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.zip.Deflater

class PlantUmlClient(server: String) {
    private val logger = Logger.getInstance(PlantUmlClient::class.java)
    private val server = server

    fun get(uml: String, type: String): ByteArray {
        var urlText = "$server/$type/~1$uml"
        logger.info("PlantUmlClient urlText: {$urlText}")

        val url = URL(urlText)
        val input: InputStream = url.openStream()
        val bytes = input.readBytes()
        return bytes
    }

    fun getSvg(uml: String): ByteArray {
        return get(uml, "svg")
    }

    fun getPng(uml: String): ByteArray {
        return get(uml, "png")
    }

    private fun encodePlantUml(text: String): String {
        val deflated = Deflater(9).run {
            val input = text.toByteArray(Charsets.UTF_8)
            val output = ByteArray(8192)
            setInput(input)
            finish()
            val size = deflate(output)
            end()
            output.copyOf(size)
        }
        return encode64(deflated)
    }

    private fun encode64(data: ByteArray): String {
        val cs = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_"
        val sb = StringBuilder()
        var i = 0

        while (i < data.size) {
            val b1 = data[i].toInt() and 0xff
            val b2 = if (i + 1 < data.size) data[i + 1].toInt() and 0xff else 0
            val b3 = if (i + 2 < data.size) data[i + 2].toInt() and 0xff else 0

            sb.append(cs[b1 shr 2])
            sb.append(cs[((b1 and 0x3) shl 4) or (b2 shr 4)])
            sb.append(cs[((b2 and 0xf) shl 2) or (b3 shr 6)])
            sb.append(cs[b3 and 0x3f])

            i += 3
        }

        return sb.toString()
    }
}

