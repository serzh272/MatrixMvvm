package ru.serzh272.matrixmvvm.views

import android.animation.LayoutTransition
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import ru.serzh272.matrixmvvm.utils.Fraction
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.extensions.dpToPx
import ru.serzh272.matrixmvvm.utils.Matrix
import kotlin.math.min

@ExperimentalUnsignedTypes
class MatrixViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {
    companion object {
        private const val DEFAULT_BUTTON_THICKNESS = 32
        private const val DEFAULT_BUTTON_WIDTH = 144
        private const val DEFAULT_TEXTVIEW_WIDTH = 72
        private const val DEFAULT_SPACING = 4
        private const val MAX_DIMENSION = 10
        private const val MIN_DIMENSION = 1
    }

    var precision: Int = 2
    private var detector: GestureDetector = GestureDetector(context, MyGestureDetector())
    private var initSize: Int = 0
    private var innerPadding: Int = context.dpToPx(DEFAULT_SPACING).toInt()
    var frMode = Fraction.FractionType.DECIMAL
    var matrix = Matrix()
        get() {
            val field = Matrix(this.numRows, this.numColumns)
            for (r in 0 until field.numRows) {
                for (c in 0 until field.numColumns) {
                    if (this.mFractionViews[r][c].mFraction != field[r, c]) {
                        field[r, c] = this.mFractionViews[r][c].mFraction
                    }
                }
            }
            return field
        }
        set(value) {
            field = value
            if (field.numRows > this.numRows) {
                for (i in 0 until field.numRows - this.numRows) {
                    this.addRow()
                }
            } else if (field.numRows < this.numRows) {
                for (i in 0 until this.numRows - field.numRows) {
                    this.removeRow()
                }
            }
            if (field.numColumns > this.numColumns) {
                for (i in 0 until field.numColumns - this.numColumns) {
                    this.addColumn()
                }
            } else if (field.numColumns < this.numColumns) {
                for (i in 0 until this.numColumns - field.numColumns) {
                    this.removeColumn()
                }
            }
            for (r in 0 until field.numRows) {
                for (c in 0 until field.numColumns) {
                    if (this.mFractionViews[r][c].mFraction != field[r, c]) {
                        this.mFractionViews[r][c].mFraction = field[r, c]
                    }
                }
            }
            tvNumRows.text = "${this.numRows}"
            tvNumCols.text = "${this.numColumns}"
        }
    var numRows: Int = 3
    var numColumns: Int = 3
    var mode = 1
    var listener: OnDataChangedListener? = null
    var spacing: Int = context.dpToPx(DEFAULT_SPACING).toInt()
    var lPadding = 0
    private var buttonThickness: Int = context.dpToPx(DEFAULT_BUTTON_THICKNESS).toInt()
    private var buttonWidth: Int = context.dpToPx(DEFAULT_BUTTON_WIDTH).toInt()
    private var textViewWidth: Int = context.dpToPx(DEFAULT_TEXTVIEW_WIDTH).toInt()
    private var cellSize: Float = 50.0f
    var cellsColor: Int = Color.WHITE
    private var btnDecCols: MaterialButton = MaterialButton(context)
    private var btnDecRows: MaterialButton = MaterialButton(context)
    private var btnIncCols: MaterialButton = MaterialButton(context)
    private var btnIncRows: MaterialButton = MaterialButton(context)
    private var btnIncRowsCols: MaterialButton = MaterialButton(context)
    private var btnDecRowsCols: MaterialButton = MaterialButton(context)
    private var tvNumRows: MaterialTextView = MaterialTextView(context)
    private var tvNumCols: MaterialTextView = MaterialTextView(context)

    @ExperimentalUnsignedTypes
    private var mFractionViews: MutableList<MutableList<FractionView>> = MutableList(numRows) {
        MutableList(numColumns) { FractionView(context) }
    }

