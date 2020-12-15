package ru.taerd.gui

import VideoProcessor
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.taerd.ImageProducer
import ru.taerd.panels.VideoPanel
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowListener
import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess


/**
 * Класс дополнительного окна,для создания видео по фреймам
 */
class VideoWindow : JFrame() {

    private val minSizeVidePanel = Dimension(400, 400)
    private val minSizePlaneScroll = Dimension(200, 380)

    private val minSizeAddFrameJButton = Dimension(150, 20)
    private val minSizeRemoveFrameJButton = Dimension(150, 20)

    private val minSizeStartJButton = Dimension(150, 20)
    private val minSizeStopJButton = Dimension(150, 20)

    private val minSizeTextField = Dimension(80, 20)
    private val minSizeTextLabel = Dimension(220, 20)

    private val minSizeProgressTextField = Dimension(150, 20)
    private val minSizeProgressBar = Dimension(150, 20)

    //Компоненты для создания видео
    private val queueList = mutableListOf<LinkedBlockingQueue<BufferedImage>>()
    private var videoProcessor: VideoProcessor? = null
    private var imageProducers = mutableListOf<ImageProducer>()
    private val PRODUCERS_COUNT = 2
    private val fps = 60
    private val WIDTH = 1920
    private val HEIGHT = 1080

    //Компоненты окна
    val videoPanel: VideoPanel
    private val addFrameButton = JButton("Добавить")
    private val startButton = JButton("Создать")
    private val removeFrameButton = JButton("Удалить")
    private val stopButton = JButton("Остановить")
    private val textField = JTextField("10")
    private val textLabel = JLabel("Время между переходами по кадрам")
    private val dlm = DefaultListModel<String>()
    private var list = JList(dlm)
    private var planeScroll = JScrollPane(list)
    private var timeBetweenFrames = 0
    private val frameList = mutableListOf<CartesianScreenPlane>()
    private val progressBar = JProgressBar(0, 100)
    private val progressTextLabel = JLabel("Процент выполнения")

    fun close(){
        videoPanel.plane.apply {
            xMin = -2.0
            xMax = 1.0
            yMin = -1.0
            yMax = 1.0

        }
        isVisible = false
        videoProcessor?.disable()
        imageProducers.forEach { pr ->
            pr.disable()
        }
        imageProducers.clear()
        frameList.clear()
        dlm.clear()
        progressBar.value = 0
        progressBar.isVisible = false
        progressTextLabel.isVisible = false
        dispose()
    }

