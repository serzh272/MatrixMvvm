package ru.serzh272.matrixmvvm.utils

import android.os.Parcel
import android.os.Parcelable
import ru.serzh272.matrixmvvm.App
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.exceptions.DeterminantZeroException
import ru.serzh272.matrixmvvm.exceptions.MatrixDimensionsException
import kotlin.math.sqrt

@ExperimentalUnsignedTypes
class Matrix(r: Int = 3, c: Int = 3) : Parcelable {
    var numColumns: Int = c
        set(value) {
            field = if ((value > 0) and (value <= 20)) value else 3
        }
    var numRows: Int = r
        set(value) {
            field = if ((value > 0) and (value <= 20)) value else 3
        }

    private var matr: MutableList<MutableList<Fraction>> =
        MutableList(numRows) { MutableList(numColumns) { Fraction() } }

    constructor(parcel: Parcel) : this(parcel.readString())

    init {
        matr = MutableList(r) { MutableList(c) { Fraction() } }
    }

    constructor(mStr: String?) : this() {
        val strRows = mStr?.split(Regex("\\}(,\\s*)\\{"))?.map { it.replace(Regex("[}{]"), "") }
        this.numRows = strRows!!.size
        this.numColumns = strRows[0].split(Regex(",\\s*")).size
        var rowItems: List<String>
        for (r in 0 until this.numRows) {
            rowItems = strRows[r].split(Regex(",\\s*")).map { it.trim() }
            for (c in 0 until this.numColumns) {
                matr[r][c] = Fraction(rowItems[c])
            }
        }
    }

    @Throws(MatrixDimensionsException::class)
    operator fun plus(m: Matrix): Matrix {
        if ((this.numColumns != m.numColumns) or (this.numRows != m.numRows)) {
            throw MatrixDimensionsException(App.applicationContext().resources.getString(R.string.not_equals_sizes))
        } else {
            val res = Matrix(this.numRows, this.numColumns)
            for (i: Int in 0 until this.numRows) {
                for (j: Int in 0 until this.numColumns) {
                    res[i, j] = this[i, j] + m[i, j]
                }
            }
            return res
        }
    }

    @Throws(MatrixDimensionsException::class)
    operator fun minus(m: Matrix): Matrix {
        if ((this.numColumns != m.numColumns) or (this.numRows != m.numRows)) {
            throw MatrixDimensionsException(App.applicationContext().resources.getString(R.string.not_equals_sizes))
        } else {
            val res = Matrix(this.numRows, this.numColumns)
            for (i: Int in 0 until this.numRows) {
                for (j: Int in 0 until this.numColumns) {
                    res[i, j] = this[i, j] - m[i, j]
                }
            }
            return res
        }
    }

    operator fun get(r: Int, c: Int): Fraction {
        return this.matr[r][c]
    }

    operator fun set(r: Int, c: Int, fr: Fraction) {
        this.matr[r][c] = fr.copy()
    }

    @Throws(MatrixDimensionsException::class)
    operator fun times(m: Matrix): Matrix {
        when {
            this.numColumns == m.numRows -> {
                val res = Matrix(this.numRows, m.numColumns)
                for (i in 0 until this.numRows) {
                    for (j in 0 until m.numColumns) {
                        res[i, j] = Fraction()
                        for (g in 0 until m.numRows) {

                            res[i, j] = res[i, j] + this[i, g] * m[g, j]
                        }
                    }
                }
                return res
            }
            (this.numRows == 1) and (this.numColumns == 1) -> {
                return this[0, 0] * m
            }
            (m.numRows == 1) and (m.numColumns == 1) -> {
                return this * m[0, 0]
            }
            else -> {
                throw MatrixDimensionsException(App.applicationContext().resources.getString(R.string.not_equals_cols_rowssizes))
            }
        }
    }

    operator fun times(fr: Fraction): Matrix {
        val res = Matrix(this.numRows, this.numColumns)
        for (i in 0 until this.numRows) {
            for (j in 0 until this.numColumns) {
                res[i, j] = res[i, j] * fr
            }
        }
        return res
    }

