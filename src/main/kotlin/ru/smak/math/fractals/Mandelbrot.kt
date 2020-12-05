package ru.smak.math.fractals

import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.math.Complex
import kotlin.math.*

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
    var maxIters = 35
        //30  8
        // 35 10
        set(value) {
            //Проверяем устанавливаемое значение на корректность
            field = max(maxIters, abs(value))
        }

    /**
     * метод для динамического изменения  кол-во итераций который изменеться относительно площади окошка
     */

    fun changeMaxItrs(new: CartesianScreenPlane, old: CartesianScreenPlane): Int {
        val areaOfNew=(Math.abs(new.xMax-new.xMin))*(Math.abs(new.yMax-new.yMin))
        val areaOfOld = (Math.abs(old.xMax-old.xMin))*(Math.abs(old.yMax-old.yMin))
        maxIters=(maxIters*areaOfOld/areaOfNew/10).toInt()
        return maxIters
    }


    /**
     * Метод определения принадлежности точки множеству Мандельброта
     * @param c точка комплексной плоскости
     * @return true, если точка принадлежит множеству (при заданном значении maxIter)
     * false - в противном случае
     */
    fun isInSet(c: Complex): Float {
        //var z = Complex()
        val z = Complex()
        for (i in 1..maxIters){
            z powAssign 2
            z += c
            if (z.abs2() > r2)
                //return i.toFloat() - log2(log2(z.abs2())).toFloat()+4.0F
                return i.toFloat() -
                        log(log(z.abs(), E)/log(maxIters.toDouble(),E), E).toFloat()/
                        log(2.0, E).toFloat()
                //i.toFloat()/maxIters.toFloat()
        }
        return 0F
    }
}