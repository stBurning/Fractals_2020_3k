package ru.taerd.panels

import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.border.EtchedBorder

class ControlPanel: JPanel() {
    private val addButton = JButton("Добавить")
    private val startButton = JButton("Создать")
    init{
        border= EtchedBorder()
        background = Color.WHITE
        add(addButton)
        add(startButton)


        with(addButton){
            //Событие возникающее при нажатии на добавление фрейма
            addMouseListener(object: MouseAdapter(){
                override fun mousePressed(e: MouseEvent?) {
                    text="Pressed"
                }
            })
        }
        with(startButton){
            //Событие возникающее при нажатии на создание видео
            addMouseListener(object: MouseAdapter(){
                override fun mousePressed(e: MouseEvent?) {
                    text="Create"
                }
            })
        }

    }
}