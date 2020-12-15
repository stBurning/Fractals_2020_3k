package ru.smak

import ru.smak.gui.MainWindow
import ru.taerd.gui.VideoWindow
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame


fun main() {
    val v = VideoWindow().apply { isVisible = false }
    v.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
    v.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(e: WindowEvent) {
            v.close()
        }
    })
    MainWindow(v).apply { isVisible = true }
}