package com.go.light.turnlightoff

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView


import java.util.ArrayList

/**
 * Created by Austin on 2017-09-11.
 */

class AllSolveAct : AppCompatActivity() {

    private var tvSolves: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_solve)
        tvSolves = findViewById(R.id.tvSolves) as TextView

        val solves = intent.getSerializableExtra("solves") as ArrayList<SolveKT>

        val sb = StringBuilder()

        for (m in solves.indices) {
            val solvePress = solves[m].solvePress
            sb.append("第").append((m + 1).toString()).append("个解：开关次数").append(solves[m].count).append("\n")
            for (i in solvePress.indices) {
                for (j in 0 until solvePress[i].size) {
                    sb.append(solvePress[i][j].toString()).append(" ")
                }
                sb.append("\n")
            }
            sb.append("\n")
        }
        tvSolves!!.text = sb.toString()
    }
}