    init {
        val ltTrans = LayoutTransition()
        ltTrans.setDuration(LayoutTransition.DISAPPEARING, 100)
        layoutTransition = ltTrans
        initAttrs(context, attrs)
        mFractionViews = MutableList(numRows) { MutableList(numColumns) { FractionView(context) } }
        with(btnIncRowsCols) {
            this.setBackgroundResource(R.drawable.ripple_right_bottom_bg)
            addView(this)
        }
        btnIncRowsCols.setOnClickListener(this)
        with(btnDecRowsCols) {
            this.setBackgroundResource(R.drawable.ripple_left_top_bg)
            //this.background = resources.getDrawable(R.attr.selectableItemBackground, context.theme)
            this.isClickable = true
            addView(this)
        }
        btnDecRowsCols.setOnClickListener(this)
        with(btnIncRows) {
            this.setBackgroundResource(R.drawable.ripple_down_bg)
            addView(this)
        }
        btnIncRows.setOnClickListener(this)
        with(btnDecRows) {
            this.setBackgroundResource(R.drawable.ripple_up_bg)
            addView(this)
        }
        btnDecRows.setOnClickListener(this)
        with(btnDecCols) {
            this.setBackgroundResource(R.drawable.ripple_left_bg)
            addView(this)
        }
        btnDecCols.setOnClickListener(this)
        with(btnIncCols) {
            this.setBackgroundResource(R.drawable.ripple_right_bg)
            addView(this)
        }
        btnIncCols.setOnClickListener(this)

        with(tvNumCols) {
            //addView(this)
            this.maxLines = 1
            this.textSize = 24f
            this.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            this.gravity = Gravity.CENTER
            this.text = "$numColumns"
        }
        with(tvNumRows) {
            //addView(this)
            this.maxLines = 1
            this.textSize = 24f
            this.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            this.gravity = Gravity.CENTER
            this.text = "$numRows"

        }
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                mFractionViews[i][j].mFraction = matrix[i, j]
                mFractionViews[i][j].setOnClickListener(this)
                addView(mFractionViews[i][j])
            }
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MatrixLayout, 0, 0
        )
        try {
            numRows = a.getInt(R.styleable.MatrixLayout_rows, 3)
            numColumns = a.getInt(R.styleable.MatrixLayout_columns, 3)
            spacing = a.getDimension(
                R.styleable.MatrixLayout_spacing,
                context.dpToPx(DEFAULT_SPACING)
            ).toInt()
            buttonThickness = a.getDimension(
                R.styleable.MatrixLayout_buttons_thickness,
                context.dpToPx(DEFAULT_BUTTON_THICKNESS)
            ).toInt()
            cellsColor = a.getColor(
                R.styleable.MatrixLayout_cells_color,
                ContextCompat.getColor(context, R.color.color_secondary)
            )
        } finally {
            a.recycle()
        }
    }
    /**
     * Inserts a row into the [MatrixViewGroup] at the specified [pos].
     */
    private fun addRow(pos: Int) {
        this.mFractionViews.add(pos, MutableList(this.numColumns) {
            FractionView(context)
        })
        for (j in 0 until numColumns) {
            mFractionViews[pos][j].mFraction = Fraction()
            addView(mFractionViews[pos][j])
            mFractionViews[pos][j].setOnClickListener(this)

        }
        this.numRows++
    }

    /**
     * Adds the row to the end of this [MatrixViewGroup].
     */
    private fun addRow() {
        this.mFractionViews.add(MutableList(this.numColumns) {
            FractionView(context)
        })
        for (j in 0 until numColumns) {
            mFractionViews[numRows][j].mFraction = Fraction()
            addView(mFractionViews[numRows][j])
            mFractionViews[numRows][j].setOnClickListener(this)
        }
        this.numRows++
    }

    /**
     * Inserts a column into the [MatrixViewGroup] at the specified [pos].
     */
    @ExperimentalUnsignedTypes
    fun addColumn(pos: Int) {
        for (i in 0 until this.numRows) {
            this.mFractionViews[i].add(
                pos,
                FractionView(context)
            )
            addView(mFractionViews[i][pos])
            mFractionViews[i][pos].setOnClickListener(this)
        }
        this.numColumns++
    }

    /**
     * Adds the column to the end of this [MatrixViewGroup].
     */
    @ExperimentalUnsignedTypes
    fun addColumn() {
        for (i in 0 until this.numRows) {
            this.mFractionViews[i].add(
                FractionView(context)
            )
            addView(mFractionViews[i][numColumns])
            mFractionViews[i][numColumns].setOnClickListener(this)
        }
        this.numColumns++
    }

    /**
     * Removes a row at the specified [pos] from the [MatrixViewGroup].
     */
    private fun removeRowAt(pos: Int) {
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

    /**
     * Removes a column at the specified [pos] from the [MatrixViewGroup].
     */
    private fun removeColumnAt(pos: Int) {
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

    /**
     * Adds the [n] rows to the specified [pos] of this [MatrixViewGroup].
     */
    fun addRows(pos: Int, n: Int) {
        for (i in 1..n) {
            this.addRow(pos + i - 1)
        }
    }

    /**
     * Adds the [n] columns to the specified [pos] of this [MatrixViewGroup].
     */
    fun addColumns(pos: Int, n: Int) {
        for (i in 1..n) {
            this.addColumn(pos + i - 1)
        }
    }

    /**
     * Removes the row from the end of this [MatrixViewGroup].
     */
    private fun removeRow() {
        removeRowAt(numRows - 1)

    }

    /**
     * Removes the column from the end of this [MatrixViewGroup].
     */
    private fun removeColumn() {
        removeColumnAt(numColumns - 1)
    }

    operator fun get(r: Int, c: Int): Fraction {
        return this.mFractionViews[r][c].mFraction
    }

    operator fun set(r: Int, c: Int, fr: Fraction) {
        matrix[r, c] = fr.copy()
        this.mFractionViews[r][c].mFraction = matrix[r, c]
    }


    /**
     * Generates [AlertDialog] for setting current [FractionView] value and shows it.
     */
    private fun showAlertInputDialog(frV: FractionView) {
        val textInpLt = inflate(context, R.layout.item_input_dialog, null) as TextInputLayout
        val input = textInpLt.editText
//        input?.inputType = InputType.TYPE_NULL
//        input?.inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input?.imeOptions = EditorInfo.IME_ACTION_DONE
        input?.setText(frV.toString())
        input?.selectAll()
        textInpLt.setPadding(
            context.dpToPx(18).toInt(),
            0,
            context.dpToPx(18).toInt(),
            0
        )
        textInpLt.hint = "Элемент (${frV.pos.x + 1};${frV.pos.y + 1})"
        textInpLt.isHintAnimationEnabled = true
        val alert = AlertDialog.Builder(context)
            .setTitle("Введите значение")
            .setView(textInpLt)
            .setPositiveButton("Ok") { dialog, _ ->
                val fr = Fraction(input?.text.toString())
                frV.mFraction.numerator = fr.numerator
                frV.mFraction.denominator = fr.denominator
                frV.invalidate()
                dialog.cancel()
                listener?.onDataChanged(matrix)
            }
            .create()
        input?.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (v is TextInputEditText) {
                        val fr = Fraction(v.text.toString())
                        frV.mFraction.numerator = fr.numerator
                        frV.mFraction.denominator = fr.denominator
                        frV.invalidate()
                        alert.cancel()
                        listener?.onDataChanged(matrix)
                    }
                    false
                }
                else -> false
            }
        }
        alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alert.show()
        input?.requestFocus()
    }

    /**
     * Generates [AlertDialog] for setting current [FractionView] value and shows it.
     */
    private fun clickHandler(v:View?){

            when (v) {
                is FractionView -> {
                    showAlertInputDialog(v)
                }
                btnDecCols -> {
                    if (numColumns > MIN_DIMENSION) {
                        removeColumn()
                        listener?.onDataChanged(matrix)
                    }
                }
                btnIncCols -> {
                    if (numColumns < MAX_DIMENSION) {
                        addColumn()
                        listener?.onDataChanged(matrix)
                    }
                }
                btnDecRows -> {
                    if (numRows > MIN_DIMENSION) {
                        removeRow()
                        listener?.onDataChanged(matrix)

                    }
                }
                btnIncRows -> {
                    //addRow()
                    if (numRows < MAX_DIMENSION) {
                        addRow()
                        listener?.onDataChanged(matrix)
                    }
                }
                btnIncRowsCols -> {
                    if (numRows < MAX_DIMENSION && numColumns < MAX_DIMENSION) {
                        addRow()
                        addColumn()
                        listener?.onDataChanged(matrix)
                    }
                }
                btnDecRowsCols -> {
                    if (numRows > MIN_DIMENSION && numColumns > MIN_DIMENSION) {
                        removeRow()
                        removeColumn()
                        listener?.onDataChanged(matrix)
                    }
                }
            }
            if (v is MaterialButton && v !is FractionView) {
                tvNumRows.text = "$numRows"
                tvNumCols.text = "$numColumns"
            }

    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        buttonWidth = initSize / 3
        val leftPadding: Int = (initSize - paddingLeft - paddingRight -
                numColumns * cellSize.toInt() - (numColumns - 1) * spacing -
                buttonThickness * 2) / 2 + lPadding
        val topPadding: Int = (measuredHeight - paddingTop - paddingBottom -
                numRows * cellSize.toInt() - (numRows - 1) * spacing - buttonThickness * 2) / 2
        btnDecCols.layout(
            lPadding,
            measuredHeight / 2 - buttonWidth / 2 ,
            buttonThickness + lPadding,
            measuredHeight / 2 + buttonWidth / 2
        )
        btnDecCols.z = 1.5f
        btnIncCols.layout(
            initSize - buttonThickness + lPadding,
            measuredHeight / 2 - buttonWidth / 2,
            initSize + lPadding,
            measuredHeight / 2 + buttonWidth / 2
        )
        btnIncCols.z = 1.5f
        btnDecRows.layout(
            initSize / 2 - buttonWidth / 2 + lPadding,
            0,
            initSize / 2 + buttonWidth / 2 + lPadding,
            buttonThickness
        )
        btnDecRows.z = 1.5f
        btnIncRows.layout(
            initSize / 2 - buttonWidth / 2 + lPadding,
            measuredHeight - buttonThickness,
            initSize / 2 + buttonWidth / 2 + lPadding,
            measuredHeight
        )
        btnIncRows.z = 1.5f
        btnDecRowsCols.layout(
            lPadding,
            0,
            buttonThickness * 3 + lPadding,
            buttonThickness * 3
        )
        btnDecRowsCols.elevation = 1.5f
        btnIncRowsCols.layout(
            initSize - buttonThickness * 3 + lPadding,
            measuredHeight - buttonThickness * 3,
            initSize + lPadding,
            measuredHeight
        )
        btnIncRowsCols.elevation = 1.5f
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                val left =
                    paddingLeft + buttonThickness + j * (cellSize.toInt() + spacing) + leftPadding
                val right = paddingLeft + (j + 1) * cellSize.toInt() + j * spacing + leftPadding +
                        buttonThickness
                val top: Int = paddingTop + i * (cellSize.toInt() + spacing) + topPadding +
                        buttonThickness
                val bottom: Int = paddingTop + (i + 1) * cellSize.toInt() + i * spacing +
                        topPadding + buttonThickness
                mFractionViews[i][j].setPadding(0, 0, 0, 0)
                mFractionViews[i][j].layout(left, top, right, bottom)
                mFractionViews[i][j].mode = frMode
                mFractionViews[i][j].precision = precision
                mFractionViews[i][j].pos.x = i
                mFractionViews[i][j].pos.y = j
                mFractionViews[i][j].elevation = 5.0f
            }
        }
    }

    /**
     * Resolve size for this [MatrixViewGroup].
     */
    private fun resolveSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpToPx(200).toInt()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        initSize = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            resolveSize(widthMeasureSpec)
        } else {
            resolveSize(heightMeasureSpec)
        }
        val cellHeight: Int = (initSize - spacing * (numRows - 1) - 2 * innerPadding - paddingTop -
                paddingBottom - buttonThickness * 2) / numRows
        val cellWidth: Int =
            (initSize - spacing * (numColumns - 1) - 2 * innerPadding - paddingLeft -
                    paddingRight - buttonThickness * 2) / numColumns
        cellSize = min(cellHeight, cellWidth).toFloat()
        buttonThickness = initSize / 12
        lPadding = resolveSize(widthMeasureSpec) / 2 - initSize / 2
        setMeasuredDimension(widthMeasureSpec, initSize)
    }

    interface OnDataChangedListener {
        fun onDataChanged(matrix: Matrix)
    }

    fun setOnDataChangedListener(listener: OnDataChangedListener) {
        this.listener = listener
    }

    inner class MyGestureDetector: GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }
    }

    override fun onClick(v: View?) {
        clickHandler(v)
    }
}