    operator fun Fraction.times(m: Matrix): Matrix {
        return m * this
    }

    /** Adds row to [pos] position
     */
    fun addRow(pos: Int) {
        this.matr.add(pos, MutableList(this.numColumns) { Fraction() })
        this.numRows++
    }

    /** Adds row to end position
     */
    fun addRow() {
        this.matr.add(MutableList(this.numColumns) { Fraction() })
        this.numRows++
    }

    /** Adds column to [pos] position
     */
    fun addColumn(pos: Int) {
        for (i in 0 until this.numRows) {
            this.matr[i].add(pos, Fraction())
        }
        this.numColumns++
    }

    /** Adds column to end position
     */
    fun addColumn() {
        for (i in 0 until this.numRows) {
            this.matr[i].add(Fraction())
        }
        this.numColumns++
    }

    /** Removes row at [pos] position
     */
    fun removeRowAt(pos: Int) {
        if ((pos < this.numRows) and (pos >= 0)) {
            this.matr.removeAt(pos)
            this.numRows--
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    /** Removes row at end position
     */
    fun removeRow() {
        removeRowAt(numRows - 1)
    }

    /** Removes column at [pos] position
     */
    fun removeColumnAt(pos: Int) {
        if ((pos < this.numColumns) and (pos >= 0)) {
            for (i in 0 until this.numRows) {
                matr[i].removeAt(pos)
            }
            this.numColumns--
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    /** Removes column at end position
     */
    fun removeColumn() {
        removeColumnAt(numColumns - 1)
    }

    /** Adds [n] rows below [pos] position
     */
    fun addRows(pos: Int, n: Int) {
        for (i in 1..n) {
            this.addRow(pos + i - 1)
        }
    }

    /** Adds [n] columns after [pos] position
     */
    fun addColumns(pos: Int, n: Int) {
        for (i in 1..n) {
            this.addColumn(pos + i - 1)
        }
    }

    /** Swaps [r2] and [r1] rows
     */
    fun swapRows(r1: Int, r2: Int) {
        var f: Fraction
        for (j in 0 until this.numColumns) {
            f = this[r1, j]
            this[r1, j] = this[r2, j]
            this[r2, j] = f
        }
    }

    /** Swaps [c2] and [c1] columns
     */
    fun swapColumns(c1: Int, c2: Int) {
        var f: Fraction
        for (i in 0 until this.numRows) {
            f = this[i, c1]
            this[i, c1] = this[i, c2]
            this[i, c2] = f
        }
    }

    /** Subtracts from row [r2] multiplication row [r1] by [fr] parameter
     */
    fun multRows(r1: Int, r2: Int, fr: Fraction) {
        for (j in 0 until this.numColumns) {
            this[r2, j] -= this[r1, j] * fr
        }
    }

    /** Subtracts from column [c2] multiplication column [c1] by [fr] parameter
     */
    fun multColumns(c1: Int, c2: Int, fr: Fraction) {
        for (i in 0 until this.numRows) {
            this[i, c2] -= this[i, c1] * fr
        }
    }

    /** Returns a third norm of matrix
     */
    fun norm3(): Double {
        var fr = Fraction()
        for (i in 0 until this.numRows) {
            for (j in 0 until this.numColumns) {
                val frItem = Fraction(this[i, j].numerator, this[i, j].denominator)
                fr += frItem.power(2)
            }
        }
        return sqrt(fr.toDouble())
    }

    /** Transpose this matrix.
     */
    fun transpose() {
        val m = Matrix(this.numColumns, this.numRows)
        for (i in 0 until this.numRows) {
            for (j in 0 until this.numColumns) {
                m[j, i] = this[i, j]
            }
        }
        this.matr = m.matr
        this.numRows = m.numRows
        this.numColumns = m.numColumns
    }

    fun getTranspose(): Matrix {
        val m = Matrix(this.numColumns, this.numRows)
        for (i in 0 until this.numRows) {
            for (j in 0 until this.numColumns) {
                m[j, i] = this[i, j]
            }
        }
        return m
    }

    fun copy(): Matrix {
        val res = Matrix(this.numRows, this.numColumns)
        for (i in 0 until this.numRows) {
            for (j in 0 until this.numColumns) {
                res[i, j] = this[i, j].copy()
            }
        }
        return res
    }

    /** Returns a determinant of matrix.
     */
    @Throws(MatrixDimensionsException::class)
    fun determinant(): Fraction {
        if (this.numRows == this.numColumns) {
            val m: Matrix = this.copy()
            val n: Int = this.numColumns
            var fr1: Fraction
            var fr2: Fraction
            var d = Fraction(1, 1u)
            var sumRow = Fraction(0)
            var sumCol = Fraction(0)
            for (i in 0 until n) {
                for (j in 0 until n) {
                    if (m[i, j].equals(0)) {
                        sumRow = Fraction(0)
                        sumCol = Fraction(0)
                        for (p in 0 until n) {
                            sumCol += m[i, p].abs()
                            sumRow += m[p, j].abs()
                        }
                        if ((sumCol.equals(0)) or (sumRow.equals(0))) {
                            return Fraction()
                        }
                    }
                }
            }
            for (step in 0 until (n - 1)) {
                for (i in step until n) {
                    if (!(m[i, step].equals(0))) {
                        if (m[step, step].equals(0)) {
                            m.swapRows(step, i)
                            d *= -1
                        } else {
                            for (j in 0 until n) {
                                if ((i != j) and !(m[j, step].equals(0)) and (j >= step)) {
                                    if (j < i) {
                                        fr1 = m[i, step].copy()
                                        fr2 = m[j, step].copy()
                                        m.multRows(j, i, fr1 / fr2)
                                    } else {
                                        fr1 = m[j, step].copy()
                                        fr2 = m[i, step].copy()
                                        m.multRows(i, j, fr1 / fr2)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (i in 0 until n) {
                d *= m[i, i]
            }
            return d
        } else {
            throw MatrixDimensionsException(App.applicationContext().resources.getString(R.string.not_squared))
        }
    }

    /** Set square matrix to identity matrix.
     */
    private fun setEMatr() {
        if (this.numRows == this.numColumns) {
            for (i in 0 until this.numRows) {
                for (j in 0 until this.numColumns) {
                    if (i == j) {
                        this[i, j] = Fraction(1)
                    } else {
                        this[i, j] = Fraction(0)
                    }
                }
            }
        } else {
            throw MatrixDimensionsException(App.applicationContext().resources.getString(R.string.not_squared))
        }
    }

    /** Returns the inverse of the matrix, the lower triangular matrix or the upper triangular matrix with diagonal identity.
     *The [type] value should be from [Matrix.TransformType] and may be INVERSE, TOP_TRIANGLE or BOTTOM_TRIANGLE*/
    @Throws(DeterminantZeroException::class)
    fun transformMatrix(type: TransformType): Matrix {
        val dt: Fraction = this.determinant()
        if (dt.equals(0)) {
            throw DeterminantZeroException(App.applicationContext().resources.getString(R.string.determinant_is_zero))
        } else {
            var m = Fraction()
            val n: Int = this.numColumns
            val e = Matrix(n, n)
            val x = Matrix(n, n)
            val y = Matrix(n, n)
            val b = Matrix(n, n)
            val c = Matrix(n, n)
            e.setEMatr()
            for (j in 0 until n) {
                for (i in j until n) {
                    for (g in 0 until j) {
                        m += b[i, g] * c[g, j]
                    }
                    b[i, j] = this[i, j] - m
                    m = Fraction()
                    for (g in 0 until j) {
                        m += b[j, g] * c[g, i]
                    }
                    c[j, i] = (this[j, i] - m) / b[j, j]
                    m = Fraction()
                }
            }
            for (i in 0 until n) {
                for (j in 0 until n) {
                    for (g in 0 until j) {
                        m += b[j, g] * y[g, i]
                    }
                    y[j, i] = (e[j, i] - m) / b[j, j]
                    m = Fraction()
                }
            }
            for (i in 0 until n) {
                for (j in n - 1 downTo 0) {
                    for (g in 0 until n - j - 1) {
                        m += c[j, n - g - 1] * x[n - g - 1, i]
                    }
                    x[j, i] = y[j, i] - m
                    m = Fraction()
                }
            }
            return when (type) {
                TransformType.INVERSE -> x
                TransformType.LOWER_TRIANGULAR -> b
                TransformType.UPPER_TRIANGULAR -> c
            }
        }
    }

    /** Returns a Frobenius matrix from square matrix.
     */
    @Throws(MatrixDimensionsException::class)
    fun getFrobeniusMatrix(): Matrix {
        if (this.numRows == this.numColumns) {
            val n: Int = this.numColumns
            var mFr: Matrix = this.copy()
            val e = Matrix(n, n)
            for (g in 0 until n - 1) {
                var d = Fraction()
                for (i in 0..n - g - 2) {
                    d += mFr[n - g - 1, i]
                }
                if (d.equals(0)) {
                    var b = Matrix(n - g - 1, n - g - 1)
                    for (i in 0 until n - g - 1) {
                        for (j in 0 until n - g - 1) {
                            b[i, j] = mFr[i, j]
                        }
                    }
                    b = b.getFrobeniusMatrix()
                    for (i in 0 until n - g - 1) {
                        for (j in 0 until n - g - 1) {
                            mFr[i, j] = b[i, j]
                            //mFr.A[i][j].Color = Color.FromRgb(127, 251, 189);
                        }
                    }
//                    for (i in n - g - 1 until n) {
//                        for (j in n - g - 1 until n) {
//                            //mFr.A[i, j].Color = Color.FromRgb(127, 251, 189);
//                        }
//                    }
                    return mFr
                } else if (mFr[n - g - 1, n - g - 2].equals(0)) {
                    mFr.swapColumns(n - g - 1, n - g - 2)
                    mFr.swapRows(n - g - 1, n - g - 2)
                }
                e.setEMatr()
                for (i in 0 until n) {
                    e[n - g - 2, i] = mFr[n - g - 1, i].copy()
                }
                mFr = if (!e.determinant().equals(0)) {
                    (e * mFr) * (e.transformMatrix(TransformType.INVERSE))
                } else {
                    try {
                        (e * mFr) * (e.transformMatrix(TransformType.INVERSE))
                    } catch (ex: DeterminantZeroException) {
                        throw DeterminantZeroException(App.applicationContext().resources.getString(R.string.unable_matrix_transform))
                        Matrix()
                    }
                }
            }
            return mFr
        } else {
            throw MatrixDimensionsException(App.applicationContext().resources.getString(R.string.not_squared))
        }
    }

    override fun toString(): String {
        var res = ""
        for (i in 0 until this.numRows) {
            for (j in 0 until this.numColumns) {
                if (this[i, j] == null) {
                    res += "0"
                } else {
                    res += this[i, j]
                }

                if (j != (this.numColumns - 1)) {
                    res += "\t\t"
                }
            }
            if (i != (this.numRows - 1)) {
                res += '\n'
            }
        }
        return res
    }

    /** Types of matrix for [transformMatrix] method.
     */
    enum class TransformType {
        INVERSE,
        UPPER_TRIANGULAR,
        LOWER_TRIANGULAR
    }

    fun exportToString(): String {
        var res = ""
        for (i in 0 until this.numRows) {
            res += "{"
            for (j in 0 until this.numColumns) {
                res += if (this[i, j] == null) {
                    "0"
                } else {
                    this[i, j]
                }
                res += if (j != (this.numColumns - 1)) {
                    ", "
                } else {
                    "}"
                }
            }
            if (i != (this.numRows - 1)) {
                res += ", "
            }
        }
        return res
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.exportToString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Matrix> {
        override fun createFromParcel(parcel: Parcel): Matrix {
            return Matrix(parcel)
        }

        override fun newArray(size: Int): Array<Matrix?> {
            return arrayOfNulls(size)
        }
    }
}