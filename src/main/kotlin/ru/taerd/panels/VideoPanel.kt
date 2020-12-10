package ru.taerd.panels

import VideoProcessor
import ru.smak.gui.components.GraphicsPanel
import ru.smak.gui.graphics.FractalPainter
import ru.smak.gui.graphics.SelectionFramePainter
import ru.smak.gui.graphics.colorScheme5
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.math.fractals.Mandelbrot
import java.awt.event.*
import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.border.EtchedBorder
import kotlin.concurrent.thread

class VideoPanel : GraphicsPanel() {

    public var plane = CartesianScreenPlane(width, height, -2.0, 1.0, -1.0, 1.0)
        private set

    //Создать свойство или еще что то для получения colorScheme

    //public var getColorScheme: ()->(Float)= colorScheme5()
    private val queue = LinkedBlockingQueue<BufferedImage>(100)
    private val videoProcessor = VideoProcessor(queue, 1600, 900)

    init {
        this.border = EtchedBorder()
        plane = CartesianScreenPlane(
                this.width, this.height,
                -2.0, 1.0, -1.0, 1.0
        )
        //thread {  videoProcessor.run()}

        val mfp = SelectionFramePainter(this.graphics)
        val fractal = Mandelbrot()
        val fp = FractalPainter(plane)

        fp.isInSet = fractal::isInSet

        fp.getColor = ::colorScheme5//getColorScheme()

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