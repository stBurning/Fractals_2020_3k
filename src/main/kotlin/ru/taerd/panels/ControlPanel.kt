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
    set(value){
        field=value
    }

    private val addFrameButtonClickListener : MutableList<() -> Unit> = mutableListOf()
    public fun addButtonAddFrameListener(l: () -> (Unit)){
        addFrameButtonClickListener.add(l)
    }
    public fun removeButtonAddFrameListener(l: () -> (Unit)){
        addFrameButtonClickListener.remove(l)
    }

    private val getFrameListener : MutableList<() -> Unit> = mutableListOf()
    public fun addGetFrameListener(l: () -> (Unit)){
        getFrameListener.add(l)
    }
    public fun removeGetFrameListener(l: () -> (Unit)){
        getFrameListener.remove(l)
    }

    init{
        border= EtchedBorder()
        background = Color.WHITE
        add(planeScroll)
        //planeScroll не выводится на экран
        planeScroll.setLocation(5,5)
        //planeScroll.isVisible=true
        add(addFrameButton)
        add(startButton)



        with(addFrameButton) {
            //Событие возникающее при нажатии на добавление фрейма
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    text="pressed"
                    super.mouseClicked(e)
                    addFrameButtonClickListener.forEach { l -> l() }
                    getFrameListener.forEach{l->l()}
                }
            })
            with(startButton) {
                //Событие возникающее при нажатии на создание видео
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        text="started"
                        super.mouseClicked(e)
                    }
                })
            }
        }
    }
    public fun createPlaneList(frameList: List<CartesianScreenPlane>){
        arrayFrame=Array<CartesianScreenPlane>(frameList.size) { it ->
            frameList[it]
        }
        planeList = JList(arrayFrame)
        planeScroll= JScrollPane(planeList)

    }
}
