package ru.serzh272.matrixmvvm.views

import ru.serzh272.matrixmvvm.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.android.material.color.MaterialColors
import ru.serzh272.matrix.Fraction
import ru.serzh272.matrixmvvm.extensions.dpToPx
import ru.serzh272.matrixmvvm.utils.Matrix
import kotlin.math.*

@ExperimentalUnsignedTypes
class GershgorinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    private var textPaint: Paint
    private var circlesPaint: Paint
    private var axisPaint: Paint
    private var mMatrix: Matrix = Matrix(
        "{-2, 1/2, 1/2}," +
                "{-1/2, -7/2, 3/2}," +
                "{4/5 ,-1/2, 1/2}"
    )
    private var mGraphColor = 0
    private var mXAxisColor = 0
    private var mYAxisColor = 0
    private var filled = false

    constructor(context: Context, m: Matrix) : this(context, null, 0) {
        mMatrix = m.copy()
        initAttrs(context, null)
    }

    init {
        circlesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        axisPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 0f
        textPaint.textSize = context.dpToPx(12)
        textPaint.color = MaterialColors.getColor(this, R.attr.colorOnPrimary)
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
        val origin = PointF(width.toFloat() / 2, height.toFloat() / 2)
        val circles = calculateCircles(mMatrix)
        //val circles = listOf<Circle>(Circle(300f, PointF(-50f,0f)), Circle(300f, PointF(-100f,0f)), Circle(150f, PointF(-25f,-300f)))
        circlesPaint.color = mYAxisColor
        circlesPaint.style = Paint.Style.STROKE
        circlesPaint.strokeWidth = context.dpToPx(1)
        drawCircles(circles, canvas, origin)
    }

    private fun drawAxis(canvas: Canvas?, origin: PointF) {
        val width = width
        val height = height
        axisPaint.style = Paint.Style.STROKE
        axisPaint.strokeWidth = context.dpToPx(1)
        axisPaint.color = mXAxisColor
        drawArrow(canvas, 0f, origin.y, width.toFloat(), origin.y, axisPaint)
        axisPaint.color = mYAxisColor
        drawArrow(canvas, origin.x, height.toFloat(), origin.x, 0f, axisPaint)
        canvas?.drawText(
            "(0; 0)",
            origin.x + context.dpToPx(2),
            origin.y - context.dpToPx(2),
            textPaint
        )
    }

    private fun drawArrow(canvas: Canvas?, startX: Float, startY: Float, stopX: Float, stopY: Float, p: Paint) {
        canvas?.drawLine(startX, startY, stopX, stopY, p)
        val ang = atan((-stopY + startY)/(stopX - startX))
        val a = Math.toRadians(15.0)
        val l = context.dpToPx(6)
        var x1:Double
        var y1:Double
        var x3:Double
        var y4:Double
        if (stopX >= startX){
            x1 = stopX - l* cos(ang - a)
            y1 = stopY + l* sin(ang - a)
            x3 = stopX - l* sin(PI/2 - ang - a)
            y4 = stopY + l* cos(PI/2 - ang - a)
        }else{
            x1 = stopX + l* cos(ang - a)
            y1 = stopY - l* sin(ang - a)
            x3 = stopX + l* sin(PI/2 - ang - a)
            y4 = stopY - l* cos(PI/2 - ang - a)

        }
        canvas?.drawLine(stopX, stopY, x1.toFloat(), y1.toFloat(), p)
        canvas?.drawLine(stopX, stopY, x3.toFloat(), y4.toFloat(), p)
        //canvas?.drawLine(startX, startY, startX + context.dpToPx(2), context.dpToPx(8), p)
    }

    fun calculateCircles(m: Matrix): MutableList<Circle> {
        val rez = mutableListOf<Circle>()
        val arrFr: MutableList<MutableList<Fraction>> = mutableListOf()
        mMatrix = m.copy()
        var r = Fraction()
        var a = Fraction()

        for (i in 0 until m.numColumns) {
            for (j in 0 until m.numColumns) {
                if (i != j) {
                    a = m[i, j].copy()
                    if (a < 0) {
                        a *= -1
                    }
                    r = r + a
                }
            }
            a = m[i, i].copy()
            arrFr.add(mutableListOf())
            arrFr[arrFr.size - 1].add(a)
            arrFr[arrFr.size - 1].add(r.copy())
            r = Fraction()
        }
        for (ar in arrFr) {
            rez.add(Circle((ar[1]).toFloat(), PointF((ar[0].toFloat()), 0f)))
        }
        return rez
    }

    @ExperimentalUnsignedTypes
    fun drawCircles(circles: List<Circle>, canvas: Canvas?, origin: PointF) {
        val marg = context.dpToPx(8)
        circlesPaint.color = mGraphColor
        var minX = circles[0].anchor.x - circles[0].radius
        var minY = circles[0].anchor.y - circles[0].radius
        var maxX = circles[0].anchor.x + circles[0].radius
        var maxY = circles[0].anchor.y + circles[0].radius
        var mult = 1.0f
        for (c in circles) {
            minX = min(minX, c.anchor.x - c.radius)
            minY = min(minY, c.anchor.y - c.radius)
            maxX = max(maxX, c.anchor.x + c.radius)
            maxY = max(maxY, c.anchor.y + c.radius)
        }
        mult = (height - 2 * marg) / abs(maxY - minY)

        if (width < mult * abs(maxX - minX)) {
            mult = (width - 2 * marg) / abs(maxX - minX)

        }
        minX *= mult
        minY *= mult
        maxX *= mult
        maxY *= mult

        origin.x = (width - maxX - minX) / 2
        origin.y = (height + maxY + minY) / 2
        drawAxis(canvas, origin)
        for (c in circles) {

            canvas?.drawCircle(
                origin.x + c.anchor.x * mult,
                origin.y - c.anchor.y * mult,
                c.radius * mult,
                circlesPaint
            )
            drawArrow(canvas,
                origin.x + c.anchor.x * mult,
                origin.y - c.anchor.y * mult,
                origin.x + c.anchor.x * mult - c.radius*mult*sin(PI/4).toFloat(),
                origin.y - c.anchor.y * mult - c.radius*mult*sin(PI/4).toFloat(),
                circlesPaint)
            canvas?.drawText(
                "r = ${c.radius}",
                origin.x + c.anchor.x * mult - c.radius*mult*sin(PI/4).toFloat()/2,
                origin.y - c.anchor.y * mult - c.radius*mult*sin(PI/4).toFloat()/2,
                textPaint
            )
            canvas?.drawLine(
                origin.x + c.anchor.x * mult - context.dpToPx(2),
                origin.y - c.anchor.y * mult - context.dpToPx(2),
                origin.x + c.anchor.x * mult + context.dpToPx(2),
                origin.y - c.anchor.y * mult + context.dpToPx(2),
                circlesPaint
            )
            canvas?.drawLine(
                origin.x + c.anchor.x * mult + context.dpToPx(2),
                origin.y - c.anchor.y * mult - context.dpToPx(2),
                origin.x + c.anchor.x * mult - context.dpToPx(2),
                origin.y - c.anchor.y * mult + context.dpToPx(2),
                circlesPaint
            )
            canvas?.drawText(
                "(${c.anchor.x}; ${c.anchor.y})",
                origin.x + c.anchor.x * mult + context.dpToPx(2),
                origin.y - c.anchor.y * mult - context.dpToPx(2)+2*textPaint.textSize,
                textPaint
            )
        }
//        circlesPaint.style = Paint.Style.FILL
//        circlesPaint.color = Color.BLACK
//        circlesPaint.textSize = 20f
//        canvas?.drawText(
//            "minX=${minX * mult}, minY=${minY * mult}, maxX=${maxX * mult}, maxY=${maxY * mult}, mult=${mult}",
//            0f,
//            height.toFloat()-2*circlesPaint.textSize,
//            circlesPaint
//        )
//        canvas?.drawText(
//            "origin.x = ${origin.x}, origin.y = ${origin.y}, width=$width, height=$height",
//            0f,
//            height.toFloat()-circlesPaint.textSize,
//            circlesPaint
//        )

    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val a: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.GershgorinViewGroupLayout, 0, 0)
        try {
            mGraphColor = a.getColor(R.styleable.GershgorinViewGroupLayout_graph_color, Color.BLUE)
            mXAxisColor = a.getColor(R.styleable.GershgorinViewGroupLayout_x_axis_color, Color.RED)
            mYAxisColor =
                a.getColor(R.styleable.GershgorinViewGroupLayout_y_axis_color, Color.GREEN)
            filled = a.getBoolean(R.styleable.GershgorinViewGroupLayout_filled, false)
        } finally {
            a.recycle()
        }
    }

    inner class Circle(
        var radius: Float = 1.0f,
        var anchor: PointF = PointF(0f, 0f)
    ) {

    }
}


