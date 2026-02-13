package com.babytracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.adapter.RecordAdapter
import com.babytracker.data.*
import com.babytracker.viewmodel.BabyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BabyViewModel
    private lateinit var adapter: RecordAdapter
    private lateinit var tvTodayStats: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[BabyViewModel::class.java]
        tvTodayStats = findViewById(R.id.tvTodayStats)

        setupRecyclerView()
        setupButtons()
        setupBottomNavigation()
        loadTodayStats()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RecordAdapter { record ->
            showDeleteConfirmation(record)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allRecords.observe(this) { records ->
            adapter.submitList(records)
        }
    }

    private fun setupButtons() {
        findViewById<View>(R.id.btnFeeding)?.setOnClickListener {
            showFeedingDialog()
        }

        findViewById<View>(R.id.btnPoop)?.setOnClickListener {
            showPoopDialog()
        }

        findViewById<View>(R.id.btnPee)?.setOnClickListener {
            showPeeDialog()
        }
    }

    private fun setupBottomNavigation() {
        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_statistics -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadTodayStats() {
        lifecycleScope.launch {
            val feedingCount = viewModel.getDailyStats(RecordType.FEEDING)
            val poopCount = viewModel.getDailyStats(RecordType.POOP)
            val peeCount = viewModel.getDailyStats(RecordType.PEE)
            val totalMilk = viewModel.getDailyMilkAmount()

            tvTodayStats.text = buildString {
                append("喂奶: ${feedingCount}次")
                if (totalMilk > 0) append(" (${totalMilk}ml)")
                append("\n拉屎: ${poopCount}次")
                append("\n拉尿: ${peeCount}次")
            }
        }
    }

    private fun showFeedingDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_feeding, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // 初始化视图
        val tvSelectedTime = dialogView.findViewById<TextView>(R.id.tvSelectedTime)
        val rgMilkType = dialogView.findViewById<RadioGroup>(R.id.rgMilkType)
        val rbBreast = dialogView.findViewById<RadioButton>(R.id.rbBreast)
        val rbFormula = dialogView.findViewById<RadioButton>(R.id.rbFormula)
        val layoutFormulaMilk = dialogView.findViewById<LinearLayout>(R.id.layoutFormulaMilk)
        val layoutBreastMilk = dialogView.findViewById<LinearLayout>(R.id.layoutBreastMilk)
        val etMilkAmount = dialogView.findViewById<TextInputEditText>(R.id.etMilkAmount)
        val etFeedingDuration = dialogView.findViewById<TextInputEditText>(R.id.etFeedingDuration)
        val rgBreastSide = dialogView.findViewById<RadioGroup>(R.id.rgBreastSide)
        val rbLeft = dialogView.findViewById<RadioButton>(R.id.rbLeft)
        val rbRight = dialogView.findViewById<RadioButton>(R.id.rbRight)
        val rbBoth = dialogView.findViewById<RadioButton>(R.id.rbBoth)
        val layoutBothDuration = dialogView.findViewById<LinearLayout>(R.id.layoutBothDuration)
        val etLeftDuration = dialogView.findViewById<TextInputEditText>(R.id.etLeftDuration)
        val etRightDuration = dialogView.findViewById<TextInputEditText>(R.id.etRightDuration)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.etNotes)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        // 时间选择器
        val calendar = Calendar.getInstance()
        var selectedTime = calendar.timeInMillis

        // 更新时间显示
        fun updateTimeDisplay() {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            tvSelectedTime.text = sdf.format(Date(selectedTime))
        }
        updateTimeDisplay()

        // 点击时间卡片显示时间选择器
        dialogView.findViewById<View>(R.id.tvSelectedTime).setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val timePicker = TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                            selectedTime = calendar.timeInMillis
                            updateTimeDisplay()
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePicker.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // 奶类型切换逻辑
        fun updateMilkTypeUI() {
            if (rbBreast.isChecked) {
                layoutFormulaMilk.visibility = View.GONE
                layoutBreastMilk.visibility = View.VISIBLE
            } else {
                layoutFormulaMilk.visibility = View.VISIBLE
                layoutBreastMilk.visibility = View.GONE
            }
        }

        rgMilkType.setOnCheckedChangeListener { _, _ -> updateMilkTypeUI() }

        // 左右边选择逻辑
        fun updateBreastSideUI() {
            when (rgBreastSide.checkedRadioButtonId) {
                R.id.rbLeft, R.id.rbRight -> {
                    layoutBothDuration.visibility = View.GONE
                }
                R.id.rbBoth -> {
                    layoutBothDuration.visibility = View.VISIBLE
                }
            }
        }

        rgBreastSide.setOnCheckedChangeListener { _, _ -> updateBreastSideUI() }

        // 初始化UI状态
        updateMilkTypeUI()
        updateBreastSideUI()

        // 保存按钮逻辑
        btnSave.setOnClickListener {
            val milkType = if (rbBreast.isChecked) MilkType.BREAST else MilkType.FORMULA

            val record = when (milkType) {
                MilkType.FORMULA -> {
                    // 配方奶：需要输入奶量
                    val amount = etMilkAmount.text.toString().toIntOrNull()
                    if (amount == null || amount <= 0) {
                        Toast.makeText(this, "请输入有效的奶量", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    BabyRecord(
                        type = RecordType.FEEDING,
                        timestamp = selectedTime,
                        milkAmount = amount,
                        milkType = milkType,
                        notes = etNotes.text.toString().takeIf { it.isNotBlank() }
                    )
                }
                MilkType.BREAST -> {
                    // 母乳：需要输入时长和左右边
                    val totalDuration = etFeedingDuration.text.toString().toIntOrNull()
                    if (totalDuration == null || totalDuration <= 0) {
                        Toast.makeText(this, "请输入有效的喂养时长", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val breastSide = when (rgBreastSide.checkedRadioButtonId) {
                        R.id.rbLeft -> BreastSide.LEFT
                        R.id.rbRight -> BreastSide.RIGHT
                        R.id.rbBoth -> BreastSide.BOTH
                        else -> BreastSide.BOTH
                    }

                    val leftDuration = etLeftDuration.text.toString().toIntOrNull()
                    val rightDuration = etRightDuration.text.toString().toIntOrNull()

                    // 如果是双边，验证左右时长
                    if (breastSide == BreastSide.BOTH) {
                        if (leftDuration == null || rightDuration == null ||
                            leftDuration <= 0 || rightDuration <= 0) {
                            Toast.makeText(this, "请输入左边和右边的时长", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }

                    BabyRecord(
                        type = RecordType.FEEDING,
                        timestamp = selectedTime,
                        milkType = milkType,
                        feedingDuration = totalDuration,
                        breastSide = breastSide,
                        leftBreastDuration = leftDuration,
                        rightBreastDuration = rightDuration,
                        notes = etNotes.text.toString().takeIf { it.isNotBlank() }
                    )
                }
            }

            viewModel.insert(record)
            loadTodayStats()
            dialog.dismiss()
            Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showPoopDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_poop, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val tvSelectedTime = dialogView.findViewById<TextView>(R.id.tvSelectedTime)
        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)
        val spinnerConsistency = dialogView.findViewById<Spinner>(R.id.spinnerConsistency)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.etNotes)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        // 时间选择器
        val calendar = Calendar.getInstance()
        var selectedTime = calendar.timeInMillis

        fun updateTimeDisplay() {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            tvSelectedTime.text = sdf.format(Date(selectedTime))
        }
        updateTimeDisplay()

        tvSelectedTime.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val timePicker = TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                            selectedTime = calendar.timeInMillis
                            updateTimeDisplay()
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePicker.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        btnSave.setOnClickListener {
            val color = spinnerColor.selectedItem.toString()
            val consistency = spinnerConsistency.selectedItem.toString()

            val record = BabyRecord(
                type = RecordType.POOP,
                timestamp = selectedTime,
                poopColor = color,
                poopConsistency = consistency,
                notes = etNotes.text.toString().takeIf { it.isNotBlank() }
            )

            viewModel.insert(record)
            loadTodayStats()
            dialog.dismiss()
            Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showPeeDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pee, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val tvSelectedTime = dialogView.findViewById<TextView>(R.id.tvSelectedTime)
        val rgPeeAmount = dialogView.findViewById<RadioGroup>(R.id.rgPeeAmount)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.etNotes)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        // 时间选择器
        val calendar = Calendar.getInstance()
        var selectedTime = calendar.timeInMillis

        fun updateTimeDisplay() {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            tvSelectedTime.text = sdf.format(Date(selectedTime))
        }
        updateTimeDisplay()

        tvSelectedTime.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val timePicker = TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                            selectedTime = calendar.timeInMillis
                            updateTimeDisplay()
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePicker.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        btnSave.setOnClickListener {
            val amount = when (rgPeeAmount.checkedRadioButtonId) {
                R.id.rbLittle -> "少"
                R.id.rbMuch -> "多"
                else -> "中"
            }

            val record = BabyRecord(
                type = RecordType.PEE,
                timestamp = selectedTime,
                peeAmount = amount,
                notes = etNotes.text.toString().takeIf { it.isNotBlank() }
            )

            viewModel.insert(record)
            loadTodayStats()
            dialog.dismiss()
            Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(record: BabyRecord) {
        AlertDialog.Builder(this)
            .setTitle("删除记录")
            .setMessage("确定要删除这条记录吗?")
            .setPositiveButton("删除") { _, _ ->
                viewModel.delete(record)
                loadTodayStats()
                Toast.makeText(this, "记录已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.nav_home
        loadTodayStats()
    }
}
