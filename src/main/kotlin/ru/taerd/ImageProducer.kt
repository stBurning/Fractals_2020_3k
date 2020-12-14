package ru.taerd

import ru.smak.gui.graphics.FractalPainter
import ru.smak.gui.graphics.colorScheme5
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.math.fractals.Mandelbrot
import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.abs

class ImageProducer(
    private val i: Int,
    private val queue: LinkedBlockingQueue<BufferedImage>,
    private val WIDTH: Int,
    private val HEIGHT: Int,
    private val frameList: MutableList<CartesianScreenPlane>,
    private val snapsCount: Int,

    ) : Runnable {

    private var disable = true
    private val fractal: Mandelbrot = Mandelbrot()
    private val fractalPainter: FractalPainter = FractalPainter(
        CartesianScreenPlane(
            WIDTH,
            HEIGHT, -2.0, 1.0, -1.0, 1.0
        )
    )


    private fun createImages(snapsCount: Int) {
        fractalPainter.isInSet = fractal::isInSet
        fractalPainter.getColor = ::colorScheme5
        fractalPainter.plane.apply {
            realHeight = HEIGHT
            realWidth = WIDTH
        }

        for (i in 0 until frameList.size - 1) {

            val xMinDt = abs(frameList[i].xMin - frameList[i + 1].xMin) / snapsCount
            val xMaxDt = abs(frameList[i].xMax - frameList[i + 1].xMax) / snapsCount
            val yMinDt = abs(frameList[i].yMin - frameList[i + 1].yMin) / snapsCount
            val yMaxDt = abs(frameList[i].yMax - frameList[i + 1].yMax) / snapsCount


            for (j in 0..snapsCount) {
                if (disable) return
                fractalPainter.plane.apply {
                    xMin = frameList[i].xMin + xMinDt * j
                    xMax = frameList[i].xMax - xMaxDt * j
                    yMin = frameList[i].yMin + yMinDt * j
                    yMax = frameList[i].yMax - yMaxDt * j
                }

                println("Добавление в очередь")
                synchronized(fractalPainter) {
                    val image = fractalPainter.getImage()
                    queue.put(image)
                }



            }

        }
        println("Image Generating finished!")
    }

    fun disable() {
        disable = true
        println("Поставщик остановлен")
    }

    override fun run() {
        disable = false
        println("Поставщик запущен")
        createImages(snapsCount)
    }
}