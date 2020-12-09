package ru.taerd.gui

import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.taerd.panels.VideoPanel
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.math.max
import kotlin.math.roundToInt


class VideoWindow : JFrame() {
    private val minSizeVidePanel = Dimension(300, 300)
    private val minSizePlaneScroll = Dimension(100, 250)
    private val minSizeAddFrameJButton = Dimension(40, 20)
    private val minSizeStartJButton = Dimension(40, 20)
    private val minSizeRemoveFrameJButton = Dimension(60, 20)

    private val videoPanel: VideoPanel

    private val addFrameButton = JButton("Добавить")
    private val startButton = JButton("Создать")
    private val removeFrameButton = JButton("Удалить")
    private val dlm = DefaultListModel<CartesianScreenPlane>()
    private var list = JList(dlm)
    private var planeScroll = JScrollPane(list)
    private var textArea = JTextArea(50, 50)

    //
    private val frameList = mutableListOf<CartesianScreenPlane>()


    init {

        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Составление видео из кадров"
        minimumSize = Dimension(600, 500)
        videoPanel = VideoPanel()
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
                                        GroupLayout.PREFERRED_SIZE
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
                                            .addGap(4)
                                    )
                                    .addGap(4)
                                    .addComponent(
                                        startButton,
                                        minSizeStartJButton.height,
                                        minSizeStartJButton.height,
                                        GroupLayout.DEFAULT_SIZE
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
                            .addComponent(
                                startButton,
                                minSizeStartJButton.width,
                                minSizeStartJButton.width,
                                GroupLayout.DEFAULT_SIZE
                            )
                    )
                    .addGap(4)
            )
        }



        with(addFrameButton) {

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)

                    dlm.addElement(videoPanel.plane)
                    val index: Int = dlm.size() - 1
                    list.selectedIndex = index
                    list.ensureIndexIsVisible(index)
                    list.selectedIndex = 0
                    list.isFocusable = false
                    textArea.text += videoPanel.plane.toString() + '\n'
                    println(textArea.text)
                }
            })
        }
        with(removeFrameButton) {
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {

                    super.mouseClicked(e)
                }
            })
        }
        with(startButton) {
            //Событие возникающее при нажатии на создание видео
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
//                    val snapsList = mutableListOf<CartesianScreenPlane>()
                    for (i in 0 until frameList.size - 1) {
                        println(frameList[i])

//                        val xMinDt = frameList[i].xMin - frameList[i + 1].xMin
//                        val xMaxDt = frameList[i].xMax - frameList[i + 1].xMax
//                        val yMinDt = frameList[i].yMin - frameList[i + 1].yMin
//                        val yMaxDt = frameList[i].yMax - frameList[i + 1].yMax

                        val accel = max(
                            (frameList[i].xMax - frameList[i].xMin) / (frameList[i + 1].xMax - frameList[i + 1].xMin),
                            (frameList[i].yMax - frameList[i].yMin) / (frameList[i + 1].yMax - frameList[i + 1].yMin)
                        )

//                        val snapsPerRound = (10 * accel).roundToInt()

//                        for (j in 0..snapsPerRound) {
//
//                        }

                    }
                }
            })
        }

        pack()
    }
}



