package com.kDg.xamlStyleDiagram

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import java.io.File

class ShowStyleDiagramAction : AnAction() {
    private val logger = Logger.getInstance(PlantUmlClient::class.java)

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = virtualFile?.extension?.equals("xaml", ignoreCase = true) == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
            val project = e.project ?: return
            val settings = StyleDiagramSettings.getInstance()

            var xaml = File(virtualFile.path).readText()

            val styles: List<StyleInfo> = StyleParser.parse(xaml)
            val stylesText = styles.joinToString("\n") { it.toString() }
            logger.info("Styles: {$stylesText}")

            var svgString = "<svg xmlns=\"http://www.w3.org/2000/svg\" />"
            if (!styles.isEmpty()) {
                val uml = PlantUmlGenerator(settings.umlParams, settings.isClassDiagram).toPlantUml(styles)
                var encodeUml = PlantUmlTextEncoder().encode(uml)
                val svg = PlantUmlClient(settings.plantUmlServerUrl).getSvg(encodeUml)
                svgString = String(svg, Charsets.UTF_8)
            }

            val storage = project.service<SvgStorage>()
            storage.svgString = svgString
            storage.panel?.updateSvg(svgString)   // 🔥 оновлюємо панель
            storage.fileName = virtualFile.name

            val tw = ToolWindowManager
                .getInstance(project)
                .getToolWindow("Xaml Style Diagram")

            tw?.show()
        } catch (ex: Exception) {
            Messages.showErrorDialog(
                e.project,
                "Error generating style diagram: ${ex.message}\n" +
                    "Exception: ${ex::class.qualifiedName}\n" +
                    "Stack trace:\n${ex.stackTraceToString()}",
                "Error"
            )
        }
    }
}
