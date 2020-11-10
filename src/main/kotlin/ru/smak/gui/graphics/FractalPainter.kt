package ru.smak.gui.graphics

import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.gui.graphics.convertation.Converter
import ru.smak.math.Complex
import ru.smak.math.fractals.Mandelbrot
import java.awt.Color
import java.awt.Graphics

class FractalPainter(
    val plane: CartesianScreenPlane
    ) : Painter {

    var fractalTest : ((Complex)->Boolean)? = null

    /**
     * Рисование фрактала
     * @param g графический контекст для рисования
     */
    override fun paint(g: Graphics?) {
        //val ms1 = System.currentTimeMillis()
        if (fractalTest==null || g==null) return
        for (i in 0..plane.width){
            for (j in 0..plane.height){
                val r = fractalTest?.invoke(
                    Complex(
                        Converter.xScr2Crt(i, plane),
                        Converter.yScr2Crt(j, plane)
                    )
                ) ?: return
                g.color = if (r) Color.BLACK else Color.WHITE
                g.fillRect(i, j, 1, 1)
            }
        }
        //val ms2 = System.currentTimeMillis()
        //println((ms2 - ms1)/1000.0)
    }
}