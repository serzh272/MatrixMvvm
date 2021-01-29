package ru.serzh272.matrix

import java.lang.Exception
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

class Fraction {
    var numerator: Int = 0

    @ExperimentalUnsignedTypes
    var denominator: UInt = 1u

    @ExperimentalUnsignedTypes
    constructor() {
        this.numerator = 0
        this.denominator = 1u
    }

    @ExperimentalUnsignedTypes
    constructor(n: Int, d: UInt) {
        if (d.toInt() == 0) throw Exception("Denominator must be not 0")
        this.numerator = n
        this.denominator = d
        this.normalize()
    }

    @ExperimentalUnsignedTypes
    constructor(n: Int) {
        this.numerator = n
        this.denominator = 1u
        this.normalize()
    }

    @ExperimentalUnsignedTypes
    constructor(strFr: String) {
        when {
            strFr.contains('/') -> {
                val frList: List<String> = strFr.split('/')
                val sNumerator = frList[0]
                val sDenominator = frList[1]
                if (sDenominator != "") {
                    this.numerator = sNumerator.toInt()
                    this.denominator = sDenominator.toUInt()
                }
            }
            strFr.contains(',') or strFr.contains('.') -> {
                val l = strFr.split(Regex("[,/.]"))
                val i = l[0].toIntOrNull()
                val dec = l[1].toIntOrNull()
                val precision = l[1].length
                this.denominator = 10.0.pow(precision).toUInt()
                this.numerator = (dec ?: 0) + (i ?: 0) * this.denominator.toInt()
            }
            strFr == "" -> {
                this.numerator = 0
                this.denominator = 1u
            }
            else -> {
                this.numerator = strFr.toInt()
                this.denominator = 1u
            }
        }
        this.normalize()
    }

    @ExperimentalUnsignedTypes
    fun toString(type:FractionType):String{
        return when(type){
            FractionType.COMMON -> this.toString()
            FractionType.MIXED -> {
                if (abs(this.numerator) > this.denominator.toInt()) {
                    val n = abs(this.numerator) % this.denominator.toInt()
                    val d = this.denominator
                    val i = this.numerator / this.denominator.toInt()
                    "$i${if ( n == 0) "" else " $n/$d"}"
                }else{
                    this.toString()
                }
            }
        }
    }


    @ExperimentalUnsignedTypes
    fun normalize(): Fraction {
        if (this.numerator == 0) {
            this.denominator = 1u
        } else {
            val nd: Int = nod(this.numerator, this.denominator.toInt())
            this.numerator /= nd
            this.denominator /= nd.toUInt()
        }
        return this
    }

    private fun nod(a: Int, b: Int): Int {
        var an: Int = abs(a)
        var bn: Int = abs(b)
        while ((an != 0) and (bn != 0)) {
            if (an > bn) {
                an %= bn
            } else {
                bn %= an
            }
        }
        return an + bn
    }

    private fun nok(a: Int, b: Int): Int {
        var an: Int = abs(a)
        val bn: Int = abs(b)
        an /= nod(a, b)
        return an * bn
    }

    @ExperimentalUnsignedTypes
    operator fun unaryMinus(): Fraction {
        return Fraction(
            -this.numerator,
            this.denominator
        )
    }

    @ExperimentalUnsignedTypes
    operator fun plus(fr: Fraction): Fraction {
        val nk: Int = nok(this.denominator.toInt(), fr.denominator.toInt())
        val n1: Int = this.numerator * (nk / this.denominator.toInt())
        val n2: Int = fr.numerator * (nk / fr.denominator.toInt())
        return Fraction(n1 + n2, nk.toUInt())
    }

    @ExperimentalUnsignedTypes
    operator fun plus(n: Int): Fraction {
        return Fraction(
            this.numerator + n * this.denominator.toInt(),
            this.denominator
        )
    }


    @ExperimentalUnsignedTypes
    operator fun minus(fr: Fraction): Fraction {
        return this + (-fr)
    }

    operator fun minus(n: Int): Fraction {
        return this - n
    }

