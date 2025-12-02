package com.kDg.xamlStyleDiagram

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

class SvgToolWindowFactory : ToolWindowFactory {

    var lastDragPoint: Point? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val storage = project.service<SvgStorage>()

        val panel = SvgStringPanel()
        storage.panel = panel
        storage.svgString?.let {
            panel.updateSvg(it) // Replace with actual file name if available
        }
        val scrollPane = com.intellij.ui.components.JBScrollPane(panel)
        scrollPane.viewport.scrollMode = javax.swing.JViewport.BLIT_SCROLL_MODE // Ensure viewport updates

        // Enable panning by mouse wheel (horizontal with Shift, vertical otherwise)
        scrollPane.viewport.view.addMouseWheelListener { e ->
            if (e.isShiftDown) {
                val bar = scrollPane.horizontalScrollBar
                bar.value = (bar.value + e.unitsToScroll * bar.unitIncrement).coerceIn(0, bar.maximum)
            } else {
                val bar = scrollPane.verticalScrollBar
                bar.value = (bar.value + e.unitsToScroll * bar.unitIncrement).coerceIn(0, bar.maximum)
            }
        }

        scrollPane.viewport.view.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    lastDragPoint = e.point
                    panel.cursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.MOVE_CURSOR)
                }
            }
            override fun mouseReleased(e: MouseEvent) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    lastDragPoint = null
                    panel.cursor = java.awt.Cursor.getDefaultCursor()
                }
            }
        })

        scrollPane.viewport.view.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (SwingUtilities.isMiddleMouseButton(e) && lastDragPoint != null) {
                    val dx = lastDragPoint!!.x - e.x
                    val dy = lastDragPoint!!.y - e.y
                    scrollPane.horizontalScrollBar.value =
                        (scrollPane.horizontalScrollBar.value + dx).coerceIn(0, scrollPane.horizontalScrollBar.maximum)
                    scrollPane.verticalScrollBar.value =
                        (scrollPane.verticalScrollBar.value + dy).coerceIn(0, scrollPane.verticalScrollBar.maximum)
                    lastDragPoint = e.point
                }
            }
        })

        // Force panel to be revalidated and repainted when shown
        scrollPane.viewport.addChangeListener {
            panel.revalidate()
            panel.repaint()
        }

        val content = ContentFactory.getInstance().createContent(scrollPane, storage.fileName, false)
        toolWindow.contentManager.addContent(content)
    }
}
