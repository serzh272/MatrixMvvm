package ru.serzh272.matrixmvvm.views

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import ru.serzh272.matrix.*
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.extensions.dpToPx
import ru.serzh272.matrixmvvm.utils.Matrix
import kotlin.math.min

@ExperimentalUnsignedTypes
class MatrixViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), View.OnClickListener {
    companion object {
        private const val DEFAULT_BUTTON_THICKNESS = 32
        private const val DEFAULT_BUTTON_WIDTH = 32
        private const val DEFAULT_TEXTVIEW_WIDTH = 72
        private const val DEFAULT_SPACING = 4
        private const val MAX_DIMENSION = 10
        private const val MIN_DIMENSION = 1
    }

    private var innerPadding: Int = context.dpToPx(DEFAULT_SPACING).toInt()
    var mMatrix = Matrix()

    @ExperimentalUnsignedTypes
    var numRows: Int = mMatrix.numRows

    @ExperimentalUnsignedTypes
    var numColumns: Int = mMatrix.numColumns
    var mode = 1
    var spacing: Int = context.dpToPx(DEFAULT_SPACING).toInt()
    private var buttonThickness: Int = context.dpToPx(DEFAULT_BUTTON_THICKNESS).toInt()
    private var buttonWidth: Int = context.dpToPx(DEFAULT_BUTTON_WIDTH).toInt()
    private var textViewWidth: Int = context.dpToPx(DEFAULT_TEXTVIEW_WIDTH).toInt()
    private var cellSize: Float = 50.0f
    var cellsColor: Int = Color.WHITE
    private var btnDecCols: MaterialButton = MaterialButton(context)
    private var btnIncRows: MaterialButton = MaterialButton(context)
    private var btnIncCols: MaterialButton = MaterialButton(context)
    private var btnDecRows: MaterialButton = MaterialButton(context)
    private var btnIncRowsCols: MaterialButton = MaterialButton(context)
    private var btnDecRowsCols: MaterialButton = MaterialButton(context)
    private var mEditText: EditText = EditText(context)
    private var currentPos: Point = Point(0, 0)
    private var tvNumRows: MaterialTextView = MaterialTextView(context)
    private var tvNumCols: MaterialTextView = MaterialTextView(context)

    @ExperimentalUnsignedTypes
    private var mFractionViews: MutableList<MutableList<FractionView>> = MutableList(numRows) {
        MutableList(numColumns) { FractionView(context) }
    }

