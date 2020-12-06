package ru.smak.gui

import VideoProcessor
import ru.smak.gui.components.GraphicsPanel
import ru.smak.gui.graphics.FractalPainter
import ru.smak.gui.graphics.SelectionFramePainter
import ru.smak.gui.graphics.colorScheme5
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.gui.graphics.convertation.Converter
import ru.smak.math.fractals.Mandelbrot
import java.awt.Color
import java.awt.Dimension
import java.awt.event.*
import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.GroupLayout
import javax.swing.JFrame
import kotlin.concurrent.thread


class MainWindow : JFrame() {

    private val queue = LinkedBlockingQueue<BufferedImage>(80)
    private val minSize = Dimension(300, 200)
    private val mainPanel: GraphicsPanel

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Построение множества Мандельброта"
        minimumSize = Dimension(1920, 1080)
        mainPanel = GraphicsPanel()
        layout = GroupLayout(contentPane).apply {
            setVerticalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addComponent(mainPanel, minSize.height, minSize.height, GroupLayout.DEFAULT_SIZE)
                    .addGap(4)
            )
            setHorizontalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addGroup(
                        createParallelGroup()
                            .addComponent(mainPanel, minSize.width, minSize.width, GroupLayout.DEFAULT_SIZE)
                    )
                    .addGap(4)
            )
        }

        pack()

        val plane = CartesianScreenPlane(
            mainPanel.width, mainPanel.height,
            -2.0, 1.0, -1.0, 1.0
        )

        val videoProcessor = VideoProcessor(queue, mainPanel.width, mainPanel.height)

        val mfp = SelectionFramePainter(mainPanel.graphics)
        val fractal = Mandelbrot()
        val fp = FractalPainter(plane)
        fp.isInSet = fractal::isInSet
        fp.getColor = ::colorScheme5
        fp.addImageReadyListener { mainPanel.repaint() }
        var i = 0
        fp.addImageGetReadyListener { img ->
            queue.put(img)
            if (i == 0) {
                thread { videoProcessor.run() }
            }

            if (i < 150) {
                plane.let {
                    it.xMin = it.xMin + 0.005
                    it.xMax = it.xMax - 0.005
                    it.yMin = it.yMin + 0.005
                    it.yMax = it.yMax - 0.005
                }
                mainPanel.repaint()
                i++
            }

            println("$i Изображение добавлено в очередь")
        }


        with(mainPanel) {
            background = Color.WHITE
            addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    plane.realWidth = mainPanel.width
                    plane.realHeight = mainPanel.height
                    mfp.g = mainPanel.graphics
                    mainPanel.repaint()

                }
            })
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
//                    if (e != null) {
//                        if(e.button == MouseEvent.BUTTON3)
//                            //thread {videoProcessor.run()}
//                    }
                }

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
                            val xMin = Converter.xScr2Crt(x, plane)
                            val xMax = Converter.xScr2Crt(x + width, plane)
                            val yMin = Converter.yScr2Crt(y + height, plane)
                            val yMax = Converter.yScr2Crt(y, plane)
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