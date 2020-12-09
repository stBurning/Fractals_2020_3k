package ru.taerd.panels

import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import java.awt.Color
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.EtchedBorder

class ControlPanel: JPanel() {
    private val addFrameButton = JButton("Добавить")
    private val startButton = JButton("Создать")
    private var planeScroll = JScrollPane()
    private var arrayFrame : Array<CartesianScreenPlane>?=null
    private var planeList : JList<CartesianScreenPlane>?=null

    private val startButtonListeners : MutableList<() -> Unit> = mutableListOf()
    fun addStartButtonListener(l: () -> Unit){
        startButtonListeners.add(l)
    }

    private val addFrameButtonClickListener : MutableList<() -> Unit> = mutableListOf()
    fun addButtonAddFrameListener(l: () -> (Unit)){
        addFrameButtonClickListener.add(l)
    }
    fun removeButtonAddFrameListener(l: () -> (Unit)){
        addFrameButtonClickListener.remove(l)
    }

    private val getFrameListener : MutableList<() -> Unit> = mutableListOf()
    fun addGetFrameListener(l: () -> (Unit)){
        getFrameListener.add(l)
    }
    fun removeGetFrameListener(l: () -> (Unit)){
        getFrameListener.remove(l)
    }

    init{
        border= EtchedBorder()
        background = Color.WHITE
        planeScroll.setLocation(5,5)
        add(addFrameButton)
        add(startButton)
        add(planeScroll)


        with(addFrameButton) {
            //Событие возникающее при нажатии на добавление фрейма
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    addFrameButtonClickListener.forEach { l -> l() }
                    getFrameListener.forEach{l->l()}
                    repaint()
                }
            })
            with(startButton) {
                //Событие возникающее при нажатии на создание видео
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        super.mouseClicked(e)
                        startButtonListeners.forEach{ l -> l()}

                    }
                })
            }
        }
    }
    fun createPlaneList(frameList: List<CartesianScreenPlane>){
        arrayFrame=Array(frameList.size) {
            frameList[it]
        }
        planeList = JList(arrayFrame)
        planeScroll= JScrollPane(planeList)
        planeScroll.isVisible = true

    }
}
