package ru.smak

import ru.smak.gui.MainWindow
import ru.taerd.gui.VideoWindow

fun main() {
    MainWindow().apply { isVisible = true }
    VideoWindow().apply{ isVisible=true }
}