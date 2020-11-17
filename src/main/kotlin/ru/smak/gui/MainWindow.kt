package ru.smak.gui

import ru.smak.gui.components.GraphicsPanel
import ru.smak.gui.graphics.*
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.gui.graphics.convertation.Converter
import ru.smak.math.fractals.Mandelbrot
import java.awt.Color
import java.awt.Dimension
import java.awt.event.*
import javax.swing.GroupLayout
import javax.swing.JFrame

class MainWindow : JFrame(){

    private val minSize = Dimension(300, 200)
    private val mainPanel: GraphicsPanel
    init{
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Построение множества Мандельброта"
        minimumSize = Dimension(700, 700)
        mainPanel = GraphicsPanel()

        layout = GroupLayout(contentPane).apply{
            setVerticalGroup(createSequentialGroup()
                    .addGap(4)
                    .addComponent(mainPanel, minSize.height, minSize.height, GroupLayout.DEFAULT_SIZE)
                    .addGap(4)
            )
            setHorizontalGroup(createSequentialGroup()
                    .addGap(4)
                    .addGroup(
                            createParallelGroup()
                                    .addComponent(mainPanel, minSize.width, minSize.width, GroupLayout.DEFAULT_SIZE)
                    )
                    .addGap(4))
        }

        pack()

        val plane = CartesianScreenPlane(
            mainPanel.width, mainPanel.height,
            -2.0, 1.0, -1.0, 1.0
        )

        val mfp = SelectionFramePainter(mainPanel.graphics)
        val fractal = Mandelbrot()
        val fp = FractalPainter(plane)
        fp.fractalTest = fractal::isInSet
        fp.getColor = ::colorScheme4

        with (mainPanel){
            background = Color.WHITE
            addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    plane.realWidth = mainPanel.width
                    plane.realHeight = mainPanel.height
                    mfp.g = mainPanel.graphics
                }
            })
            addMouseListener(object: MouseAdapter(){
                override fun mousePressed(e: MouseEvent?) {
                    e?.let {
                        mfp.isVisible = true
                        mfp.startPoint = it.point
                    }
                }
                override fun mouseReleased(e: MouseEvent?) {
                    e?.let{
                        mfp.currentPoint = it.point
                    }
                    mfp.isVisible = false
                    mfp.selectionRect?.apply {
                        val xMin = Converter.xScr2Crt(x, plane)
                        val xMax = Converter.xScr2Crt(x + width, plane)
                        val yMin = Converter.yScr2Crt(y + height, plane)
                        val yMax = Converter.yScr2Crt(y, plane)
                        plane.also{
                            it.xMin = xMin
                            it.xMax = xMax
                            it.yMin = yMin
                            it.yMax = yMax
                        }
                    }
                    repaint()
                }
            })
            addMouseMotionListener(object : MouseMotionAdapter(){
                override fun mouseDragged(e: MouseEvent?) {
                    e?.let{
                        mfp.currentPoint = it.point
                    }
                }
            })
            addPainter(fp)
        }
    }
}