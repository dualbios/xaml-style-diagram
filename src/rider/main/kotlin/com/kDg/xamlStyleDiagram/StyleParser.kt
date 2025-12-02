package com.kDg.xamlStyleDiagram

import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory

class StyleParser {
    companion object {
        fun parse(xaml: String): List<StyleInfo> {
            val styles = mutableListOf<StyleInfo>()
            val seenKeys = mutableSetOf<String>()
            var generatedKeyCounter = 0
            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val inputStream = ByteArrayInputStream(xaml.toByteArray(Charsets.UTF_8))
            val doc = docBuilder.parse(inputStream)
            doc.documentElement.normalize()
            val styleNodes = doc.getElementsByTagName("Style")
            val staticResourceRegex = Regex("""\{StaticResource\s+([^\}]+)\}""")
            for (i in 0 until styleNodes.length) {
                val node = styleNodes.item(i)
                if (node is Element) {
                    val key = node.getAttribute("x:Key")
                    var uniqueKey = key.ifBlank { null }
                    if (uniqueKey == null) {
                        // Generate a unique key if missing
                        do {
                            uniqueKey = "Noname Style ${generatedKeyCounter++}"
                        } while (seenKeys.contains(uniqueKey))
                    }
                    val targetType = node.getAttribute("TargetType")
                    val baseOnAttr = node.getAttribute("BasedOn")
                    val baseOn: List<String>? = if (baseOnAttr.isNotBlank()) {
                        val matches = staticResourceRegex.findAll(baseOnAttr).map { it.groupValues[1].trim() }.toList()
                        when {
                            matches.isNotEmpty() -> matches
                            else -> listOf(baseOnAttr)
                        }
                    } else {
                        null
                    }
                    if (!seenKeys.contains(uniqueKey)) {
                        styles.add(
                            StyleInfo(
                                uniqueKey,
                                targetType.ifBlank { null },
                                baseOn
                            )
                        )
                        seenKeys.add(uniqueKey)
                    }
                }
            }
            return styles
        }
    }
}
