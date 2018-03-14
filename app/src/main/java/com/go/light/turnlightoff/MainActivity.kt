package com.go.light.turnlightoff

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

        private val mChessBoardFrag by lazy {
            ChessBoardFragment()
        }
        private val allSolves = ArrayList<SolveKT>()//所有的解

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            tvSolveCount.setOnClickListener(this)
            btnMinSolve.setOnClickListener(this)
            btnMaxSolve.setOnClickListener(this)
            btnAllSolve.setOnClickListener(this)
            btnSet.setOnClickListener(this)
            btnSolve.setOnClickListener(this)
            btnPlayMode.setOnClickListener(this)

            mChessBoardFrag.columnCount=5
            mChessBoardFrag.rowCount=5
            mChessBoardFrag.isGameMode=false
            supportFragmentManager.beginTransaction().add(R.id.main, mChessBoardFrag).commit()

        }

        lateinit var press: Array<ByteArray>//记录每局按键操作,按下为1，否则为0

        override fun onClick(v: View?) {
            when (v!!.getId()) {
                R.id.btnSet -> {
                    if (!verifyInput()) {
                        return
                    }
                    mChessBoardFrag.rowCount=Integer.parseInt(etRow.text.toString())
                    mChessBoardFrag.columnCount=Integer.parseInt(etColumn.text.toString())
                    mChessBoardFrag.refreshUI()
                }

                R.id.btnPlayMode -> {
                    //判断是否是空的棋盘（没有亮灯）
                    val chessBoardStatus = mChessBoardFrag.getChessArrs()
                    var haveLight = false
                    OUT@ for (chessBoardStatu in chessBoardStatus) {
                        for (aChessBoardStatu in chessBoardStatu) {
                            if (aChessBoardStatu.toInt() == 1) {
                                haveLight = true
                                break@OUT
                            }
                        }
                    }
                    if (!haveLight) {
                        Toast.makeText(this@MainActivity, "没有打开任何灯泡", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val intent = Intent(this@MainActivity, GameActKT::class.java)
                    intent.putExtra("array", chessBoardStatus)
                    startActivity(intent)
                }
                R.id.btnSolve -> {

                    allSolves.clear()
                    val rootArr = mChessBoardFrag.getChessArrs()

                    val colum = mChessBoardFrag.columnCount
                    val rows = mChessBoardFrag.rowCount


                    press = Array(rows) { ByteArray(colum) }
                    val max = Math.pow(2.0, colum.toDouble()) - 1

                    var i = 0
                    while (i <= max) {
                        val tempArr = arrayOfNulls<ByteArray>(rows)
                        for (k in 0 until rows) {
                            tempArr[k] = rootArr[k].clone()
                        }

                        //                    System.out.println("第"+i+"局开始");
                        //重新新的局面之前，清空按键记录数组
                        for (p in 0 until rows) {
                            for (q in 0 until colum) {
                                press!![p][q] = 0
                            }
                        }

                        /*for(int jj = 0;jj<colum;jj++){
                        if(i==0)break;
                        int y = (int)(Math.pow(2,i-1));//要与运算的数
                        press[0][colum-jj-1] = (byte) ((i&y)>>jj);
                    }*/
                        val re = Integer.toBinaryString(i)
                        var sb = StringBuilder()
                        if (re.length < 16) {
                            sb = StringBuilder()
                            for (k in 0 until 16 - re.length) {
                                sb.append("0")
                            }
                            sb.append(re)
                        }
                        val reResult = sb.toString()
                        for (jj in 0 until colum) {
                            press!![0][colum - jj - 1] = java.lang.Byte.parseByte(reResult.substring(reResult.length - 1 - jj, reResult.length - jj))
                        }
                        //按下第一行并刷新第二行状态
                        for (kk in 0..1) {
                            for (n in 0 until colum) {
                                if (press!![kk][n].toInt() == 1) {//只对按下的灯和它的周边感兴趣
                                    tempArr[kk]!![n] = (if (tempArr[kk]!![n].toInt() == 1) 0 else 1).toByte()
                                    if (n + 1 < colum) {//存在下一列
                                        tempArr[kk]!![n + 1] = (if (tempArr[kk]!![n + 1].toInt() == 1) 0 else 1).toByte()
                                    }
                                    if (n - 1 >= 0) {//存在上一列
                                        tempArr[kk]!![n - 1] = (if (tempArr[kk]!![n - 1].toInt() == 1) 0 else 1).toByte()
                                    }
                                    if (kk + 1 < rows) {//存在下一行
                                        tempArr[kk + 1]!![n] = (if (tempArr[kk + 1]!![n].toInt() == 1) 0 else 1).toByte()
                                    }
                                }
                            }
                            break
                        }

                        //按下第二行，刷新第三行状态
                        for (m in 1..rows - 1) {
                            for (n in 0 until colum) {

                                if (tempArr[m - 1]!![n].toInt() == 1) {
                                    press!![m][n] = 1
                                } else {
                                    press!![m][n] = 0
                                }


                                if (press!![m][n].toInt() == 1) {//只对按下的灯和它的周边感兴趣
                                    tempArr[m]!![n] = (if (tempArr[m]!![n].toInt() == 1) 0 else 1).toByte()
                                    if (n + 1 < colum) {
                                        tempArr[m]!![n + 1] = (if (tempArr[m]!![n + 1].toInt() == 1) 0 else 1).toByte()
                                    }
                                    if (n - 1 >= 0) {
                                        tempArr[m]!![n - 1] = (if (tempArr[m]!![n - 1].toInt() == 1) 0 else 1).toByte()
                                    }
                                    if (m + 1 < rows) {
                                        tempArr[m + 1]!![n] = (if (tempArr[m + 1]!![n].toInt() == 1) 0 else 1).toByte()
                                    }
                                    if (m - 1 >= 0) {
                                        tempArr[m - 1]!![n] = (if (tempArr[m - 1]!![n].toInt() == 1) 0 else 1).toByte()
                                    }
                                }
                            }

                        }
                        var isOK = true
                        //判断整个局面是否已经全0（灯全灭）
                        for (ii in tempArr.indices) {
                            for (jj in 0 until tempArr[ii]!!.size) {
                                if (tempArr[ii]!![jj].toInt() == 1) {
                                    isOK = false
                                }
                            }
                            if (!isOK) break
                        }
                        if (isOK) {
//                            val temp2 = Array<byte>(byteArrayOf(press!!.size.toByte())//<ByteArray>(press!!.size)
//                            val temp = arrayOf<ByteArray>(byteArrayOf(press.size,9))
                            val temp = Array(press.size,{ byteArrayOf()})
                            for (b in press!!.indices) {
                                temp[b] = press!![b].clone()
                            }
                            val s = SolveKT(temp)
                            var pressCount: Int = 0

                            //                        System.out.println("找到解啦##########################################################");
                            for (b in press!!) {
                                for (c in b) {
                                    if (c.toInt() == 1) {
                                        pressCount++
                                    }
                                }
                            }
                            s.count=pressCount
                            allSolves.add(s)
                        }
                        i++
                    }
                    Toast.makeText(this, "求解完成", Toast.LENGTH_SHORT).show()
                    tvSolveCount.text = "解数量:" + allSolves.size
                }

                R.id.btnMinSolve -> {
                    if (allSolves.isEmpty()) {
                        Toast.makeText(this, "无解", Toast.LENGTH_SHORT).show()
                        return
                    }
                    mChessBoardFrag.removePress()
                    //最优解
                    val s = allSolves.get(getMin(allSolves))
                    mChessBoardFrag.refreshPress(s.solvePress)
                }

                R.id.btnMaxSolve -> {
                    if (allSolves.isEmpty()) {
                        Toast.makeText(this, "无解", Toast.LENGTH_SHORT).show()
                        return
                    }
                    mChessBoardFrag.removePress()
                    //最优解
                    val s2 = allSolves.get(getMax(allSolves))
                    mChessBoardFrag.refreshPress(s2.solvePress)
                }

                R.id.btnAllSolve -> {
                    if (allSolves.isEmpty()) {
                        Toast.makeText(this, "无解", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val intent = Intent(this, AllSolveAct::class.java)
                    intent.putExtra("solves", allSolves)
                    startActivity(intent)
                }
            }
        }

    //获取最小值所在集合的索引
    private fun getMin(solves: ArrayList<SolveKT>): Int {
        var minIndex = 0
        var min = solves[0].count

        for (i in solves.indices) {
            if (solves[i].count < min) {
                min = solves[i].count
                minIndex = i
            }
        }
        return minIndex
    }

    //获取最大值所在集合的索引
    private fun getMax(solves: ArrayList<SolveKT>): Int {
        var maxIndex = 0
        var max = solves[0].count

        for (i in solves.indices) {
            if (solves[i].count > max) {
                max = solves[i].count
                maxIndex = i
            }
        }
        return maxIndex
    }

    private fun verifyInput(): Boolean {
        val rows = etRow.text.toString()
        val clos = etColumn.text.toString()
        try {
            val rowCount = Integer.parseInt(rows)
            val columnCount = Integer.parseInt(clos)
            if (rowCount > 10 || columnCount > 10) {
                Toast.makeText(this, "鉴于手机屏幕限制，暂只支持10行10列以内", Toast.LENGTH_SHORT).show()
                return false
            }
            if (rowCount < 2 || columnCount < 2) {
                Toast.makeText(this, "列或行需>1", Toast.LENGTH_SHORT).show()
                return false
            }

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "输入有误", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}

