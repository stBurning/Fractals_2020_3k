package ru.smak.gui

import ru.smak.gui.components.GraphicsPanel
import ru.smak.gui.graphics.*
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.gui.graphics.convertation.Converter
import ru.smak.math.fractals.Mandelbrot
import ru.taerd.gui.VideoWindow
import java.awt.Color
import java.awt.Dimension
import java.awt.event.*
import javax.swing.GroupLayout
import javax.swing.JFrame


class MainWindow(Video: VideoWindow) : JFrame() {

    private val minSize = Dimension(300, 200)
    private val mainPanel: GraphicsPanel

    /*
    Экземпляр класса videoWindow
    Как показать и убрать окно
    video.isVisible=false/true
    Указано как менять цветовую палитру и другие параметры
    video.videoPanel.fp.getColor=::colorScheme5
    video.videoPanel.fp.isInSet=fractal::isInSet
    Также можно менять параметры у  plane
    video.videoPanel.plane.xMin = value
    Включать и выключать динамическую детализацию
    video.videoPanel.fractal.maxIters = value
    */
    private val video = Video

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Построение множества Мандельброта"
        minimumSize = Dimension(1600, 900)
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


        val mfp = SelectionFramePainter(mainPanel.graphics)
        val fractal = Mandelbrot()
        val fp = FractalPainter(plane)
        fp.isInSet = fractal::isInSet
        fp.getColor = ::colorScheme5
        fp.addImageReadyListener { mainPanel.repaint() }

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