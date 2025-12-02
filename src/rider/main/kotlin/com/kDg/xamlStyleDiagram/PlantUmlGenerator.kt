package com.kDg.xamlStyleDiagram


class PlantUmlGenerator(umlParams: String, private val isClassDiagram: Boolean) {
    private val umlParams: String = umlParams.ifBlank {
        "!theme amiga\nskinparam dpi 150"
    }

    fun toPlantUml(styles: List<StyleInfo>): String {
        val sb = StringBuilder()
        sb.append("@startuml\n")
        sb.append(umlParams)
        sb.append("\n")

        val objectType: String = if (isClassDiagram) "class" else "object"

        // Стилі як класи
        styles.forEach { style ->
            sb.append("$objectType \"${style.key ?: "Unnamed"}\" as ${styleKey(style)} {\n")
            sb.append("  targetType = ${style.targetType}\n")
            sb.append("}\n")
        }

        // Залежності BasedOn
        styles.forEach { style ->
            style.baseOn?.forEach { parent ->
                sb.append("${styleKeyByName(style.key)} --> ${styleKeyByName(parent)}\n")
            }
        }

        sb.append("@enduml")
        return sb.toString()
    }

    private fun styleKey(style: StyleInfo): String =
        styleKeyByName(style.key)

    private fun styleKeyByName(name: String?): String =
        name?.replace("[^a-zA-Z0-9_]".toRegex(), "_") ?: "Style_${System.identityHashCode(name)}"
}

