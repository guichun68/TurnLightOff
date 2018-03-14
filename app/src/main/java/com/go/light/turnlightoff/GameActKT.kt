package com.go.light.turnlightoff

/**
 * Created by caorb1 on 2018/3/15.
 * Desc:
 */

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class GameActKT : AppCompatActivity() {
    private var mChessBoardFrag: ChessBoardFragment? = null
    private var mChessBoardStatus: Array<ByteArray>? = null
    private var tvStep: TextView? = null
    private var mStepCount: Int = 0//一共行走次数
    private var isGameOver: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        mChessBoardStatus = intent.extras!!.getSerializable("array") as Array<ByteArray>
        tvStep = findViewById(R.id.tv_step) as TextView

        mChessBoardFrag = ChessBoardFragment()
        mChessBoardFrag!!.columnCount=(mChessBoardStatus!![0].size)
        mChessBoardFrag!!.rowCount=(mChessBoardStatus!!.size)
        supportFragmentManager.beginTransaction().add(R.id.main, mChessBoardFrag).commitNow()//commit();

        mChessBoardFrag!!.onActivityCreatedListener=object : ChessBoardFragment.OnActivityCreatedListener {
            override fun onActivityCreated() {
                mChessBoardFrag!!.setChessBoardStatus(mChessBoardStatus!!)
                mChessBoardFrag!!.isGameMode=true
            }
        }
        mChessBoardFrag!!.mOnResolvedListener=object : ChessBoardFragment.OnChessboardResolvedListener {
            override fun clickCallBack(isAllLightOff: Boolean) {
                if (!isGameOver) {
                    tvStep!!.text = "${++mStepCount}"
                    if (isAllLightOff) {
                        val adb = AlertDialog.Builder(this@GameActKT)
                        adb.setTitle("成功")
                        adb.setMessage("闯关成功，总共点击次数：" + mStepCount)
                        val dialog = adb.create()
                        dialog.show()
                        isGameOver = true
                    }
                }
            }
        }
    }


}
