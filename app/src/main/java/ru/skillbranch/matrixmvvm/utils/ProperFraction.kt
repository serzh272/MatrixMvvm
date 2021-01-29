package ru.skillbranch.matrixmvvm.utils

import ru.serzh272.matrix.Fraction
import kotlin.math.abs

class ProperFraction @ExperimentalUnsignedTypes constructor(
    override var numerator:Int,
    override var denominator:UInt,
    var integ:Int = 0
):Fraction(numerator+integ*denominator.toInt(),denominator) {
    @ExperimentalUnsignedTypes
    fun toFraction():Fraction{
        return Fraction(numerator+integ*denominator.toInt(), denominator)
    }

}