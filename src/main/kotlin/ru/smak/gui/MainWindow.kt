package ru.smak.gui

import ru.smak.gui.components.GraphicsPanel
import ru.smak.gui.graphics.*
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.gui.graphics.convertation.Converter
import ru.smak.julia.JuliaSetWindow
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
    internal var updated: Boolean = false
    private val fractal = Mandelbrot()

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
    internal val video = Video
    private val juliaSetWindow = JuliaSetWindow()

    init{
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Построение множества Мандельброта"
        minimumSize = Dimension(600, 600)
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
                        if (it.button == MouseEvent.BUTTON1) {
                            mfp.isVisible = true
                            mfp.startPoint = it.point
                        } else if (it.button == MouseEvent.BUTTON3) {
                            if (juliaSetWindow.isLaunched.not()) {
                                juliaSetWindow.launch()
                            }
                            juliaSetWindow.updateState(Converter.xScr2Crt(it.x, plane), Converter.yScr2Crt(it.y, plane))
                        }
                    }
                }
                override fun mouseReleased(e: MouseEvent?) {
                    e?.let {
                        if (it.button == MouseEvent.BUTTON1) {
                            mfp.currentPoint = e.point
                            mfp.isVisible = false
                            mfp.selectionRect?.apply {
                                if (width > 3 && height > 3) {
                                    history.add(Coords(plane.xMin, plane.xMax, plane.yMin, plane.yMax))
                                    val old = Coords(plane.xMin, plane.xMax, plane.yMin, plane.yMax)
                                    val xMin = Converter.xScr2Crt(x, plane)
                                    val xMax = Converter.xScr2Crt(x + width, plane)
                                    val yMin = Converter.yScr2Crt(y + height, plane)
                                    val yMax = Converter.yScr2Crt(y, plane)
                                    val new = Coords(xMin, xMax, yMin, yMax)
                                    if (updated) {
                                        fractal.updateMaxIterations(new, old)  //добавить флажок с менюшниками
                                    }
                                    updatePlane(xMin, xMax, yMin, yMax)
                                }
                            }
                            repaint()
                        } else if (it.button == MouseEvent.BUTTON3) {
                            juliaSetWindow.updateState(Converter.xScr2Crt(it.x, plane), Converter.yScr2Crt(it.y, plane))
                        }
                    }
                }
            })
            addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseDragged(e: MouseEvent?) {
                    e?.let {
                        if (it.button == MouseEvent.BUTTON1) {
                            mfp.currentPoint = e.point
                        } else if (it.button == MouseEvent.BUTTON3) {
                            juliaSetWindow.updateState(Converter.xScr2Crt(it.x, plane), Converter.yScr2Crt(it.y, plane))
                        }
                    }
                }
            })
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
        video.videoPanel.plane.also {
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
        when (i) {
            1 -> {
                fp.getColor = ::colorScheme1
                video.videoPanel.fp.getColor = ::colorScheme1
                juliaSetWindow.colorScheme = 1
            }
            2 -> {
                fp.getColor = ::colorScheme2
                video.videoPanel.fp.getColor = ::colorScheme2

            }
            3 -> {
                fp.getColor = ::colorScheme3
                video.videoPanel.fp.getColor = ::colorScheme3
            }
            4 -> {
                fp.getColor = ::colorScheme4
                video.videoPanel.fp.getColor = ::colorScheme4
            }
            5 -> {
                fp.getColor = ::colorScheme5
                video.videoPanel.fp.getColor = ::colorScheme5
            }
        }
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