    init {

        title = "Составление видео из кадров"
        minimumSize = Dimension(950, 700)
        videoPanel = VideoPanel()
        progressBar.isVisible = false
        progressTextLabel.isVisible = false
        layout = GroupLayout(contentPane).apply {
            setVerticalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addGroup(
                        createParallelGroup()
                            .addComponent(
                                videoPanel,
                                minSizeVidePanel.height,
                                minSizeVidePanel.height,
                                GroupLayout.DEFAULT_SIZE
                            )
                            .addGap(4)
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(
                                        planeScroll,
                                        minSizePlaneScroll.height,
                                        minSizePlaneScroll.height,
                                        GroupLayout.DEFAULT_SIZE
                                    )
                                    .addGap(4)
                                    .addGroup(
                                        createParallelGroup()
                                            .addComponent(
                                                addFrameButton,
                                                minSizeAddFrameJButton.height,
                                                minSizeAddFrameJButton.height,
                                                GroupLayout.DEFAULT_SIZE
                                            )
                                            .addGap(4)
                                            .addComponent(
                                                removeFrameButton,
                                                minSizeRemoveFrameJButton.height,
                                                minSizeRemoveFrameJButton.height,
                                                GroupLayout.DEFAULT_SIZE
                                            )
                                    )
                                    .addGap(4)
                                    .addGroup(
                                        createParallelGroup()
                                            .addComponent(
                                                textLabel,
                                                minSizeTextLabel.height,
                                                minSizeTextLabel.height,
                                                GroupLayout.DEFAULT_SIZE
                                            )
                                            .addGap(4)
                                            .addComponent(
                                                textField,
                                                minSizeTextField.height,
                                                minSizeTextField.height,
                                                GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                                    .addGap(4)
                                    .addGroup(
                                        createParallelGroup()
                                            .addComponent(
                                                startButton,
                                                minSizeStartJButton.height,
                                                minSizeStartJButton.height,
                                                GroupLayout.DEFAULT_SIZE
                                            )
                                            .addGap(4)
                                            .addComponent(
                                                stopButton,
                                                minSizeStopJButton.height,
                                                minSizeStopJButton.height,
                                                GroupLayout.DEFAULT_SIZE
                                            )
                                    )
                                    .addGap(4)
                                    .addGroup(
                                        createParallelGroup()
                                            .addComponent(
                                                progressTextLabel,
                                                minSizeProgressTextField.height,
                                                minSizeProgressTextField.height,
                                                GroupLayout.DEFAULT_SIZE
                                            )
                                            .addGap(4)
                                            .addComponent(
                                                progressBar,
                                                minSizeProgressBar.height,
                                                minSizeProgressBar.height,
                                                GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            )
                    )
                    .addGap(4)
            )
            setHorizontalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addComponent(videoPanel, minSizeVidePanel.width, minSizeVidePanel.width, GroupLayout.DEFAULT_SIZE)
                    .addGap(4)
                    .addGroup(
                        createParallelGroup()
                            .addComponent(
                                planeScroll,
                                minSizePlaneScroll.width,
                                minSizePlaneScroll.width,
                                GroupLayout.DEFAULT_SIZE
                            )
                            .addGap(4)
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(
                                        addFrameButton,
                                        minSizeAddFrameJButton.width,
                                        minSizeAddFrameJButton.width,
                                        GroupLayout.DEFAULT_SIZE
                                    )
                                    .addGap(4)
                                    .addComponent(
                                        removeFrameButton,
                                        minSizeRemoveFrameJButton.width,
                                        minSizeRemoveFrameJButton.width,
                                        GroupLayout.DEFAULT_SIZE
                                    )
                            )
                            .addGap(4)
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(
                                        textLabel,
                                        minSizeTextLabel.width,
                                        minSizeTextLabel.width,
                                        GroupLayout.DEFAULT_SIZE
                                    )
                                    .addGap(4)
                                    .addComponent(
                                        textField,
                                        minSizeTextField.width,
                                        minSizeTextField.width,
                                        GroupLayout.PREFERRED_SIZE
                                    )
                            )
                            .addGap(4)
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(
                                        startButton,
                                        minSizeStartJButton.width,
                                        minSizeStartJButton.width,
                                        GroupLayout.DEFAULT_SIZE
                                    )
                                    .addGap(4)
                                    .addComponent(
                                        stopButton,
                                        minSizeStopJButton.width,
                                        minSizeStopJButton.width,
                                        GroupLayout.DEFAULT_SIZE
                                    )
                            )
                            .addGap(4)
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(
                                        progressTextLabel,
                                        minSizeProgressTextField.width,
                                        minSizeProgressTextField.width,
                                        GroupLayout.DEFAULT_SIZE
                                    )
                                    .addGap(4)
                                    .addComponent(
                                        progressBar,
                                        minSizeProgressBar.width,
                                        minSizeProgressBar.width,
                                        GroupLayout.PREFERRED_SIZE
                                    )
                            )
                    )
                    .addGap(4)
            )
        }
        pack()
        with(addFrameButton) {
            //Событие возникающее при нажатии кнопку добавления фрейма
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    frameList.add(videoPanel.plane.copy())
                    val arrPlane = videoPanel.plane.copy()
                    dlm.addElement("xMin: " + arrPlane.xMin.toString() + " xMax: " + arrPlane.xMax.toString() + " yMin: " + arrPlane.yMin.toString() + " yMax: " + arrPlane.yMax.toString())
                    val index: Int = dlm.size() - 1
                    list.selectedIndex = index
                    list.ensureIndexIsVisible(index)
                    list.selectedIndex = 0
                    list.isFocusable = false
                }
            })
        }
        //Событие возникающее при нажатии на кнопку удаления фрейма (Удаляется выделенный)
        with(removeFrameButton) {
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    val indexRemove = list.selectedIndex
                    if (indexRemove >= 0) {
                        dlm.remove(indexRemove)
                        frameList.removeAt(indexRemove)
                        //list.remove(indexRemove)
                    }
                }
            })
        }

        with(startButton) {
            //Событие возникающее при нажатии на создание видео
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    startButton.isEnabled = false
                    addFrameButton.isEnabled = false
                    removeFrameButton.isEnabled = false
                    timeBetweenFrames = getValidValue(textField.text)
                    val snapsCount = timeBetweenFrames * fps

                    for (i in 0 until PRODUCERS_COUNT) {
                        queueList.add(LinkedBlockingQueue(300))
                        imageProducers.add(
                            ImageProducer(
                                i,
                                PRODUCERS_COUNT,
                                queueList[i],
                                WIDTH,
                                HEIGHT,
                                frameList,
                                snapsCount,
                            videoPanel.fp.isInSet,
                            videoPanel.fp.getColor)
                        )
                        thread { imageProducers[i].run() }
                    }
                    videoProcessor =
                        VideoProcessor(queueList, WIDTH, HEIGHT, timeBetweenFrames * (frameList.size - 1), fps)
                    progressTextLabel.isVisible = true
                    progressBar.isVisible = true
                    videoProcessor!!.addProgressListener {
                        progressBar.value = (it * 100).toInt()
                        progressBar.repaint()
                    }
                    videoProcessor!!.addFinishListener {
                        progressTextLabel.isVisible = false
                        progressBar.isVisible = false
                        startButton.isEnabled = true
                        addFrameButton.isEnabled = true
                        removeFrameButton.isEnabled = true
                        JOptionPane.showMessageDialog(videoPanel, "Видео создано")
                    }
                    thread { videoProcessor!!.run() }
                }
            })
        }
        with(stopButton) {
            //Событие возникающее при нажатии на кнопку Остановить
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    startButton.isEnabled = true
                    addFrameButton.isEnabled = true
                    removeFrameButton.isEnabled = true
                    try {
                        videoProcessor?.disable()
                        imageProducers.forEach { imageProducer ->
                            imageProducer.disable()
                        }
                    } catch (e: InterruptedException) {
                        println("InterruptedException")
                    }
                }
            })
        }
    }

    /**
     * Функция проверки валидности строки на число
     * @param inputStr - строка, в которой записано число
     * @return result - число от 1 до 30
     */
    fun getValidValue(inputStr: String): Int {
        //println(inputStr)
        return try {
            val result = Integer.parseInt(inputStr)
            //println(result)
            if (result in 1..29) result
            else {
                textField.text = "value set 10"
                10
            }
        } catch (e: Exception) {
            println(e.message + " -incorrect value")
            textField.text = 10.toString()
            10
        }
    }
}