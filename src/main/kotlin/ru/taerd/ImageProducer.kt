package ru.taerd

import ru.smak.gui.graphics.FractalPainter
import ru.smak.gui.graphics.colorScheme5
import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.math.Complex
import ru.smak.math.fractals.Mandelbrot
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.abs

class ImageProducer(
    private val index: Int,
    private val producersCount: Int,
    private val queue: LinkedBlockingQueue<BufferedImage>,
    private val WIDTH: Int,
    private val HEIGHT: Int,
    frames: MutableList<CartesianScreenPlane>,
    private val snapsCount: Int,
    private val fractalTest: ((Complex)->Float)?,
    private val colorScheme: ((Float)->(Color))

    ) : Runnable {

    private val frameList = frames.subList(0, frames.size)
    private var disable = true
    private val fractalPainter: FractalPainter = FractalPainter(
        CartesianScreenPlane(
            WIDTH,
            HEIGHT, -2.0, 1.0, -1.0, 1.0
        )
    )


    private fun createImages(snapsCount: Int) {
        fractalPainter.isInSet = fractalTest
        fractalPainter.getColor = colorScheme
        fractalPainter.plane.apply {
            realHeight = HEIGHT
            realWidth = WIDTH
        }

        for (i in 0 until frameList.size - 1) {


            val xMinDt = abs(frameList[i].xMin - frameList[i + 1].xMin) / snapsCount
            val xMaxDt = abs(frameList[i].xMax - frameList[i + 1].xMax) / snapsCount
            val yMinDt = abs(frameList[i].yMin - frameList[i + 1].yMin) / snapsCount
            val yMaxDt = abs(frameList[i].yMax - frameList[i + 1].yMax) / snapsCount


            for (j in 0 until snapsCount / producersCount) {
                if (disable) return
                fractalPainter.plane.apply {
                    xMin = frameList[i].xMin + xMinDt * j * (producersCount) + xMinDt * index
                    xMax = frameList[i].xMax - xMaxDt * j * (producersCount) - xMaxDt * index
                    yMin = frameList[i].yMin + yMinDt * j * (producersCount) + yMinDt * index
                    yMax = frameList[i].yMax - yMaxDt * j * (producersCount) - yMaxDt * index
                }

                println("[Producer №${index}] Добавление в очередь")
                synchronized(fractalPainter) {
                    val image = fractalPainter.getImage()
                    queue.put(image)
                }


            }

        }
        println("[Producer №${index}] Image Generating finished!")
    }

    fun disable() {
        disable = true
        println("[Producer №${index}] Поставщик остановлен")
    }

    override fun run() {
        disable = false
        println("[Producer №${index}] Поставщик запущен")
        createImages(snapsCount)
    }
}