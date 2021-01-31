package ru.serzh272.matrixmvvm.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.view.marginTop
import com.google.android.material.button.MaterialButton
import ru.serzh272.matrix.Fraction
import ru.serzh272.matrixmvvm.R
import kotlin.math.max

class FractionView: View{
    var backColor: Int = Color.LTGRAY
    var mode: Int = 2
    @ExperimentalUnsignedTypes
    var mFraction = Fraction(2, 3u)
    private var sp:Float = 0.0f
    private var anchor: Point = Point(0, 0)
    var pos:Point = Point()
    @ExperimentalUnsignedTypes
    constructor(context: Context?): this(context, null){
    }
    @ExperimentalUnsignedTypes
    constructor(context: Context?, attrs: AttributeSet?): this(context, attrs, 0)
    @ExperimentalUnsignedTypes
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context!!, attrs, defStyleAttr){
        initAttrs(context,attrs)
    }
    @ExperimentalUnsignedTypes
    private fun initAttrs(context: Context?, attrs: AttributeSet?){
        val a:TypedArray = context!!.theme.obtainStyledAttributes(attrs,
            R.styleable.FractionViewLayout, 0,0)
        try {
            mFraction.numerator = a.getInteger(R.styleable.FractionViewLayout_numerator, 27)
            mFraction.denominator = a.getInteger(R.styleable.FractionViewLayout_denominator, 3).toUInt()
            mFraction.normalize()
            //presenter.mFraction.integ = a.getInteger(R.styleable.FractionViewLayout_integ, 0).toLong()
            mode = a.getInteger(R.styleable.FractionViewLayout_mode, 1)
            backColor = a.getColor(R.styleable.FractionViewLayout_color, Color.WHITE)
            sp = a.getDimension(R.styleable.FractionViewLayout_fraction_space, 2.0f)
        }
        finally {
            a.recycle()
        }
    }

    @ExperimentalUnsignedTypes
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        showFraction(canvas!!, mFraction)
    }

    fun showFraction() {
        invalidate()
    }

    @ExperimentalUnsignedTypes
    fun showFraction(canvas: Canvas, fraction: Fraction) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 0.0f
        p.textSize = this.height.toFloat()/3
        p.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC)
        p.textAlign = Paint.Align.CENTER
        anchor.x = this.width/2
        anchor.y = this.height/2
        var txt:String
        var integTxt = ""
        val bndNum = Rect()
        val bndDen = Rect()
        var textHeight:Float
        val bndInteg = Rect(0,0,0,0)
        var frSpan = 0.0f
        var integSpan = 0.0f
        p.style = Paint.Style.FILL
        p.color = backColor
        canvas.drawRect(0.0f, 0.0f, this.width.toFloat(), this.height.toFloat(),p)
        p.setColor(Color.BLUE)
        if ((fraction.denominator == 1u) or (mode == 3)){
            txt = if (mode == 3){
                if (fraction.numerator%fraction.denominator.toLong() == 0L){
                    this.toString()
                }
                else{
                    "%.3f".format(fraction.toDouble())
                }
            }
            else{
                fraction.numerator.toString()
            }
            p.getTextBounds(txt,0, txt.length, bndNum)
            if (bndNum.width()> (this.width - sp)){
                p.textSize *= ((this.width.toFloat()-sp)/bndNum.width().toFloat())
                p.getTextBounds(txt,0, txt.length, bndNum)
            }
            textHeight = bndNum.height().toFloat()
            canvas.drawText(txt,anchor.x.toFloat(),anchor.y.toFloat()+textHeight/2, p)
        }
        else{
            var (integ, numerator, denominator) = Triple(0, fraction.numerator, fraction.denominator)
            if (mode == 2){
                val frVals = fraction.getValues()
                integ = frVals.first
                numerator = frVals.second
                denominator = frVals.third

            }
            txt = numerator.toString()
            p.getTextBounds(txt,0, txt.length, bndNum)
            textHeight = bndNum.height().toFloat()
            txt = denominator.toString()
            p.getTextBounds(txt,0, txt.length, bndDen)
            sp = bndDen.height().toFloat()/5
            var wdt:Float = max(bndNum.width().toFloat(), bndDen.width().toFloat())
            if (integ != 0){
                integTxt = integ.toString()
                p.getTextBounds(integTxt, 0, integTxt.length, bndInteg)
                frSpan = bndInteg.width().toFloat()/2 +sp
                integSpan = wdt/2 +sp
            }
            if ((bndInteg.width() + wdt + sp*8) > this.width){
                val k:Float = (this.width.toFloat())/(bndInteg.width().toFloat() + wdt + sp*8)
                p.textSize *= k
                wdt *= k
                frSpan *= k
                integSpan *= k
                textHeight *= k
                sp *= k
                p.getTextBounds(integTxt, 0, integTxt.length, bndInteg)
            }
            if (integ != 0){
                canvas.drawText(integTxt,anchor.x.toFloat() -integSpan,anchor.y.toFloat()+textHeight/2, p)
            }
            canvas.drawText(numerator.toString(),anchor.x.toFloat() + frSpan,anchor.y.toFloat()-sp, p)
            canvas.drawText(denominator.toString(),anchor.x.toFloat() + frSpan,anchor.y.toFloat()+textHeight+sp, p)
            p.strokeWidth = 3.0f
            canvas.drawLine(anchor.x.toFloat() - wdt/2+frSpan, anchor.y.toFloat(), anchor.x.toFloat() + wdt/2 + frSpan, anchor.y.toFloat(),p)
        }
    }

    @ExperimentalUnsignedTypes
    override fun toString(): String {
        return mFraction.toString()
    }

}