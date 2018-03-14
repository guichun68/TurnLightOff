package com.go.light.turnlightoff

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout

/**
 * Created by caorb1 on 2018/3/14.
 * Desc:
 */
class ChessBoardFragment:Fragment(),LightKT.onLightClickListener{
    private lateinit var rootView:LinearLayout
    private var currFrgHeight:Int = 0
    private var width: Int = 0
    private var height:Int = 0
    var isGameMode: Boolean = false//是否是解题模式，默认false
    var mOnResolvedListener: OnChessboardResolvedListener? = null//棋盘解谜成功的回调
    var columnCount: Int = 0
    var rowCount:Int = 0
    val rootParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun onLightClicked(rowIndex: Int, columnIndex: Int) {
        if (!isGameMode) {
            return
        }
        val tempStatus = getChessArrs()
        //根据游戏规则点亮或熄灭指定灯泡
        if (rowIndex > 0) {//判断顶部
            tempStatus[rowIndex - 1][columnIndex] = (if (tempStatus[rowIndex - 1][columnIndex].toInt() == 1) 0 else 1).toByte()
        }
        if (columnIndex > 0) {//判断左侧
            tempStatus[rowIndex][columnIndex - 1] = (if (tempStatus[rowIndex][columnIndex - 1].toInt() == 1) 0 else 1).toByte()
        }
        if (columnIndex + 1 < columnCount) {//判断右侧
            tempStatus[rowIndex][columnIndex + 1] = (if (tempStatus[rowIndex][columnIndex + 1].toInt() == 1) 0 else 1).toByte()
        }
        if (rowIndex + 1 < columnCount) {//判断底部
            tempStatus[rowIndex + 1][columnIndex] = (if (tempStatus[rowIndex + 1][columnIndex].toInt() == 1) 0 else 1).toByte()
        }
        setChessBoardStatus(tempStatus)

        if (mOnResolvedListener != null) {
            //检查是否已经成功熄灭所有灯泡了
            var isAllLightOff = true
            Out@ for (i in 0 until rowCount) {
                for (j in 0 until columnCount) {
                    if (((rootView.getChildAt(i) as LinearLayout).getChildAt(j) as LightKT).isLightOn) {
                        isAllLightOff = false
                        break@Out
                    }
                }
            }
            mOnResolvedListener!!.clickCallBack(isAllLightOff)
        }
    }



    interface OnChessboardResolvedListener {
        /**
         * 棋盘点击回调
         * @param isAllLightOff 是否所有的灯泡都熄灭了
         */
        fun clickCallBack(isAllLightOff: Boolean)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = LinearLayout(activity)

        currFrgHeight = (height - getStatusBarHeight() - dip2px(40)) * 2 / 3

        rootView.setOrientation(LinearLayout.VERTICAL)
        rootView.setBackground(resources.getDrawable(R.mipmap.timg))

        val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager

        width = wm.defaultDisplay.width
        height = wm.defaultDisplay.height

        for (i in 0 until rowCount) {
            val rowLinearLayout = LinearLayout(activity)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.weight = 1f
            if (i == 0) {
                params.topMargin = 10
            }
            rowLinearLayout.orientation = LinearLayout.HORIZONTAL
            for (j in 0 until columnCount) {
                val l = LightKT(activity, i, j)
                l.setmOnClickListener(this)
                rowLinearLayout.addView(l, params)
            }

            if (i != rowCount - 2) {
                rootParams.bottomMargin = width / rowCount / 2
            } else {
                rootParams.bottomMargin = 0
            }
            rootView.addView(rowLinearLayout, rootParams)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onActivityCreatedListener?.onActivityCreated()
    }

    var onActivityCreatedListener: OnActivityCreatedListener? = null

    interface OnActivityCreatedListener {
        fun onActivityCreated()
    }

    private fun getStatusBarHeight(): Int {
        /**
         * 获取状态栏高度——方法1
         */
        var statusBarHeight1 = -1
        //获取status_bar_height资源的ID
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = resources.getDimensionPixelSize(resourceId)
        }
        Log.e("WangJ", "状态栏-方法1:" + statusBarHeight1)

        return if (statusBarHeight1 == 0) {
            50
        } else statusBarHeight1
    }

    /** dip转换px  */
    private fun dip2px(dip: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dip * scale + 0.5f).toInt()
    }




    fun refreshUI() {

        rootView.removeAllViews()
        for (i in 0 until rowCount) {

            val rowLinearLayout = LinearLayout(activity)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            if (i == 0) {
                params.topMargin = 10
            }
            params.weight = 1f
            rowLinearLayout.orientation = LinearLayout.HORIZONTAL
            for (j in 0 until columnCount) {
                if (j == columnCount - 1) {
                    params.bottomMargin = 0
                    params.bottomMargin = 0
                }
                val l = LightKT(activity, i, j)
                l.setmOnClickListener(this)
                rowLinearLayout.addView(l, params)
            }

            if (i != rowCount - 2) {
                rootParams.bottomMargin = width / rowCount / 2 / 2
            } else if (i == rowCount - 1) {
                rootParams.bottomMargin = 0
            }
            rootView.addView(rowLinearLayout, rootParams)
        }
    }


    /**
     * 得到棋局的二维数组表示
     */
    fun getChessArrs(): Array<ByteArray> {
        val chArr = Array(rowCount) { ByteArray(columnCount) }
        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                chArr[i][j] = if (((rootView.getChildAt(i) as LinearLayout).getChildAt(j) as LightKT).isLightOn) 1.toByte() else 0.toByte()
            }
        }
        return chArr
    }

    //UI显示指定的解
    fun refreshPress(pressArr: Array<ByteArray>) {
        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                if (pressArr[i][j].toInt() == 1) {
                    ((rootView.getChildAt(i) as LinearLayout).getChildAt(j) as LightKT).setPressStatus(true)
                }
            }
        }
    }

    /**
     * 设置棋盘状态
     * @param status 棋盘状态数组，1灯开；0灯灭
     */
    fun setChessBoardStatus(status: Array<ByteArray>) {
        refreshUI()
        for (i in status.indices) {
            for (j in 0 until status[i].size) {
                if (status[i][j].toInt() == 1) {
                    ((rootView.getChildAt(i) as LinearLayout).getChildAt(j) as LightKT).setLightStatus(true)
                }
            }
        }
    }

    //从UI中去掉显示的解
    fun removePress() {
        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                ((rootView.getChildAt(i) as LinearLayout).getChildAt(j) as LightKT).setPressStatus(false)
            }
        }
    }


}