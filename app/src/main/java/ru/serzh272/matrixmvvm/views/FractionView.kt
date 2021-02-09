package ru.serzh272.matrixmvvm.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import ru.serzh272.matrix.Fraction
import ru.serzh272.matrixmvvm.R
import ru.serzh272.matrixmvvm.extensions.dpToPx
import ru.serzh272.matrixmvvm.utils.MotionEventsDebugger
import kotlin.math.abs
import kotlin.math.max

@ExperimentalUnsignedTypes
class FractionView @JvmOverloads constructor(context: Context,
                   attrs: AttributeSet? = null,
                   defStyleAttr: Int = 0
): MaterialButton(context,attrs, defStyleAttr){
    var fractionTextColor: Int = Color.WHITE
    var backColor: Int = Color.LTGRAY
    var mode: Int = 2
    var size:Int = 0
    var mFraction = Fraction(2, 3u)
    private var sp:Float = 0.0f
    private var anchor: Point = Point(0, 0)
    var pos:Point = Point()
    init {
        initAttrs(context,attrs)
        setBackgroundResource(R.drawable.button_bg)
    }

    private fun initAttrs(context: Context?, attrs: AttributeSet?){
        val a:TypedArray = context!!.theme.obtainStyledAttributes(attrs,
            R.styleable.FractionViewLayout, 0,0)
        try {
            mFraction.numerator = a.getInteger(R.styleable.FractionViewLayout_numerator, 0)
            mFraction.denominator = a.getInteger(R.styleable.FractionViewLayout_denominator, 1).toUInt()
            mFraction.normalize()
            //presenter.mFraction.integ = a.getInteger(R.styleable.FractionViewLayout_integ, 0).toLong()
            mode = a.getInteger(R.styleable.FractionViewLayout_mode, 1)
            backColor = a.getColor(R.styleable.FractionViewLayout_color, Color.WHITE)
            fractionTextColor = a.getColor(R.styleable.FractionViewLayout_text_color, MaterialColors.getColor(context, R.attr.colorOnPrimary, Color.WHITE))
            sp = a.getDimension(R.styleable.FractionViewLayout_fraction_space, 2.0f)
        }
        finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        showFraction(canvas!!, mFraction)
    }


    fun showFraction() {
        invalidate()
    }

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
        //canvas.drawRect(0.0f, 0.0f, this.width.toFloat(), this.height.toFloat(),p)
        p.setColor(fractionTextColor)
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

            }else{
                integTxt = if (numerator < 0) "-" else ""
            }
            p.getTextBounds(integTxt, 0, integTxt.length, bndInteg)
            frSpan = bndInteg.width().toFloat()/2 +sp
            integSpan = wdt/2 +sp
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
            //if (integ != 0){
                canvas.drawText(integTxt,anchor.x.toFloat() -integSpan,anchor.y.toFloat()+textHeight/2, p)
            //}
            canvas.drawText(abs(numerator).toString(),anchor.x.toFloat() + frSpan,anchor.y.toFloat()-sp, p)
            canvas.drawText(denominator.toString(),anchor.x.toFloat() + frSpan,anchor.y.toFloat()+textHeight+sp, p)
            p.strokeWidth = context.dpToPx(2)
            canvas.drawLine(anchor.x.toFloat() - wdt/2+frSpan, anchor.y.toFloat(), anchor.x.toFloat() + wdt/2 + frSpan, anchor.y.toFloat(),p)
        }
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        MotionEventsDebugger.debugPrint("onTouchEvent","FractionView", event)
//        return MotionEventsDebugger.debugReturnPrint("onTouchEvent", "FractionView", super.onTouchEvent(event))
//    }

//    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
//        MotionEventsDebugger.debugPrint("dispatchTouchEvent","FractionView", event)
//        return MotionEventsDebugger.debugReturnPrint("onTouchEvent", "FractionView", super.dispatchTouchEvent(event))
//    }

    override fun toString(): String {
        return mFraction.toString()
    }

//    override fun performClick(): Boolean {
//        super.performClick()
//        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
//        return true
//    }
}