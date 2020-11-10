package ru.smak.gui.graphics

import java.awt.Color
import kotlin.math.*

fun colorScheme1(x: Float): Color {
    val r = cos(2*sin(3*x))*cos(2*sin(3*x))
    val g = abs(cos(10*x))
    val b = 2F/3F*(1F - abs(cos(5*x)))
    return Color(r, g, b)
}

fun colorScheme2(x: Float): Color {
    val r = sin(2*sin(3*x))*sin(2*sin(3*x))
    val g = abs(sin(2*sin(3*x))*cos(2*sin(3*x)))
    val b = (abs(cos(5*x)))
    return Color(r, g, b)
}

fun colorScheme3(x: Float): Color {
    val r = 1F-abs(sin(6*sin(15*x))*sin(2*sin(28*x)))
    val b = log2(1F+abs(sin(2*sin(3*x))*cos(2*sin(3*x))))
    val g = 1F-abs(cos(12+6*sin(15*x))*cos(13+2*sin(28*x)))
    return Color(r, g, b)
}

fun colorScheme4(x: Float): Color {
    val r = 1F-abs(sin(17+6*sin(15*x))*sin(7+2*sin(28*x)))
    val b = log2(1F+abs(sin(2*sin(3*x))*cos(2*sin(3*x))))
    val g = 1F-abs(cos(12+6*sin(15*x))*cos(13+2*sin(28*x)))
    return Color(r, g, b)
}