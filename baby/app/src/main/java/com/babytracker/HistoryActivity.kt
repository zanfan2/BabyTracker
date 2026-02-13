package com.babytracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.adapter.RecordAdapter
import com.babytracker.data.RecordType
import com.babytracker.viewmodel.BabyViewModel
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: BabyViewModel
    private lateinit var adapter: RecordAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    private var startDate: Calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, -7)
    }
    private var endDate: Calendar = Calendar.getInstance()
    private var selectedType: RecordType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "历史记录"

        viewModel = ViewModelProvider(this)[BabyViewModel::class.java]
        recyclerView = findViewById(R.id.recyclerViewHistory)
        emptyView = findViewById(R.id.tvEmptyView)

        setupRecyclerView()
        setupFilters()
        setupDatePickers()
        loadHistory()
    }

    private fun setupRecyclerView() {
        adapter = RecordAdapter { record ->
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("删除记录")
                .setMessage("确定要删除这条记录吗?")
                .setPositiveButton("删除") { _, _ ->
                    viewModel.delete(record)
                    Toast.makeText(this, "记录已删除", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFilters() {
        findViewById<Chip>(R.id.chipAll).setOnClickListener {
            selectedType = null
            loadHistory()
        }

        findViewById<Chip>(R.id.chipFeeding).setOnClickListener {
            selectedType = RecordType.FEEDING
            loadHistory()
        }

        findViewById<Chip>(R.id.chipPoop).setOnClickListener {
            selectedType = RecordType.POOP
            loadHistory()
        }

        findViewById<Chip>(R.id.chipPee).setOnClickListener {
            selectedType = RecordType.PEE
            loadHistory()
        }
    }

    private fun setupDatePickers() {
        val btnStartDate = findViewById<Button>(R.id.btnSelectStartDate)
        val btnEndDate = findViewById<Button>(R.id.btnSelectEndDate)
        val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())

        btnStartDate.text = dateFormat.format(startDate.time)
        btnEndDate.text = dateFormat.format(endDate.time)

        btnStartDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    startDate.set(year, month, day)
                    startDate.set(Calendar.HOUR_OF_DAY, 0)
                    startDate.set(Calendar.MINUTE, 0)
                    startDate.set(Calendar.SECOND, 0)
                    btnStartDate.text = dateFormat.format(startDate.time)
                    loadHistory()
                },
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnEndDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    endDate.set(year, month, day)
                    endDate.set(Calendar.HOUR_OF_DAY, 23)
                    endDate.set(Calendar.MINUTE, 59)
                    endDate.set(Calendar.SECOND, 59)
                    btnEndDate.text = dateFormat.format(endDate.time)
                    loadHistory()
                },
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun loadHistory() {
        val liveData = if (selectedType != null) {
            viewModel.getRecordsByType(selectedType!!)
        } else {
            viewModel.getRecordsByDateRange(startDate.timeInMillis, endDate.timeInMillis)
        }

        liveData.observe(this) { records ->
            val filteredRecords = records.filter {
                it.timestamp in startDate.timeInMillis..endDate.timeInMillis
            }

            if (filteredRecords.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                adapter.submitList(filteredRecords)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
