package ru.smak.math.fractals

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
    var maxIters = 200
    set(value) {
        //Проверяем устанавливаемое значение на корректность
        field = max(200, abs(value))
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