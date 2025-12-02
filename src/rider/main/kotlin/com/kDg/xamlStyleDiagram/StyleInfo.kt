package com.kDg.xamlStyleDiagram

data class StyleInfo(val key: String, val targetType: String?, val baseOn: List<String>?) {
    override fun toString(): String {
        return "StyleInfo(key='$key', targetType=$targetType, baseOn=$baseOn)"
    }
}
