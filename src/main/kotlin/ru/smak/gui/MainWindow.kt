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
    private val fp: FractalPainter


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
//
        val mfp = SelectionFramePainter(mainPanel.graphics)
        fp = FractalPainter(plane)
        createMandelbrot()
//
        //val menu = Menu(this)
        //jMenuBar = menu.jMenuBar


        fp.addImageReadyListener { mainPanel.repaint() }

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
    fun changeColorScheme(i: Int){
        if(i ==1){fp.getColor = ::colorScheme1 }
        if(i==2){fp.getColor = ::colorScheme2 }
        if(i==3){fp.getColor = ::colorScheme3 }
        if(i==4){fp.getColor = ::colorScheme4 }
        repaint()
    }
    fun createMandelbrot(){
        title = "Построение множества Мандельброта"
        val fractal = Mandelbrot()
        fp.isInSet = fractal::isInSet
        fp.getColor = ::colorScheme5
    }
    fun createJulia(){
        title = "Построение множества Жюлия"
        // тут для Жюлия так же как и в createMandelbrot()
        //функции для fp!!!
    }
    //sd:SaveData
    fun open(){
        val plane1 = CartesianScreenPlane(
                mainPanel.width, mainPanel.height,
                -2.0, 1.0, -1.0, 1.0
        )
        fp.plane.let {
            it.xMin = -2.0
            it.xMax = 1.0
            it.yMin = -1.0
            it.yMax = 1.0
        }

        /*
        в итоге будет так
        fp.plane.let {
            it.xMin = sd.xMin
            it.xMax = sd.xMax
            it.yMin = sd.yMin
            it.yMax = sd.yMax
        }*/
        changeColorScheme(3)
        createMandelbrot()
    }


}