    init {
        initAttrs(context, attrs)
        mFractionViews = MutableList(numRows) { MutableList(numColumns) { FractionView(context) } }
        with(btnDecRows) {
            setIconResource(R.drawable.ic_remove)
            this.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            setBackgroundResource(R.drawable.button_bg)

            addView(this)

            //setBackgroundColor(ContextCompat.getColor(context, R.color.color_primary))
        }
        btnDecRows.setOnClickListener(this)
        with(btnIncRows) {
            setIconResource(R.drawable.ic_add)
            setBackgroundResource(R.drawable.button_bg)
            addView(this)
            //setBackgroundColor(ContextCompat.getColor(context, R.color.color_primary))
        }
        btnIncRows.setOnClickListener(this)
        with(btnDecCols) {
            setIconResource(R.drawable.ic_remove)
            setBackgroundResource(R.drawable.button_bg)
            addView(this)
            //setBackgroundColor(ContextCompat.getColor(context, R.color.color_primary))
        }
        btnDecCols.setOnClickListener(this)
        with(btnIncCols) {
            setIconResource(R.drawable.ic_add)
            setBackgroundResource(R.drawable.button_bg)
            addView(this)
            //setBackgroundColor(ContextCompat.getColor(context, R.color.color_primary))
        }
        btnIncCols.setOnClickListener(this)
        with(btnIncRowsCols) {
            setIconResource(R.drawable.ic_add)
            setBackgroundResource(R.drawable.button_bg)
            addView(this)
            //setBackgroundColor(ContextCompat.getColor(context, R.color.color_primary))
        }
        btnIncRowsCols.setOnClickListener(this)
        with(btnDecRowsCols) {
            setIconResource(R.drawable.ic_remove)
            setBackgroundResource(R.drawable.button_bg)
            addView(this)
            //setBackgroundColor(ContextCompat.getColor(context, R.color.color_primary))
        }
        btnDecRowsCols.setOnClickListener(this)
        with(tvNumCols) {
            addView(this)
            maxLines = 1
            textSize = 24f
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            text = "$numColumns"
        }
        with(tvNumRows) {
            addView(this)
            maxLines = 1
            textSize = 24f
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            text = "$numRows"

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
    fun showAlertInputDialog(frV:FractionView):String{
        val textInpLt = TextInputLayout(context)
        val input = EditText(context)
        input.setText(frV.toString())
        textInpLt.hint = "Элемент (${frV.pos.x};${frV.pos.y})"
        textInpLt.addView(input)
        val alert = AlertDialog.Builder(context)
            .setTitle("Введите число")
            .setView(textInpLt)
            .setPositiveButton("Ok"){dialog, _ ->
                frV.mFraction.setFromString(input.text.toString())
                frV.invalidate()
                dialog.cancel()
            }
            .create()
        alert.show()
        return input.text.toString()
    }

    @ExperimentalUnsignedTypes
    override fun onClick(v: View?) {
        when (v) {
            is FractionView -> {
                currentPos.x = v.pos.x
                currentPos.y = v.pos.y
                showAlertInputDialog(v)
                //v.invalidate()

                //showEditText(v)

            }
            btnDecCols -> {
                if (numColumns > MIN_DIMENSION) {
                    removeColumn()
                }
                //hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnIncCols -> {
                if (numColumns < MAX_DIMENSION) {
                    addColumn()
                }
                //hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnDecRows -> {
                if (numRows > MIN_DIMENSION) {
                    removeRow()
                }
                //hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnIncRows -> {
                //addRow()
                if (numRows < MAX_DIMENSION) {
                    addRow()
                }
                //hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnIncRowsCols -> {
                if (numRows < MAX_DIMENSION && numColumns < MAX_DIMENSION) {
                    addRow()
                    addColumn()
                }
                //hideEditText()
                //Toast.makeText(context, "$numRows x $numColumns", Toast.LENGTH_SHORT).show()
            }
            btnDecRowsCols -> {
                if (numRows > MIN_DIMENSION && numColumns > MIN_DIMENSION) {
                    removeRow()
                    removeColumn()
                }
                //hideEditText()
            }
        }
        if (v is MaterialButton && v !is FractionView ) {
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
            ((measuredWidth - textViewWidth) / 2) - buttonWidth,
            measuredHeight - buttonThickness - paddingBottom,
            measuredWidth / 2 - textViewWidth / 2,
            measuredHeight - paddingBottom
        )
        tvNumCols.layout(
            measuredWidth / 2 - tvNumCols.measuredWidth / 2,
            measuredHeight - buttonThickness / 2 - tvNumCols.measuredHeight / 2 - paddingBottom,
            measuredWidth / 2 + tvNumCols.measuredWidth / 2,
            measuredHeight - buttonThickness / 2 + tvNumCols.measuredHeight / 2 - paddingBottom
        )
        btnIncCols.layout(
            measuredWidth / 2 + textViewWidth / 2,
            measuredHeight - buttonThickness - paddingBottom,
            measuredWidth / 2 + textViewWidth / 2 + buttonWidth,
            measuredHeight - paddingBottom
        )
        btnIncRows.layout(
            measuredWidth - buttonThickness - paddingRight,
            measuredHeight / 2 - textViewWidth / 2 - buttonWidth,
            measuredWidth - paddingRight,
            measuredHeight / 2 - textViewWidth / 2
        )
        tvNumRows.layout(
            measuredWidth - buttonThickness / 2 - paddingRight - tvNumRows.measuredWidth / 2,
            measuredHeight / 2 - tvNumRows.measuredHeight / 2,
            measuredWidth - buttonThickness / 2 - paddingRight + tvNumRows.measuredWidth / 2,
            measuredHeight / 2 + tvNumRows.measuredHeight / 2
        )
        btnDecRows.layout(
            measuredWidth - buttonThickness - paddingRight,
            measuredHeight / 2 + textViewWidth / 2,
            measuredWidth - paddingRight,
            measuredHeight / 2 + textViewWidth / 2 + buttonWidth
        )
        btnDecRowsCols.layout(
            paddingLeft,
            measuredHeight - buttonThickness - paddingBottom,
            buttonThickness + paddingLeft,
            measuredHeight - paddingBottom
        )
        btnIncRowsCols.layout(
            measuredWidth - buttonThickness - paddingRight,
            paddingTop,
            measuredWidth - paddingRight,
            buttonThickness + paddingTop
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
        val cellHeight: Int = (heightM - spacing * (numRows - 1) - 2 * innerPadding - paddingTop -
                paddingBottom - buttonThickness * 2) / numRows
        val cellWidth: Int = (widthM - spacing * (numColumns - 1) - 2 * innerPadding - paddingLeft -
                paddingRight - buttonThickness * 2) / numColumns
        cellSize = min(cellHeight, cellWidth).toFloat()
        measureChild(tvNumRows, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvNumCols, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthM, heightM)
    }


}