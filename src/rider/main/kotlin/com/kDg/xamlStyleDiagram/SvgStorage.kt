package com.kDg.xamlStyleDiagram

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service(Service.Level.PROJECT)
class SvgStorage {

    var svgString: String? = null
    var panel: SvgStringPanel? = null
    var fileName: String? = null

    companion object {
        fun getInstance(project: com.intellij.openapi.project.Project): SvgStorage =
            project.service()
    }
}
