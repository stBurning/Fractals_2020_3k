package ru.smak

import ru.smak.gui.MainWindow
import ru.taerd.gui.VideoWindow

fun main() {
    val v = VideoWindow().apply { isVisible = false }
    MainWindow(v).apply { isVisible = true }
}