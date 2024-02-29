package com.example.myproject

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import android.content.Context


class ChartActivity : AppCompatActivity() {
    private lateinit var BarChart: BarChart
    private lateinit var PieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        BarChart = findViewById(R.id.BarChart)
        PieChart = findViewById(R.id.PieChart)

        // Получить данные о банках из SharedPreferences
        val bankData = loadBankData()

        // Установить данные на диаграмме
        setData(bankData)
        setDataPie(bankData)
    }

    private fun loadBankData(): List<Pair<String, Float>> {
        val sharedPreferences = getSharedPreferences("Banks", Context.MODE_PRIVATE)
        val allBanks = sharedPreferences.all
        val bankData = mutableListOf<Pair<String, Float>>()

        for ((bankName, amount) in allBanks) {
            bankData.add(bankName to amount.toString().toFloat())
        }

        return bankData
    }

    private fun setData(bankData: List<Pair<String, Float>>) {
        if (::BarChart.isInitialized) {
            BarChart.clearChart()

            bankData.forEachIndexed { index, (bankName, amount) ->
                val color = when (index % 25) {
                    0 -> Color.parseColor("#FFA726")
                    1 -> Color.parseColor("#66BB6A")
                    2 -> Color.parseColor("#EF5350")
                    3 -> Color.parseColor("#DC143C")
                    4 -> Color.parseColor("#2F4F4F")
                    5 -> Color.parseColor("#EEE8AA")
                    6 -> Color.parseColor("#C0C0C0")
                    7 -> Color.parseColor("#FF1493")
                    8 -> Color.parseColor("#FF4500")
                    9 -> Color.parseColor("#00FFFF")
                    10 -> Color.parseColor("#FFA500")
                    11-> Color.parseColor("#8B008B")
                    12 -> Color.parseColor("#ffe2b7")
                    13 -> Color.parseColor("#FFFF00")
                    14 -> Color.parseColor("#DDA0DD")
                    15 -> Color.parseColor("#DA70D6")
                    16 -> Color.parseColor("#FFF8DC")
                    17 -> Color.parseColor("#0000FF")
                    18 -> Color.parseColor("#FFD700")
                    19 -> Color.parseColor("#00FFFF")
                    20 -> Color.parseColor("#808080")
                    21 -> Color.parseColor("#ADFF2F")
                    22 -> Color.parseColor("#7FFF00")
                    23 -> Color.parseColor("#808000")
                    24 -> Color.parseColor("#7B68EE")
                    else -> Color.parseColor("#29B6F6")
                }

                BarChart.addBar(
                    BarModel(bankName, amount, color)
                )
            }
            BarChart.startAnimation()
        }
    }

    private fun setDataPie(bankData: List<Pair<String, Float>>) {
        if (::PieChart.isInitialized) {
            PieChart.clearChart()

            bankData.forEachIndexed { index, (bankName, amount) ->
                val color = when (index % 25) {
                    0 -> Color.parseColor("#FFA726")
                    1 -> Color.parseColor("#66BB6A")
                    2 -> Color.parseColor("#EF5350")
                    3 -> Color.parseColor("#DC143C")
                    4 -> Color.parseColor("#2F4F4F")
                    5 -> Color.parseColor("#EEE8AA")
                    6 -> Color.parseColor("#C0C0C0")
                    7 -> Color.parseColor("#FF1493")
                    8 -> Color.parseColor("#FF4500")
                    9 -> Color.parseColor("#00FFFF")
                    10 -> Color.parseColor("#FFA500")
                    11-> Color.parseColor("#8B008B")
                    12 -> Color.parseColor("#ffe2b7")
                    13 -> Color.parseColor("#FFFF00")
                    14 -> Color.parseColor("#DDA0DD")
                    15 -> Color.parseColor("#DA70D6")
                    16 -> Color.parseColor("#FFF8DC")
                    17 -> Color.parseColor("#0000FF")
                    18 -> Color.parseColor("#FFD700")
                    19 -> Color.parseColor("#00FFFF")
                    20 -> Color.parseColor("#808080")
                    21 -> Color.parseColor("#ADFF2F")
                    22 -> Color.parseColor("#7FFF00")
                    23 -> Color.parseColor("#808000")
                    24 -> Color.parseColor("#7B68EE")
                    else -> Color.parseColor("#29B6F6")
                }

                PieChart.addPieSlice(
                    PieModel(bankName, amount, color)
                )
            }
            PieChart.startAnimation()
        }
    }

}