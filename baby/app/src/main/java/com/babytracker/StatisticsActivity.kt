package com.babytracker

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.babytracker.data.RecordType
import com.babytracker.viewmodel.BabyViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch

class StatisticsActivity : AppCompatActivity() {

    private lateinit var viewModel: BabyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "统计"

        viewModel = ViewModelProvider(this)[BabyViewModel::class.java]

        loadStatistics()
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            // 今日统计
            val todayFeedingCount = viewModel.getDailyStats(RecordType.FEEDING)
            val todayTotalMilk = viewModel.getDailyMilkAmount()
            val todayPoopCount = viewModel.getDailyStats(RecordType.POOP)
            val todayPeeCount = viewModel.getDailyStats(RecordType.PEE)

            findViewById<TextView>(R.id.tvTodayFeedingCount).text = "喂奶次数: $todayFeedingCount"
            findViewById<TextView>(R.id.tvTodayTotalMilk).text = "总奶量: ${todayTotalMilk}ml"
            findViewById<TextView>(R.id.tvTodayPoopCount).text = "拉屎次数: $todayPoopCount"
            findViewById<TextView>(R.id.tvTodayPeeCount).text = "拉尿次数: $todayPeeCount"

            // 本周趋势
            val feedingWeekly = viewModel.getWeeklyStats(RecordType.FEEDING)
            val poopWeekly = viewModel.getWeeklyStats(RecordType.POOP)
            val peeWeekly = viewModel.getWeeklyStats(RecordType.PEE)

            setupChart(findViewById(R.id.feedingChart), feedingWeekly, Color.parseColor("#4CAF50"))
            setupChart(findViewById(R.id.poopChart), poopWeekly, Color.parseColor("#FF9800"))
            setupChart(findViewById(R.id.peeChart), peeWeekly, Color.parseColor("#2196F3"))
        }
    }

    private fun setupChart(chart: BarChart, data: Map<String, Int>, color: Int) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // 创建7天的数据
        val days = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        for ((index, day) in days.withIndex()) {
            val value = data.values.elementAtOrNull(index)?.toFloat() ?: 0f
            entries.add(BarEntry(index.toFloat(), value))
            labels.add(day)
        }

        val dataSet = BarDataSet(entries, "")
        dataSet.color = color
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        chart.data = barData
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setFitBars(true)

        // X轴设置
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.setDrawGridLines(false)

        // Y轴设置
        chart.axisLeft.setDrawGridLines(true)
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.isEnabled = false

        chart.animateY(800)
        chart.invalidate()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
