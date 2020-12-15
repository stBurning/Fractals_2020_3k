package ru.taerd.panels

import ru.smak.gui.components.GraphicsPanel
import ru.smak.gui.graphics.FractalPainter
import ru.smak.gui.graphics.SelectionFramePainter
import ru.smak.gui.graphics.colorScheme5
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.math.fractals.Mandelbrot
import java.awt.Dimension
import java.awt.event.*
import javax.swing.border.EtchedBorder

class VideoPanel : GraphicsPanel() {

    val plane = CartesianScreenPlane(width, height, -2.0, 1.0, -1.0, 1.0)
    val fp = FractalPainter(plane)
    var fractal = Mandelbrot()

    init {
        this.border = EtchedBorder()
        minimumSize = Dimension(1600, 900)
        val mfp = SelectionFramePainter(this.graphics)

        fp.isInSet = fractal::isInSet

        fp.getColor = ::colorScheme5

        fp.addImageReadyListener { this.repaint() }

        with(this) {
            background = java.awt.Color.WHITE
            addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    plane.realWidth = width
                    plane.realHeight = height
                    mfp.g = graphics
                    repaint()
                }
            })
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent?) {
                    e?.let {
                        mfp.isVisible = true
                        mfp.startPoint = it.point
                    }
                }

                override fun mouseReleased(e: MouseEvent?) {
                    e?.let {
                        mfp.currentPoint = it.point
                    }
                    mfp.isVisible = false
                    mfp.selectionRect?.apply {
                        if (width > 3 && height > 3) {
                            val xMin = ru.smak.gui.graphics.convertation.Converter.xScr2Crt(x, plane)
                            val xMax = ru.smak.gui.graphics.convertation.Converter.xScr2Crt(x + width, plane)
                            val yMin = ru.smak.gui.graphics.convertation.Converter.yScr2Crt(y + height, plane)
                            val yMax = ru.smak.gui.graphics.convertation.Converter.yScr2Crt(y, plane)
                            plane.let {
                                it.xMin = xMin
                                it.xMax = xMax
                                it.yMin = yMin
                                it.yMax = yMax
                            }
                        }
                    }
                    repaint()
                }
            })
            addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseDragged(e: MouseEvent?) {
                    e?.let {
                        mfp.currentPoint = it.point
                    }
                }
            })
            addPainter(fp)
        }

    }
}