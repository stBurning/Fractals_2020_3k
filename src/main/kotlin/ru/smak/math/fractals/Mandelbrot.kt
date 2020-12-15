package ru.smak.math.fractals

import ru.smak.gui.graphics.Coords
import ru.smak.math.Complex
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.max

/**
 * Класс множества Мандельброта
 */
class Mandelbrot {

    /**
     * r^2 для проверки принадлежности точки множеству
     */
    private var r2: Double = 4.0

    /**
     * Количество итераций, в течение которых проверяется
     * принадлежность точки множеству
     */
    var maxIters = 100
        private set(value) {
            //Проверяем устанавливаемое значение на корректность
            field = max(maxIters, abs(value))
        }

    /**
     * Метод обновления максимального количества итераций
     * @param new Новые значения границ плоскости
     * @param old Старые значения границ плоскости
     * @return обновленное количество итераций
     */

    fun updateMaxIterations(new: Coords, old: Coords): Int {
        val differenceX = abs(old.xMax - old.xMin) - abs(new.xMax - new.xMin)
        val differenceY = abs(old.yMax - old.yMin) - abs(new.yMax - new.yMin)
        maxIters += (differenceX * 300).toInt() + (differenceY * 300).toInt()
        return maxIters
    }


    /**
     * Метод определения принадлежности точки множеству Мандельброта
     * @param c точка комплексной плоскости
     * @return true, если точка принадлежит множеству (при заданном значении maxIter)
     * false - в противном случае
     */
    fun isInSet(c: Complex): Float {

        val z = Complex()
        for (i in 1..maxIters) {
            z powAssign 2
            z += c
            if (z.abs2() > r2)
            //return i.toFloat() - log2(log2(z.abs2())).toFloat()+4.0F
                return i.toFloat() -
                        log(log(z.abs(), E) / log(maxIters.toDouble(), E), E).toFloat() /
                        log(2.0, E).toFloat()
            //i.toFloat()/maxIters.toFloat()
        }
        return 0F
    }

    fun isInSet1(c: Complex): Float {

        val z = Complex()
        for (i in 1..maxIters) {
            z powAssign 3
            z += c
            if (z.abs2() > r2)
            //return i.toFloat() - log2(log2(z.abs2())).toFloat()+4.0F
                return i.toFloat() -
                        log(log(z.abs(), E) / log(maxIters.toDouble(), E), E).toFloat() /
                        log(2.0, E).toFloat()
            //i.toFloat()/maxIters.toFloat()
        }
        return 0F
    }
}