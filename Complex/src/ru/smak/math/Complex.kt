package ru.smak.math

import kotlin.math.absoluteValue

class Complex(var re: Double, var im: Double) {

    constructor(): this(0.0, 0.0)

    operator fun plus(c: Complex)  =
        Complex(re+c.re, im+c.im)
    operator fun minus(c: Complex) =
        Complex(re-c.re, im-c.im)
    operator fun times(c: Complex) =
        Complex(re*c.re - im*c.im, re*c.im+im*c.re)
    operator fun div(c: Complex): Complex{
        val zn = c.re*c.re + c.im*c.im
        val r = (re*c.re + im*c.im)/zn
        val i = (im*c.re - re*c.im)/zn
        return Complex(r, i)
    }

    infix fun pow(p: Int): Complex {
        if (p==0) return Complex(1.0, 0.0)
        var r = Complex(1.0, 0.0)
        repeat(p.absoluteValue){
            r *= this
        }
        if (p<0){
            r = Complex(1.0, 0.0) / r
        }
        return r
    }

    fun abs() =  Math.sqrt(re*re+im*im)

    fun abs2() = re*re+im*im
}
