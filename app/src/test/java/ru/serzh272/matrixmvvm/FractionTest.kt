package ru.serzh272.matrixmvvm

import org.junit.Test

import org.junit.Assert.*
import ru.serzh272.matrix.Fraction
import java.lang.Exception

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FractionTest {
    private val fr1 = Fraction()
    private val fr2 = Fraction(2, 3u)
    private val fr3 = Fraction(9, 4u)
    private val fr4 = Fraction(9, 3u)
    private val fr5 = Fraction(1234, 5396u)
    private val fr6 = Fraction(25698, 389u)
    private val fr7 = Fraction(2, 3u)

    @ExperimentalUnsignedTypes
    @Test
    fun test_addition_isCorrect() {
        assertEquals(Fraction(2, 3u), fr1 + fr2)
        assertEquals(Fraction(-2, 3u), fr1 - fr2)
        assertEquals(Fraction(11, 3u), fr2 + fr4)
        assertEquals(Fraction(17, 3u), fr2 + 5)
        assertEquals(Fraction(69573217, 1049522u), fr5 + fr6)
        assertEquals(Fraction(-69093191, 1049522u), fr5 - fr6)

    }

    @ExperimentalUnsignedTypes
    @Test
    fun test_multiply_isCorrect() {
        assertEquals(Fraction(), fr1 * fr2)
        assertEquals(Fraction(3, 2u), fr2 * fr3)
        assertEquals(Fraction(27, 4u), fr3 * fr4)
        assertEquals(Fraction(9), fr3 * 4)
        assertEquals(Fraction(7927833, 524761u), fr5 * fr6)

    }

    @ExperimentalUnsignedTypes
    fun test_divide_isCorrect() {
        assertEquals(Fraction(), fr1 / fr2)
        assertEquals(Fraction(8, 27u), fr2 / fr3)
        assertEquals(Fraction(3, 4u), fr3 / fr4)
        assertEquals(Fraction(1, 4u), fr3 / 9)
        assertEquals(Fraction(240013, 69333204u), fr5 / fr6)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun test_get_mixed() {
        assertEquals("2 1/4", fr3.toString(Fraction.FractionType.MIXED))
        assertEquals("-2 1/4", (-fr3).toString(Fraction.FractionType.MIXED))
        assertEquals("3", fr4.toString(Fraction.FractionType.MIXED))
        assertEquals("65 874261/1049522", Fraction(69093191, 1049522u).toString(Fraction.FractionType.MIXED))
    }

    @ExperimentalUnsignedTypes
    @Test
    fun test_normalize() {
        assertEquals(3, fr4.normalize().numerator)
        assertEquals(1u, fr4.normalize().denominator)
        assertEquals(2, fr2.normalize().numerator)
        assertEquals(3u, fr2.normalize().denominator)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun test_init() {
        var fract1 = Fraction(35, 17u)
        assertEquals(35, fract1.numerator)
        assertEquals(17u, fract1.denominator)
        fract1 = Fraction(34, 17u)
        assertEquals(2, fract1.numerator)
        assertEquals(1u, fract1.denominator)
        fract1 = Fraction(2, 16u)
        assertEquals(1, fract1.numerator)
        assertEquals(8u, fract1.denominator)
        try {
            fract1 = Fraction(35, 0u)
        }catch (e:Exception){
            print(e.message)
        }

    }


    @ExperimentalUnsignedTypes
    @Test
    fun test_equals_fraction() {
        var fract = fr2.copy()
        assertTrue(fr1 != fr2)
        assertTrue(fr2 == Fraction(2, 3u))
        assertTrue(fr3 == Fraction(18, 8u))
        assertTrue(fr3 != Fraction(1, 4u))
        assertTrue(fr2 !== fr7)
        fract = fr2
        assertTrue(fr2 === fract)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun test_to_double() {
        assertEquals(0.67f, fr2.toDouble().toFloat(), 0f)
        assertEquals(2.3f, fr3.toDouble(1).toFloat(), 0f)
        assertEquals(0.22869f, fr5.toDouble(5).toFloat(), 0f)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun test_set_from_string() {
        assertEquals(Fraction(5, 3u), Fraction("5/3"))
        assertEquals(Fraction(53, 10u), Fraction("5.3"))
        assertEquals(Fraction(106, 20u), Fraction("5,3"))
        assertEquals(Fraction(-1, 2u), Fraction("-0.5"))
        assertEquals(Fraction(-7, 2u), Fraction("-3.5"))
    }

    @ExperimentalUnsignedTypes
    @Test
    fun test_check_cast() {
        var vls = fr3.getValues()
        assertEquals(2, vls.first)
        assertEquals(1, vls.second)
        assertEquals(4u, vls.third)
        vls = fr2.getValues()
        assertEquals(0, vls.first)
        assertEquals(2, vls.second)
        assertEquals(3u, vls.third)
    }



}