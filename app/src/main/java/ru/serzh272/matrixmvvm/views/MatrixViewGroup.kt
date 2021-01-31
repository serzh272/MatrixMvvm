package ru.serzh272.matrixmvvm.views

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import ru.serzh272.matrix.*
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.extensions.dpToPx
import ru.serzh272.matrixmvvm.utils.Matrix
import kotlin.math.min

@ExperimentalUnsignedTypes
class MatrixViewGroup : ViewGroup, View.OnClickListener {
    companion object{
        private const val DEFAULT_BUTTON_THICKNESS = 32
        private const val DEFAULT_BUTTON_WIDTH = 48
        private const val DEFAULT_TEXT_VIEW_WIDTH = 48
        private const val DEFAULT_SPACING = 4
    }
    var mMatrix = Matrix(
        "{1, 2, 3}," +
                "{9, 8, 17}," +
                "{4, 2, 5}"
    )

    @ExperimentalUnsignedTypes
    var numRows: Int = mMatrix.numRows

    @ExperimentalUnsignedTypes
    var numColumns: Int = mMatrix.numColumns
    var mode = 1
    var spacing: Int = context.dpToPx(DEFAULT_SPACING).toInt()
    private var buttonThickness: Int = context.dpToPx(DEFAULT_BUTTON_THICKNESS).toInt()
    private var buttonWidth: Int = context.dpToPx(DEFAULT_BUTTON_WIDTH).toInt()
    private var textViewWidth: Int = context.dpToPx(DEFAULT_TEXT_VIEW_WIDTH).toInt()
    private var cellSize: Float = 50.0f
    var cellsColor: Int = Color.WHITE
    private var btnDecCols: ImageView = ImageView(context)
    private var btnIncRows: ImageView = ImageView(context)
    private var btnIncCols: ImageView = ImageView(context)
    private var btnDecRows: ImageView = ImageView(context)
    private var btnIncRowsCols: ImageView = ImageView(context)
    private var btnDecRowsCols: ImageView = ImageView(context)
    private var mEditText: EditText = EditText(context)
    private var currentPos: Point = Point(0, 0)
    private var tvNumRows: MaterialTextView = MaterialTextView(context)
    private var tvNumCols: MaterialTextView = MaterialTextView(context)

    @ExperimentalUnsignedTypes
    private var mFractionViews: MutableList<MutableList<FractionView>> = MutableList(numRows) {
        MutableList(numColumns) {FractionView(context)}}

