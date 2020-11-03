package ru.smak.math.fractals

import ru.smak.math.Complex
import kotlin.math.abs
import kotlin.math.max

class Mandelbrot {

    private var r2: Double = 4.0

    //Количество итераций, в течение которых проверяется
    //принадлежность точки множеству
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
    fun isInSet(c: Complex): Boolean{
        val z = Complex()
        for (i in 1..maxIters){
            z powAssign 2
            z += c
            if (z.abs2() > r2) return false
        }
        return true
    }
}