    @ExperimentalUnsignedTypes
    operator fun times(fr: Fraction): Fraction {
        val i = Fraction(this.numerator, this.denominator)
        val j = Fraction(fr.numerator, fr.denominator)
        val l: Int = j.numerator
        j.numerator = i.numerator
        i.numerator = l
        j.normalize()
        i.normalize()

        return Fraction(
            i.numerator * j.numerator,
            i.denominator * j.denominator
        )
    }

    @ExperimentalUnsignedTypes
    operator fun times(n: Int): Fraction {
        return this * Fraction(n, 1u)
    }

    @ExperimentalUnsignedTypes
    fun invert(): Fraction {
        val rez = Fraction(
            this.numerator,
            this.denominator
        )
        if (rez.numerator != 0) {
            val n: Int = rez.denominator.toInt()
            if (rez.numerator > 0) {
                rez.denominator = rez.numerator.toUInt()
                rez.numerator = n
            } else {
                rez.denominator = (-rez.numerator).toUInt()
                rez.numerator = -n
            }
        }
        return rez
    }

    @ExperimentalUnsignedTypes
    operator fun div(fr: Fraction): Fraction {
        return this * (fr.invert())
    }

    @ExperimentalUnsignedTypes
    operator fun div(n: Int): Fraction {
        return this / Fraction(n, 1u)
    }

    @ExperimentalUnsignedTypes
    operator fun compareTo(fr: Fraction): Int {
        val f: Fraction = this - fr
        if (f.numerator > 0) {
            return 1
        } else if (f.numerator == 0) {
            return 0
        }
        return -1
    }

    @ExperimentalUnsignedTypes
    fun abs(): Fraction {
        val res: Fraction = this.copy()
        if (this.numerator < 0) {
            return -res
        }
        return res
    }

    @ExperimentalUnsignedTypes
    fun power(p: Int): Fraction {
        return Fraction(
            this.numerator.toDouble().pow(p).toInt(), this.denominator.toDouble().pow(p).toUInt()
        )
    }

    @ExperimentalUnsignedTypes
    fun copy(): Fraction {
        return Fraction(
            this.numerator,
            this.denominator
        )
    }

    @ExperimentalUnsignedTypes
    fun toDouble(precision: Int = 2): Double {
        val str = String.format(
            Locale("en"),
            "%.${precision}f",
            this.numerator.toDouble() / this.denominator.toInt()
        )
        return str.toDouble()
    }

    /*operator fun minusAssign(fr: Fraction) {
        val res: Fraction = this - fr
        this.numerator = res.numerator
        this.denominator = res.denominator
    }*/

    @ExperimentalUnsignedTypes
    override fun equals(other: Any?): Boolean {
        val cmp1 = this.copy()
        cmp1.normalize()
        return when (other) {
            is Fraction -> {
                val cmp2 = other.copy()
                cmp2.normalize()
                (cmp1.numerator == cmp2.numerator) and (cmp1.denominator == cmp2.denominator)
            }
            is Int -> {
                (cmp1.numerator == other) and (cmp1.denominator.toInt() == 1)
            }
            else -> false
        }
    }

    @ExperimentalUnsignedTypes
    fun setFromString(str: String) {
        when {
            str.contains('/') -> {
                val frList: List<String> = str.split('/')
                if (frList.size == 2) {
                    val sNumerator = frList[0]
                    val sDenominator = frList[1]
                    if (sDenominator != "") {
                        numerator = sNumerator.toInt()
                        denominator = sDenominator.toUInt()
                    }
                }
            }
            str.contains(',') or str.contains('.') -> {

            }
            str == "" -> {
                numerator = 0
                denominator = 1u
            }
            else -> {
                numerator = str.toInt()
                denominator = 1u
            }
        }
    }

    @ExperimentalUnsignedTypes
    override fun toString(): String {
        return if (this.denominator.toInt() != 1)
            "${this.numerator}/${this.denominator}"
        else
            "${this.numerator}"
    }

    @ExperimentalUnsignedTypes
    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
    enum class FractionType{
        COMMON,
        MIXED
    }

    /**
     * getValues function returns Triple of integer part of fraction, numerator, denominator
     */
    @ExperimentalUnsignedTypes
    fun getValues():Triple<Int, Int, UInt>{
        return Triple(this.numerator/this.denominator.toInt(), this.numerator % this.denominator.toInt(), this.denominator)
    }
}

