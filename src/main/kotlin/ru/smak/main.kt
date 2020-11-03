package ru.smak

import ru.smak.gui.MainWindow
import ru.smak.math.Complex

fun main() {
    //val w = MainWindow()
    //w.isVisible = true
    val x = Complex(2.0, 1.0)
    x powAssign 3
    println(x)
}