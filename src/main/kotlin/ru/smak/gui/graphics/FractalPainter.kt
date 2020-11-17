package ru.smak.gui.graphics

import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.gui.graphics.convertation.Converter
import ru.smak.math.Complex
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.math.max

class FractalPainter(
    val plane: CartesianScreenPlane
    ) : Painter {

    var fractalTest : ((Complex)->Float)? = null
    var getColor: ((Float)->Color) = { x -> Color(x, x, x)}

    private var threadList: List<Pair<Thread, BufferedImage>>? = null
    //private var stop = true
    /**
     * Рисование фрактала
     * @param g графический контекст для рисования
     */
    override fun paint(g: Graphics?) {
        val ms1 = System.currentTimeMillis()
        if (fractalTest==null || g==null) return
        val threadCount = Runtime.getRuntime().availableProcessors()
        val stripWidth = plane.width / threadCount
        var stop = true
        println("Want to stop")
        threadList?.apply {
            //forEach { it.first.interrupt() }
            forEach { it.first.join() }
        }
        stop = false
        println("Stopped and starting new...")
        threadList = List<Pair<Thread, BufferedImage>>(threadCount) {
            val b = it * stripWidth
            val e = (it + 1) * stripWidth +
                    if (it == threadCount-1) plane.width % threadCount else 0
            val bi = BufferedImage(e - b + 1, plane.height, BufferedImage.TYPE_INT_RGB)
            val bg = bi.graphics
            Pair(thread {
                for (i in 0..e - b) {
                    //sleep(10)
                    for (j in 0..plane.height) {
                        fillPixel(bg, i, j, i + b, j)
                        if (stop) {
                            println("Exiting thread")
                            return@thread
                        }
                    }
                }
            }, bi)
        }.apply {
            forEachIndexed { index, pair ->
                if (stop) {
                    println("Exiting applying")
                    return@apply
                }
                pair.first.join()
                synchronized(g) {
                    g.drawImage(pair.second, index * stripWidth, 0, null)
                }
            }
        }
        val ms2 = System.currentTimeMillis()
        println((ms2 - ms1)/1000.0)
    }

    private fun fillPixel(g: Graphics, i: Int, j: Int, xPos: Int, yPos: Int) {
        val r = fractalTest?.invoke(
                Complex(
                        Converter.xScr2Crt(xPos, plane),
                        Converter.yScr2Crt(yPos, plane)
                )
        ) ?: return
        val c = if (r eq 1F) Color.BLACK else getColor(r)
        g.color = c
        g.fillRect(i, j, 1, 1)
    }
}

private infix fun Float.eq(other: Float) =
        abs(this - other) < max(Math.ulp(this), Math.ulp(other)) * 2
private infix fun Float.neq(other: Float) =
        abs(this - other) > max(Math.ulp(this), Math.ulp(other)) * 2