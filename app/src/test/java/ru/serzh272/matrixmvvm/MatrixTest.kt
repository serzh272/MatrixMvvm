package ru.serzh272.matrixmvvm

import org.junit.Test

import org.junit.Assert.*
import ru.serzh272.matrix.Fraction
import ru.serzh272.matrixmvvm.utils.Matrix

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalUnsignedTypes
class MatrixTest {
    private val m1 = Matrix()
    private val m2 = Matrix(4, 3)
    private val m3 = Matrix("{1, 2, 3}," +
                                  "{4, 5.6, 6}," +
                                  "{8 ,6.8, 9}")
    private val m4 = Matrix("{1/8, 2/4, 3}," +
                                  "{4/13, 5/7, 6}," +
                                  "{8 ,6, 9}")

    @Test
    fun test_addition_isCorrect() {
        var mrez = m3 + m4
        assertEquals(Fraction(9,8u), mrez[0,0])
        assertEquals(Fraction(5, 2u), mrez[0,1])
        assertEquals(Fraction(6), mrez[0,2])
        assertEquals(Fraction(56,13u), mrez[1,0])
        assertEquals(Fraction(221,35u), mrez[1,1])
        assertEquals(Fraction(12), mrez[1,2])
        assertEquals(Fraction(16), mrez[2,0])
        assertEquals(Fraction(64,5u), mrez[2,1])
        assertEquals(Fraction(18), mrez[2,2])
        mrez = m3 - m4
        assertEquals(Fraction(7,8u), mrez[0,0])
        assertEquals(Fraction(3, 2u), mrez[0,1])
        assertEquals(Fraction(), mrez[0,2])
        assertEquals(Fraction(48,13u), mrez[1,0])
        assertEquals(Fraction(171,35u), mrez[1,1])
        assertEquals(Fraction(), mrez[1,2])
        assertEquals(Fraction(), mrez[2,0])
        assertEquals(Fraction(4,5u), mrez[2,1])
        assertEquals(Fraction(), mrez[2,2])
    }

    @Test
    fun test_multiply_isCorrect() {
        val mrez = m3 * m4
        assertEquals(Fraction(2573, 104u), mrez[0, 0])
        assertEquals(Fraction(279, 14u), mrez[0, 1])
        assertEquals(Fraction(42), mrez[0, 2])
        assertEquals(Fraction(6529, 130u), mrez[1, 0])
        assertEquals(Fraction(42), mrez[1, 1])
        assertEquals(Fraction(498,5u), mrez[1, 2])
        assertEquals(Fraction(4881,65u), mrez[2, 0])
        assertEquals(Fraction(440, 7u), mrez[2, 1])
        assertEquals(Fraction(729,5u), mrez[2, 2])
    }
    @Test
    fun test_transform_isCorrect() {
        val strM =  "{5, 8, 1}, " +
                    "{3, -2, 6}, " +
                    "{2, 1, -1}"
        val m = Matrix(strM)
        var mrez = m.transformMatrix(Matrix.TransformType.INVERSE)
        assertEquals(Fraction(-4, 107u), mrez[0, 0])
        assertEquals(Fraction(9, 107u), mrez[0, 1])
        assertEquals(Fraction(50, 107u), mrez[0, 2])
        assertEquals(Fraction(15, 107u), mrez[1, 0])
        assertEquals(Fraction(-7, 107u), mrez[1, 1])
        assertEquals(Fraction(-27,107u), mrez[1, 2])
        assertEquals(Fraction(7,107u), mrez[2, 0])
        assertEquals(Fraction(11, 107u), mrez[2, 1])
        assertEquals(Fraction(-34,107u), mrez[2, 2])
        mrez = m.transformMatrix(Matrix.TransformType.BOTTOM_TRIANGLE)
        assertEquals(Fraction(5), mrez[0, 0])
        assertEquals(Fraction(), mrez[0, 1])
        assertEquals(Fraction(), mrez[0, 2])
        assertEquals(Fraction(3), mrez[1, 0])
        assertEquals(Fraction(-34, 5u), mrez[1, 1])
        assertEquals(Fraction(0), mrez[1, 2])
        assertEquals(Fraction(2), mrez[2, 0])
        assertEquals(Fraction(-11, 5u), mrez[2, 1])
        assertEquals(Fraction(-107,34u), mrez[2, 2])
        mrez = m.transformMatrix(Matrix.TransformType.TOP_TRIANGLE)
        assertEquals(Fraction(1), mrez[0, 0])
        assertEquals(Fraction(8,5u), mrez[0, 1])
        assertEquals(Fraction(1,5u), mrez[0, 2])
        assertEquals(Fraction(), mrez[1, 0])
        assertEquals(Fraction(1), mrez[1, 1])
        assertEquals(Fraction(-27, 34u), mrez[1, 2])
        assertEquals(Fraction(), mrez[2, 0])
        assertEquals(Fraction(), mrez[2, 1])
        assertEquals(Fraction(1), mrez[2, 2])
    }

    @Test
    fun test_init_from_string() {
        val str = "{1.2, 2.6, 3/4}," +
                "{4/7, 5, 6}," +
                "{8 ,18/6, 9}"
        val m = Matrix(str)
        assertEquals(m[1,1], Fraction(5))
        assertEquals(m[2,1], Fraction(3))
        assertEquals(m[0,0], Fraction(6, 5u))

    }

    @Test
    fun test_determinant() {
        var str = "{1.2, 2.6, 3/4}," +
                "{4/7, 5, 6}," +
                "{8 ,18/6, 50}"
        var m = Matrix(str)
        assertEquals(Fraction(1501, 5u), m.determinant())
        str =   "{1, 2, 3}," +
                "{4, 5, 6}," +
                "{7 ,8, 9}"
        m = Matrix(str)
        assertEquals(Fraction(), m.determinant())

    }


}