package com.haytham.coder.stepview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toRectF
import kotlin.math.roundToInt


class RectangularStepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object{
        private const val DEFAULT_STEPS_COUNT= 3
        private const val MINIMUM_STEPS_COUNT= 2
        private const val DEFAULT_HEIGHT_WIDTH_RATIO= 0.08f
    }

    private lateinit var shapeRect: RectF
    private lateinit var activeStepPath: Path

    private var _currentStep= 0
    set(value) {
        val previousValue= field
        field= value
        onStepChangedListener?.invoke(previousValue, value)
    }

    val currentStep get()= _currentStep
    var onStepChangedListener: ((previousStep:Int, currentStep:Int) -> Unit)? = null

    private var stepsCount= 0

    private val sectionPercentage get() =  1f/stepsCount
    private var strokeWidth= 0f


    private val shapePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val activeStepPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var heightToWidthRatio= 0f
    private var cornerRadius = 0f
    private var firstLineXPos= 0f

    fun incrementStep(){
        _currentStep = (_currentStep+1) % stepsCount
        updateActivePath()

    }

    fun decrementStep(){
        if(_currentStep-1 < 0) _currentStep= stepsCount-1
        else --_currentStep


        updateActivePath()
    }

    private fun updateActivePath() {
        calculateActivePath()
        invalidate()
    }


    fun gotoStep(step:Int){
        if(_currentStep == step) return

        _currentStep= step.coerceIn(0, stepsCount-1)
        updateActivePath()
    }

    init {


        context.withStyledAttributes(attrs, R.styleable.RectangularStepView) {
            shapePaint.color = getColor(R.styleable.RectangularStepView_borderColor, Color.BLACK)
            activeStepPaint.color = getColor(R.styleable.RectangularStepView_activeColor, Color.GREEN)

            strokeWidth= getDimension(
                R.styleable.RectangularStepView_stepViewStrokeWidth,
                resources.getDimension(R.dimen.RectangularStepViewStrokeWidth)
            ).coerceAtLeast(0f)
            shapePaint.strokeWidth= strokeWidth


            stepsCount= getInteger(R.styleable.RectangularStepView_stepsCount, DEFAULT_STEPS_COUNT).coerceAtLeast(MINIMUM_STEPS_COUNT)
            _currentStep= getInteger(R.styleable.RectangularStepView_initialStep, 0).coerceIn(0, stepsCount-1)

            heightToWidthRatio= getFloat(R.styleable.RectangularStepView_heightToWidthRatio, DEFAULT_HEIGHT_WIDTH_RATIO).coerceIn(0.01f, 0.2f)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cornerRadius= w/6f
        firstLineXPos= w * sectionPercentage

        initializeRect()
        calculateActivePath()
    }

    private fun initializeRect() {
        shapeRect = Rect(0, 0, width, height).toRectF().apply {
            // inset the rect by half stroke width
            val offset = strokeWidth / 2
            inset(offset, offset)
        }
    }

    private fun calculateActivePath() {
        activeStepPath = Path().apply {

            // 1- setup corner radius
            val radiusArr = when(_currentStep){

                0 -> floatArrayOf(
                    cornerRadius , cornerRadius,
                    0f, 0f,
                    0f, 0f,
                    cornerRadius, cornerRadius
                )

                stepsCount-1 -> floatArrayOf(
                    0f, 0f,
                    cornerRadius, cornerRadius,
                    cornerRadius, cornerRadius,
                    0f, 0f
                )

                else -> floatArrayOf(
                    0f, 0f,
                    0f, 0f,
                    0f, 0f,
                    0f, 0f
                )
            }

            val activeRect = shapeRect.run {

                RectF(
                    right * (sectionPercentage * _currentStep)
                    , top,
                    right * sectionPercentage * (_currentStep + 1),
                    bottom
                )
            }

            addRoundRect(
                activeRect,
                radiusArr,
                Path.Direction.CW
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val oldWidth= MeasureSpec.getSize(widthMeasureSpec)


        val height= (heightToWidthRatio * oldWidth).roundToInt()
        setMeasuredDimension(oldWidth, height)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            canvas.drawPath(activeStepPath, activeStepPaint)

            // draw border
            drawRoundRect(shapeRect, cornerRadius, cornerRadius, shapePaint)

            val fHeight= height.toFloat()

            for(step in 1 until stepsCount){
                val xPos= firstLineXPos * step
                drawLine(xPos, 0f, xPos, fHeight, shapePaint)
            }
        }
    }

}