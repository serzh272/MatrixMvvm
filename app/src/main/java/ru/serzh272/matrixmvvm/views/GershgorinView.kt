package ru.serzh272.matrixmvvm.views

import ru.serzh272.matrixmvvm.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import ru.serzh272.matrix.Fraction
import ru.serzh272.matrixmvvm.utils.Matrix
import kotlin.math.max
import kotlin.math.min

@ExperimentalUnsignedTypes
class GershgorinView @JvmOverloads constructor(context: Context,
                     attrs: AttributeSet?,
                     defStyleAttr: Int = 0):
    View(context, attrs , defStyleAttr) {

    private lateinit var paint: Paint
    private var mMatrix: Matrix = Matrix()
    private var mGraphColor = 0
    private var mXAxisColor = 0
    private var mYAxisColor = 0
    private var filled = false

    constructor(context: Context, m: Matrix): this(context, null, 0){
        mMatrix = m.copy()
        initAttrs(context, null)
    }

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        initAttrs(context, attrs)
    }

    fun getFrMatrix(): Matrix {
        return mMatrix
    }

    fun drawCircles(matrix: Matrix) {
        mMatrix = matrix
        invalidate()
        requestLayout()
    }

    fun GetMaxValue(arr: DoubleArray): Double {
        var max = arr[0]
        for (i in 1 until arr.size) {
            max = max(max, arr[i])
        }
        return max
    }

    fun GetMinValue(arr: DoubleArray): Double {
        var min = arr[0]
        for (i in 1 until arr.size) {
            min = Math.min(min, arr[i])
        }
        return min
    }

    override fun onDraw(canvas: Canvas?) {
        //super.onDraw(canvas)
        drawAxis(canvas)
        drawCircles(mMatrix, canvas)
    }

    private fun drawAxis(canvas: Canvas?) {
        val width = width
        val height = height
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = mXAxisColor
        canvas?.drawLine(0f, height.toFloat() / 2, width.toFloat(), height.toFloat() / 2, paint)
        paint.color = mYAxisColor
        canvas?.drawLine(width.toFloat() / 2, 0f, width.toFloat() / 2, height.toFloat(), paint)
    }


    @ExperimentalUnsignedTypes
    fun drawCircles(m: Matrix, canvas: Canvas?) {
        paint.color = mGraphColor
        val arrFr: MutableList<MutableList<Fraction>> = mutableListOf()
        var minX: Fraction
        var minY: Fraction
        var maxX: Fraction
        var maxY: Fraction
        minX = Fraction()
        minY = Fraction()
        maxX = Fraction()
        maxY = Fraction()
        mMatrix = m.copy()
        var mult = 1.0
        var r = Fraction()
        var a = Fraction()
        for (i in 0 until m.numColumns) {
            for (j in 0 until m.numColumns) {
                if (i != j) {
                    a = m[i,j].copy()
                    if (a < 0) {
                        a *= -1
                    }
                    r += a
                }
            }
            a = m[i, i].copy()
            if (min((width - 10) / ((r + if (a < 0) (-a) else a)*2).toDouble(), (height - 10) / (r*2).toDouble()
                ) <= mult || mult == 1.0
            ) {
                mult = min(
                    (width - 10) / ((r + if (a < 0) -a else a)*2).toDouble(), (height - 10) / (r * 2).toDouble()
                )
            }
            r = Fraction()
            a = Fraction()
        }
        for (i in 0 until m.numColumns) {
            for (j in 0 until m.numColumns) {
                if (i != j) {
                    a = m[i,j].copy()
                    if (a<0) {
                        a *= -1
                    }
                    r = r + a
                }
            }
            a = m[i, i].copy()
            arrFr.add(mutableListOf())
            minX = Fraction.min(minX, a - r)
            minY = Fraction.min(minY, r)
            maxX = Fraction.max(maxX, a + r)
            maxY = Fraction.max(maxY, r)
            arrFr[arrFr.size - 1].add(a)
            arrFr[arrFr.size - 1].add(r.copy())
            r = Fraction()
        }
        for (ar in arrFr) {
            canvas?.drawCircle((ar[0].toDouble() * mult).toFloat() + width / 2,
                height.toFloat() / 2,
                (ar[1].toDouble() * mult).toFloat(), paint
            )
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val a: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.GershgorinViewGroupLayout, 0, 0)
        try {
            mGraphColor = a.getColor(R.styleable.GershgorinViewGroupLayout_graph_color, Color.BLUE)
            mXAxisColor = a.getColor(R.styleable.GershgorinViewGroupLayout_x_axis_color, Color.RED)
            mYAxisColor = a.getColor(R.styleable.GershgorinViewGroupLayout_y_axis_color, Color.GREEN)
            filled = a.getBoolean(R.styleable.GershgorinViewGroupLayout_filled, false)
        } finally {
            a.recycle()
        }
    }
}