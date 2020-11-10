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
        mainPanel.background = Color.WHITE
        val gl = GroupLayout(contentPane)

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGap(4)
            .addComponent(mainPanel, minSize.height, minSize.height, GroupLayout.DEFAULT_SIZE)
            .addGap(4)
        )

        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGap(4)
            .addGroup(
                gl.createParallelGroup()
                    .addComponent(mainPanel, minSize.width, minSize.width, GroupLayout.DEFAULT_SIZE)
            )
            .addGap(4))
        layout = gl

        pack()

        val plane = CartesianScreenPlane(
            mainPanel.width, mainPanel.height,
            -2.0, 1.0, -1.0, 1.0
        )

        mainPanel.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                plane.realWidth = mainPanel.width
                plane.realHeight = mainPanel.height
            }
        })

        val mfp = MouseFramePainter(mainPanel.graphics)

        mainPanel.addMouseListener(object: MouseAdapter(){
            override fun mousePressed(e: MouseEvent?) {
                e?.let {
                    mfp.isVisible = true
                    mfp.startPoint = it.point
                }
            }
            override fun mouseReleased(e: MouseEvent?) {
                mfp.isVisible = false
                e?.let{
                    mfp.currentPoint = it.point
                }
            }
        })

        mainPanel.addMouseMotionListener(object : MouseAdapter(){
            override fun mouseDragged(e: MouseEvent?) {
                e?.let{
                    mfp.currentPoint = it.point
                }
            }
        })

        val fp = FractalPainter(plane)
        val fractal = Mandelbrot()
        fp.fractalTest = fractal::isInSet
        fp.getColor = ::colorScheme4

        mainPanel.addPainter(fp)
    }
}