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
import javax.swing.JButton
import javax.swing.JFrame

class MainWindow : JFrame(){

    private val history = History()
    private val minSize = Dimension(300, 200)
    private val mainPanel: GraphicsPanel
    val ButtonBack = JButton("Назад")
    val ButtonReset = JButton("Сбросить")
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
        fp.isInSet = fractal::isInSet
        fp.getColor = ::colorScheme5
        fp.addImageReadyListener { mainPanel.repaint() }

        fun updatePlane(xMin: Double, xMax: Double, yMin: Double, yMax: Double) {
            plane.also {
                it.xMin = xMin
                it.xMax = xMax
                it.yMin = yMin
                it.yMax = yMax
            }
        }

        fun onUndo() {
            val coords = history.undo()
            if (coords != null) {
                updatePlane(coords.xMin, coords.xMax, coords.yMin, coords.yMax)
                repaint()
            }
        }

        fun onReset() {
            history.reset()
            updatePlane(-2.0, 1.0, -1.0, 1.0)
            repaint()
        }

        with (mainPanel){
            background = Color.WHITE
            addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    plane.realWidth = mainPanel.width
                    plane.realHeight = mainPanel.height
                    mfp.g = mainPanel.graphics
                    mainPanel.repaint()
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
                        if (width > 3 && height > 3) {
                            history.add(History.Coords(plane.xMin, plane.xMax, plane.yMin, plane.yMax))
                            val xMin = Converter.xScr2Crt(x, plane)
                            val xMax = Converter.xScr2Crt(x + width, plane)
                            val yMin = Converter.yScr2Crt(y + height, plane)
                            val yMax = Converter.yScr2Crt(y, plane)
                            updatePlane(xMin, xMax, yMin, yMax)
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

            ButtonBack.addActionListener {
                onUndo()
            }
            ButtonBack.mnemonic = KeyEvent.VK_Z  // сначала нужно сделать пункт меню, а потом к нему добавить мнемонику: menuItem.mnemonic = KeyEvent.VK_Z
            ButtonReset.addActionListener {
                onReset()
            }
            ButtonReset.mnemonic = KeyEvent.VK_R  // сначала нужно сделать пункт меню, а потом к нему добавить мнемонику:  menuItem.mnemonic = KeyEvent.VK_R

            addPainter(fp)
        }
    }
}