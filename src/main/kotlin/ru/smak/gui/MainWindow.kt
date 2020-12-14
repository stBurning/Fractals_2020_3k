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
//import javax.swing.JButton
import javax.swing.JFrame


class MainWindow(Video: VideoWindow) : JFrame() {

    private val history = History()
    private val minSize = Dimension(300, 200)
    private val mainPanel: GraphicsPanel
    internal val fp: FractalPainter
    internal val plane:  CartesianScreenPlane
    internal var updated:Boolean=false
    val fractal = Mandelbrot()

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

//    private val buttonBack = JButton("Назад")
//    private val buttonReset = JButton("Сбросить")
    init{

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

        plane = CartesianScreenPlane(
            mainPanel.width, mainPanel.height,
            -2.0, 1.0, -1.0, 1.0
        )

        val mfp = SelectionFramePainter(mainPanel.graphics)

        //createMandelbrot()
       // fractal = Mandelbrot()



        val fractal = Mandelbrot()
        fp = FractalPainter(plane)

        val menu = Menu(this)
        jMenuBar = menu.jMenuBar






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
                            history.add(Coords(plane.xMin, plane.xMax, plane.yMin, plane.yMax))
                            val old = Coords(plane.xMin,plane.xMax,plane.yMin,plane.yMax)
                            val xMin = Converter.xScr2Crt(x, plane)
                            val xMax = Converter.xScr2Crt(x + width, plane)
                            val yMin = Converter.yScr2Crt(y + height, plane)
                            val yMax = Converter.yScr2Crt(y, plane)
                            val new = Coords(xMin,xMax,yMin,yMax)
                            if(updated) {
                                fractal.updateMaxIterations(new, old)  //добавить флажок с менюшниками
                            }
                            updatePlane(xMin, xMax, yMin, yMax)
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

//            SaveFractal.invoke(plane, fp.savedImage, true, "colorScheme1") //сначала нужно сделать пункт меню, потом перенести эту строку в обработчик нажатия на кнопку
//            buttonBack.addActionListener {
//                onUndo()
//            }
//            buttonBack.mnemonic = KeyEvent.VK_Z  // сначала нужно сделать пункт меню, а потом к нему добавить мнемонику: menuItem.mnemonic = KeyEvent.VK_Z
//            buttonReset.addActionListener {
//                onReset()
//            }
//            buttonReset.mnemonic = KeyEvent.VK_R  // сначала нужно сделать пункт меню, а потом к нему добавить мнемонику:  menuItem.mnemonic = KeyEvent.VK_R

            addPainter(fp)


        }

    }

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

    fun changeColorScheme(i: Int){
        if(i ==1){fp.getColor = ::colorScheme1 }
        if(i==2){fp.getColor = ::colorScheme2 }
        if(i==3){fp.getColor = ::colorScheme3 }
        if(i==4){fp.getColor = ::colorScheme4 }
        repaint()
    }
    fun createMandelbrot(){
        title = "Построение множества Мандельброта"
        fp.isInSet = fractal::isInSet

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