    init {
        mFractionViews = MutableList(numRows) { MutableList(numColumns) { FractionView(context) } }
        with(btnDecRows){
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_remove,context.theme))
            addView(this)
            setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
        }
        btnDecRows.setOnClickListener(this)
        with(btnIncRows){
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_add,context.theme))
            addView(this)
            setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
        }
        btnIncRows.setOnClickListener(this)
        with(btnDecCols){
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_remove,context.theme))
            addView(this)
            setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
        }
        btnDecCols.setOnClickListener(this)
        with(btnIncCols){
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_add,context.theme))
            addView(this)
            setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
        }
        btnIncCols.setOnClickListener(this)
        with(btnIncRowsCols){
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_add,context.theme))
            addView(this)
            setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
        }
        btnIncRowsCols.setOnClickListener(this)
        with(btnDecRowsCols){
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_remove,context.theme))
            addView(this)
            setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
        }
        btnDecRowsCols.setOnClickListener(this)
        tvNumCols.text = "3"
        //tvNumCols.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        with(tvNumCols){
            gravity = Gravity.CENTER
            text = "3"
            addView(this)
        }
        with(tvNumRows){
            gravity = Gravity.CENTER
            text = "3"
            addView(this)
        }

        mEditText.inputType = InputType.TYPE_NULL
        mEditText.inputType = InputType.TYPE_CLASS_DATETIME
        mEditText.setBackgroundColor(Color.WHITE)
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                mFractionViews[i][j].mFraction = mMatrix[i, j]
                mFractionViews[i][j].setOnClickListener(this)
                addView(mFractionViews[i][j])
            }
        }
    }

    constructor(context: Context?, matrix: Matrix) : super(context) {
        mMatrix = matrix

    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context!!, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(context!!, attrs)
    }

    fun showEditText(fv: FractionView) {
        mEditText.setText(fv.toString())
        mEditText.layout(fv.left, fv.top, fv.right, fv.bottom)
        if (indexOfChild(mEditText) != -1) {
            removeView(mEditText)
            addView(mEditText)
        } else {
            addView(mEditText)
        }
    }

    fun hideEditText() {
        if (indexOfChild(mEditText) != -1) {
            mFractionViews[currentPos.x][currentPos.y].mFraction.setFromString(mEditText.text.toString())
            removeView(mEditText)
            currentPos.x = 0
            currentPos.y = 0
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MatrixLayout, 0, 0
        )
        try {
            numRows = a.getInt(R.styleable.MatrixLayout_rows, 3)
            numColumns = a.getInt(R.styleable.MatrixLayout_columns, 3)
            spacing = a.getDimension(R.styleable.MatrixLayout_spacing,
                context.dpToPx(DEFAULT_SPACING)).toInt()
            buttonThickness = a.getDimension(R.styleable.MatrixLayout_buttons_thickness,
                context.dpToPx(DEFAULT_BUTTON_THICKNESS)).toInt()
            cellsColor = a.getColor(R.styleable.MatrixLayout_cells_color, Color.LTGRAY)
        } finally {
            a.recycle()
        }
    }

    /*override fun get(r: Int, c: Int): Fraction {
        TODO("Not yet implemented")
    }

    override fun set(r: Int, c: Int, fr: Fraction) {
        TODO("Not yet implemented")
    }*/


    fun addRow(pos: Int) {
        this.mFractionViews.add(pos, MutableList(this.numColumns) {
            FractionView(context)
        })
        for (j in 0 until numColumns) {
            mFractionViews[pos][j].mFraction = Fraction()
            addView(mFractionViews[pos][j])
        }
        this.numRows++
    }

    fun addRow() {
        this.mFractionViews.add(MutableList(this.numColumns) {
            FractionView(context)
        })
        for (j in 0 until numColumns) {
            mFractionViews[numRows][j].mFraction = Fraction()
            addView(mFractionViews[numRows][j])
        }
        this.numRows++
    }

    @ExperimentalUnsignedTypes
    fun addColumn(pos: Int) {
        for (i in 0 until this.numRows) {
            this.mFractionViews[i].add(
                pos,
                FractionView(context)
            )
            addView(mFractionViews[i][pos])
        }
        this.numColumns++
    }

    @ExperimentalUnsignedTypes
    fun addColumn() {
        for (i in 0 until this.numRows) {
            this.mFractionViews[i].add(
                FractionView(context)
            )
            addView(mFractionViews[i][numColumns])
        }
        this.numColumns++
    }

    fun removeRowAt(pos: Int) {
        if ((pos < this.numRows) and (pos >= 0)) {
            for (j in 0 until numColumns) {
                removeView(mFractionViews[pos][j])
            }
            this.mFractionViews.removeAt(pos)
            this.numRows--
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    fun removeColumnAt(pos: Int) {
        if ((pos < this.numColumns) and (pos >= 0)) {
            for (i in 0 until this.numRows) {
                removeView(mFractionViews[i][pos])
                mFractionViews[i].removeAt(pos)
            }
            this.numColumns--
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    fun addRows(pos: Int, n: Int) {
        for (i in 1..n) {
            this.addRow(pos + i - 1)
        }
    }

    fun addColumns(pos: Int, n: Int) {
        for (i in 1..n) {
            this.addColumn(pos + i - 1)
        }
    }

    fun removeRow() {
        removeRowAt(numRows - 1)
    }

    fun removeColumn() {
        removeColumnAt(numColumns - 1)
    }

    fun updateFractionView(r: Int, c: Int) {
        mFractionViews[r][c].invalidate()
    }
    /*
    override fun transpose() {
        TODO("Not yet implemented")
    }*/


    @ExperimentalUnsignedTypes
    override fun onClick(v: View?) {
        when (v) {
            is FractionView -> {
                mFractionViews[currentPos.x][currentPos.y].mFraction.setFromString(mEditText.text.toString())
                mFractionViews[currentPos.x][currentPos.y].invalidate()
                currentPos.x = v.pos.x
                currentPos.y = v.pos.y
                showEditText(v)
            }
            btnDecCols -> {
                removeColumn()
                hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnIncCols -> {
                addColumn()
                hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnDecRows -> {
                removeRow()
                hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnIncRows -> {
                //addRow()
                addRow()
                hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnIncRowsCols ->{
                addRow()
                addColumn()
                hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnDecRowsCols ->{
                removeRow()
                removeColumn()
                hideEditText()
            }
        }
        if (v is ImageView){
            Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            tvNumRows.text = "$numRows"
            tvNumCols.text = "$numColumns"
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val leftPadding: Int = (measuredWidth - paddingLeft - paddingRight -
                numColumns * cellSize.toInt() - (numColumns - 1) * spacing -
                buttonThickness * 2) / 2
        val topPadding: Int = (measuredHeight - paddingTop - paddingBottom -
                numRows * cellSize.toInt() - (numRows - 1) * spacing - buttonThickness * 2) / 2
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                val left = paddingLeft + buttonThickness+ j * (cellSize.toInt() + spacing) + leftPadding
                val right = paddingLeft + (j + 1) * cellSize.toInt() + j * spacing + leftPadding +
                        buttonThickness
                val top: Int = paddingTop + i * (cellSize.toInt() + spacing) + topPadding +
                        buttonThickness
                val bottom: Int = paddingTop + (i + 1) * cellSize.toInt() + i * spacing +
                        topPadding + buttonThickness
                mFractionViews[i][j].setPadding(0, 0, 0, 0)
                mFractionViews[i][j].layout(left, top, right, bottom)
                mFractionViews[i][j].backColor = cellsColor
                mFractionViews[i][j].setOnClickListener(this)
                mFractionViews[i][j].mode = mode
                mFractionViews[i][j].pos.x = i
                mFractionViews[i][j].pos.y = j
            }
        }
        mEditText.gravity = Gravity.CENTER
        mEditText.setPadding(0, 0, 0, 0)
        //showEditText(mFractionViews[currentPos.x][currentPos.y])

        mEditText.layout(
            mFractionViews[currentPos.x][currentPos.y].left,
            mFractionViews[currentPos.x][currentPos.y].top,
            mFractionViews[currentPos.x][currentPos.y].right,
            mFractionViews[currentPos.x][currentPos.y].bottom
        )
        mEditText.clearFocus()
        btnDecCols.layout(
            ((measuredWidth - textViewWidth)/2) - buttonWidth,
            measuredHeight - buttonThickness,
            measuredWidth/2 - textViewWidth/2,
            measuredHeight
        )
        tvNumCols.layout(
            measuredWidth/2 - textViewWidth/2,
            measuredHeight - buttonThickness,
            measuredWidth/2 + textViewWidth/2,
            measuredHeight
        )
        btnIncCols.layout(
            measuredWidth/2 + textViewWidth/2,
            measuredHeight - buttonThickness,
            measuredWidth/2 + textViewWidth/2 + buttonWidth,
            measuredHeight
        )
        btnIncRows.layout(
            measuredWidth - buttonThickness,
            measuredHeight/2 - textViewWidth/2-buttonWidth,
            measuredWidth,
            measuredHeight/2 - textViewWidth/2
        )
        tvNumRows.layout(
            measuredWidth - buttonThickness,
            measuredHeight/2 - textViewWidth/2,
            measuredWidth,
            measuredHeight/2 + textViewWidth/2
        )
        btnDecRows.layout(
            measuredWidth - buttonThickness,
            measuredHeight/2 + textViewWidth/2,
            measuredWidth,
            measuredHeight/2 + textViewWidth/2 + buttonWidth
        )
        btnDecRowsCols.layout(
            0,
            measuredHeight - buttonThickness,
            buttonThickness,
            measuredHeight
        )
        btnIncRowsCols.layout(
            measuredWidth - buttonThickness,
            0,
            measuredWidth,
            buttonThickness
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val heightM: Int
        val widthM: Int
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            widthM = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
            heightM = min(
                widthMeasureSpec, View.getDefaultSize(
                    suggestedMinimumHeight,
                    heightMeasureSpec
                )
            )
        } else {
            heightM = View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
            widthM = heightMeasureSpec
        }
        buttonThickness = heightM / 12
        val cellHeight: Int = (heightM - spacing * (numRows - 1) - paddingTop -
                paddingBottom - buttonThickness * 2) / numRows
        val cellWidth: Int = (widthM - spacing * (numColumns - 1) - paddingLeft -
                paddingRight - buttonThickness * 2) / numColumns
        cellSize = min(cellHeight, cellWidth).toFloat()
        setMeasuredDimension(widthM, heightM)
    }
}