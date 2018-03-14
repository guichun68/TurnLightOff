package com.go.light.turnlightoff

/**
 * Created by caorb1 on 2018/3/14.
 * Desc:
 */

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by Austin on 2017-09-10.
 * 电灯View
 */

class LightKT : View {
    private var rowIndex: Int = 0
    private var columnIndex: Int=0//当前灯泡在棋盘中的坐标
    //是否亮灯，默认熄灭状态
    var isLightOn: Boolean = false
    private var mPaint: Paint? = null
    var mOnClickListener: onLightClickListener? = null

    /**
     * 是否是标记状态（如是，则显示黄色圆圈标记）
     */
    private var isFlagStatus: Boolean = false

    constructor(context: Context, rowIndex: Int, columnIndex: Int) : super(context) {
        this.rowIndex = rowIndex
        this.columnIndex = columnIndex
        mPaint = Paint()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> if (x + left < right && y + top < bottom) {
                //在本控件内
                isLightOn = !isLightOn
                postInvalidate()
                if (mOnClickListener != null) {
                    mOnClickListener!!.onLightClicked(rowIndex, columnIndex)
                }
            }
        }
        return true
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        /**
         * 获得我们所定义的自定义样式属性
         */
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomView, defStyleAttr, 0)
        val n = a.indexCount
        for (i in 0 until n) {
            val attr = a.getIndex(i)
            when (attr) {
                R.styleable.CustomView_isChecked -> isLightOn = a.getBoolean(attr, false)
            }

        }
        a.recycle()
        mPaint = Paint()
        this.setOnClickListener {
            isLightOn = !isLightOn
            postInvalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int
        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            //            mPaint.setTextSize(mTitleTextSize);
            //            mPaint.getTextBounds(mTitle, 0, mTitle.length(), mBounds);
            //            float textWidth = mBounds.width();
            val desired = paddingLeft + 80 + paddingRight
            width = desired
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            val desired = paddingTop + 80 + paddingBottom
            height = desired
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        mPaint!!.color = if (isLightOn) Color.RED else Color.BLACK
        mPaint!!.isAntiAlias = true
        val mW = measuredWidth
        val mH = measuredHeight
        val r = if (mW < mH) mW / 2 else mH / 2
        canvas.drawCircle((mW / 2).toFloat(), (mH / 2).toFloat(), r.toFloat(), mPaint!!)
        if (isFlagStatus) {
            mPaint!!.color = Color.YELLOW
            canvas.drawCircle((mW / 2).toFloat(), (mH / 2).toFloat(), (r / 2).toFloat(), mPaint!!)
        }
    }

    /**
     * 设置灯泡的开或关（true开，false关）
     * @param status
     */
    fun setLightStatus(status: Boolean) {
        this.isLightOn = status
        invalidate()
    }

    fun setPressStatus(b: Boolean) {
        isFlagStatus = b
        invalidate()
    }


    fun setmOnClickListener(click: onLightClickListener) {
        this.mOnClickListener = click
    }


    interface onLightClickListener {
        /**
         * @param rowIndex 灯泡所在棋盘的横坐标（第几行，从0开始）
         * @param columnIndex 灯泡所在棋盘的纵坐标（第几列，从0开始）
         */
        fun onLightClicked(rowIndex: Int, columnIndex: Int)
    }
}