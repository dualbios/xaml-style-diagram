package com.kDg.xamlStyleDiagram

import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.DocumentLoader
import org.apache.batik.bridge.GVTBuilder
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.dom.svg.SAXSVGDocumentFactory
import org.apache.batik.gvt.GraphicsNode
import org.apache.batik.util.XMLResourceDescriptor
import java.awt.*
import java.awt.geom.AffineTransform
import java.io.ByteArrayInputStream
import javax.swing.JComponent
import javax.swing.Scrollable

class SvgStringPanel : JComponent(), Scrollable {
    private var lastSvg: String? = null
    private var graphicsNode: GraphicsNode? = null
    private var svgDocument: org.w3c.dom.svg.SVGDocument? = null

    var zoomValue: Double = 1.0
        set(value) {
            field = value.coerceIn(0.1, 10.0)
            revalidate()
            repaint()
        }

    fun zoomIn() = zoomValue.let {
        zoomValue = it * 1.1
    }

    fun zoomOut() = zoomValue.let {
        zoomValue = it / 1.1
    }

    fun resetZoom() {
        zoomValue = 1.0
    }

    fun updateSvg(svg: String) {
        lastSvg = svg

        val parser = XMLResourceDescriptor.getXMLParserClassName()
        val factory = SAXSVGDocumentFactory(parser)

        val inputStream = ByteArrayInputStream(svg.toByteArray())
        svgDocument = factory.createDocument(null, inputStream) as org.w3c.dom.svg.SVGDocument

        val userAgent = UserAgentAdapter()
        val loader = DocumentLoader(userAgent)
        val ctx = BridgeContext(userAgent, loader)
        ctx.isDynamic = false

        val builder = GVTBuilder()
        graphicsNode = builder.build(ctx, svgDocument)

        revalidate()
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val node = graphicsNode ?: return
        val g2 = g as Graphics2D

        // фон
        g2.color = background
        g2.fillRect(0, 0, width, height)

        val transform = g2.transform
        val scaled = transform.clone() as AffineTransform
        scaled.scale(zoomValue, zoomValue)
        g2.transform = scaled

        node.paint(g2)

        g2.transform = transform
    }

    override fun getPreferredSize(): Dimension {
        val doc = svgDocument ?: return super.getPreferredSize()
        val root = doc.rootElement

        val w = root.getAttribute("width").removeSuffix("px").toDoubleOrNull() ?: 200.0
        val h = root.getAttribute("height").removeSuffix("px").toDoubleOrNull() ?: 200.0

        return Dimension((w * zoomValue).toInt(), (h * zoomValue).toInt())
    }

    // Scrollable
    override fun getPreferredScrollableViewportSize(): Dimension = preferredSize
    override fun getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int = 16
    override fun getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int =
        if (orientation == Adjustable.HORIZONTAL) visibleRect.width else visibleRect.height

    override fun getScrollableTracksViewportWidth(): Boolean = false
    override fun getScrollableTracksViewportHeight(): Boolean = false

    init {
        addMouseWheelListener { e ->
            if (e.isControlDown) {
                if (e.preciseWheelRotation < 0) zoomIn() else zoomOut()
                repaint()
                revalidate()
                e.consume()
            }
        }
    }
}
