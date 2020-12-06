package ru.taerd.gui

import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.taerd.panels.ControlPanel
import ru.taerd.panels.VideoPanel
import java.awt.Dimension
import javax.swing.GroupLayout
import javax.swing.JFrame


class VideoWindow : JFrame() {
    private val minSizeVidePanel = Dimension(300, 300)
    private val minSizePrePanel = Dimension(100,300)
    private val videoPanel : VideoPanel
    private val prePanel : ControlPanel
    private val frameList = mutableListOf<CartesianScreenPlane>()
    init{
        //сделать подписку на событие defaultCloseOperation которое будет блокировать потоки отрисовки изображения и закрыввтаь окошко
        defaultCloseOperation.apply{
            isVisible=false
        }
        title = "Составление видео из кадров"
        minimumSize= Dimension(500,400)
        videoPanel = VideoPanel()
        prePanel = ControlPanel()

        layout = GroupLayout(contentPane).apply{
            setVerticalGroup(createSequentialGroup()
                    .addGap(4)
                    .addGroup(
                            createParallelGroup()
                                    .addComponent(videoPanel,minSizeVidePanel.height,minSizeVidePanel.height, GroupLayout.DEFAULT_SIZE)
                                    .addComponent(prePanel,minSizePrePanel.height,minSizePrePanel.height, GroupLayout.DEFAULT_SIZE)
                    )
                    .addGap(4)
            )
            setHorizontalGroup(createSequentialGroup()
                    .addGap(4)
                    .addComponent(videoPanel,minSizeVidePanel.width,minSizeVidePanel.width, GroupLayout.DEFAULT_SIZE)
                    .addGap(4)
                    .addComponent(prePanel,minSizePrePanel.width,minSizePrePanel.width, GroupLayout.DEFAULT_SIZE)
                    .addGap(4)
            )
        }
        pack()

        prePanel.addButtonAddFrameListener {
            frameList.add(videoPanel.plane)
        }
        prePanel.addGetFrameListener {
            prePanel.createPlaneList(frameList)
        }
    }

}