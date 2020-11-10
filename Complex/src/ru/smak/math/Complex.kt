package ru.smak.math

import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Класс для работы с комплексными числами
 */
class Complex(var re: Double, var im: Double) {

    /**
     * Вторичный конструктор для создания нулевого комплекного числа
     */
    constructor(): this(0.0, 0.0)

    /**
     * Вторичный конструктор для создания комплексного числа с ненулевой действительной частью
     */
    constructor(re: Double): this(re, 0.0)

    /**
     * Оператор сложения комплексных чисел
     * @param other второе комплексное число
     * @return  новое комплексное число
     */
    operator fun plus(other: Complex)  =
        Complex(re+other.re, im+other.im)

    /**
     * Оператор сложения с присвоением прибавляет к данному комплексному число второе
     * @param other прибавляемое комплексное число
     */
    operator fun plusAssign(other: Complex){
        re += other.re
        im += other.im
    }

    /**
     * Оператор вычитания для комплексных чисел
     * @param other вычитаемое комплексное число
     * @return новое комплексное число, равное разности данного числа и вычитаемого
     */
    operator fun minus(other: Complex) =
        Complex(re-other.re, im-other.im)

    /**
     * Оператор вычитания с присвоением вычитает из данного комплексного числа второе
     * @param other вычитаемое комплексное число
     */
    operator fun minusAssign(other: Complex){
        re -= other.re
        im -= other.im
    }

    /**
     * Оператор умножения
     * @param other второе комплексное число для умножения
     * @return комплексное число, представляющее собой произведение двух исходных комплекксных чисел
     */
    operator fun times(other: Complex) =
        Complex(re*other.re - im*other.im, re*other.im+im*other.re)

    /**
     * Оператор умножения с присвоением выполняет умножение данного комплексного числа на второе
     * @param other второе комлпексное число для умножения
     */
    operator fun timesAssign(other: Complex){
        // Сначала новая вещественная часть сохраняется во временной переменной,
        // чтобы не повлиять на вычисление мнимой части
        val t_re = re * other.re - im * other.im
        im = re * other.im + im * other.re
        // сохранение вещественной части в поле
        re = t_re
    }

    /**
     * Оператор деления комплексных чисел
     * @param other комплексное число - делитель
     * @return частное от деления делимого числа на делитель
     */
    operator fun div(other: Complex): Complex{
        //Вычисление знаменателя
        val zn = other.re * other.re + other.im * other.im

        val r = (re * other.re + im * other.im) / zn
        val i = (im * other.re - re * other.im) / zn

        return Complex(r, i)
    }

    /**
     * Оператор деления с присвоением делит данное комплексное число (делимое) на делитель
     * @param other комплексноее число - делитель
     */
    operator fun divAssign(other: Complex){
        // Вычисление знаменателя
        val zn = other.re * other.re + other.im * other.im
        // Вещественная часть сохраняется сначала во временной перменной,
        // чтобы не повлиять на вычисление мнимой части
        val t_re = (re * other.re + im * other.im) / zn
        im = (im * other.re - re * other.im) / zn
        // сохранение вещественной части в поле
        re = t_re
    }

    /**
     * Вычисление значения 1 / z, для данного комплексного числа z
     * @return результат вычисления числа обратного для данного
     */
    operator fun not() = Complex(1.0) / this

    /**
     * Вычисление значения 1 / z, для данного комплексного числа z
     */
    fun inverse(){
        // Вычисление знаменателя
        val zn = abs2()
        re /= zn
        im /= zn
    }

    /**
     * Нахождение числа, сопряженного к данному
     * @return сопряженное комплексное число
     */
    operator fun unaryMinus() = Complex(re, -im)

    /**
     * Инфиксный оператор возведения комплексного числа в степень
     * @param p целый показатель степени
     * @return комплексное число, возведенное в целую степень
     */
    infix fun pow(p: Int): Complex {
        val res =  Complex(1.0)
        // Если степень = 0, возвращаем значение 1.0
        if (p==0) return res
        // Вспомогательная переменная для накопления степени
        val t = this
        // Вспомогательная переменная для вычисления показателя степени
        var n = p
        // Пока показатель степени отличен от 0
        while (n != 0)
            if (n % 2 == 0)
            {
                // Если показатель четный
                // Домножаем вспомогательную переменную на себя и
                t *= t
                // уменьшаем показатель степени
                n /= 2
            } else
            {
                // Если показатель нечетный
                // домножаем результат на временную переменную
                res *= t
                // Уменьшаем показатель степени на 1
                n--
            }
        // Если степень отрицательная, обращаем результат
        if (p < 0) res.inverse()
        return res
    }

    /**
     * Инфиксный оператор возведения комплексного числа в степень с присвоением
     * @param p целый показатель степени
     */
    infix fun powAssign(p: Int) {
        val t = this.clone()
        if (p == 0) this /= this
        repeat(p - 1){
            this *= t
        }
        if (p < 0) this.inverse()
    }

    /**
     * Вычисление модуля комплексного числа
     */
    fun abs()  = sqrt(re * re + im * im)

    /**
     * Вычисление квадрата модуля комплексного числа
     */
    fun abs2() = re * re + im * im

    /**
     * Строковое представление комплексного числа
     */
    override fun toString(): String {
        val s = StringBuilder()
        // Если вещественная часть не нулевая,
        // а также если мнимая - нулевая
        if (re neq 0.0 || im eq 0.0)
            // Додбавляем вещественную часть в выод
            s.append(re)
        // Если мнимая часть не нулевая
        if (im neq 0.0) {
            //Добавляем знак (+ или -)
            s.append(
                if (im < 0) " - "
                else " + "
            )
            // Если мнимая часть по модулю не 1, выводим ее в строку
            if (abs(im) neq 1.0)
                s.append("${abs(im)}")
            // Добавляем букву "i"
            s.append("i")
        }
        return s.toString()
    }

    /**
     * Получение копии комплексного числа
     * @return комплексное число с вещественной и мнимой частью,
     * совпадающей с вещественной и мнимой частями данного числа соответственно
     */
    fun clone(): Complex {
        return Complex(re, im)
    }
}

private infix fun Double.eq(other: Double) =
        abs(this - other) < max(Math.ulp(this), Math.ulp(other)) * 2
private infix fun Double.neq(other: Double) =
        abs(this - other) > max(Math.ulp(this), Math.ulp(other)) * 2
private infix fun Double.ge(other: Double) =
        this > other || this.eq(other)
private infix fun Double.le(other: Double) =
        this < other || this.eq(other)