package ru.smak.math

import kotlin.math.abs
import kotlin.math.max

infix fun Double.eq(other: Double) = abs(this - other) < max(Math.ulp(this), Math.ulp(other)) * 2
infix fun Double.neq(other: Double) = abs(this - other) > max(Math.ulp(this), Math.ulp(other)) * 2
infix fun Double.ge(other: Double) = this > other || this.eq(other)
infix fun Double.le(other: Double) = this < other || this.eq(other)