package com.babytracker.adapter

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.R
import com.babytracker.data.*

class RecordAdapter(private val onDeleteClick: (BabyRecord) -> Unit) :
    ListAdapter<BabyRecord, RecordAdapter.RecordViewHolder>(RecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layoutMainContent: LinearLayout = itemView.findViewById(R.id.layoutMainContent)
        private val layoutNotes: LinearLayout = itemView.findViewById(R.id.layoutNotes)
        private val typeIndicator: View = itemView.findViewById(R.id.typeIndicator)
        private val tvRecordIcon: TextView = itemView.findViewById(R.id.tvRecordIcon)
        private val tvRecordType: TextView = itemView.findViewById(R.id.tvRecordType)
        private val tvRecordDetails: TextView = itemView.findViewById(R.id.tvRecordDetails)
        private val tvRecordTime: TextView = itemView.findViewById(R.id.tvRecordTime)
        private val tvRecordNotes: TextView = itemView.findViewById(R.id.tvRecordNotes)
        private val btnDelete: View = itemView.findViewById(R.id.btnDelete)

        fun bind(record: BabyRecord) {
            when (record.type) {
                RecordType.FEEDING -> {
                    typeIndicator.backgroundTintList = itemView.context.getColorStateList(R.color.md_theme_light_primaryContainer)
                    tvRecordIcon.text = "🍼"
                    tvRecordType.text = "喂奶"

                    // 根据奶类型显示不同信息
                    tvRecordDetails.text = when (record.milkType) {
                        MilkType.BREAST -> {
                            buildString {
                                append("母乳")
                                record.breastSide?.let { side ->
                                    append(" • ")
                                    append(when (side) {
                                        BreastSide.LEFT -> "左边"
                                        BreastSide.RIGHT -> "右边"
                                        BreastSide.BOTH -> "双边"
                                    })
                                }
                                record.feedingDuration?.let { append(" • ${it}分钟") }
                                if (record.breastSide == BreastSide.BOTH) {
                                    record.leftBreastDuration?.let { append(" (左${it}") }
                                    record.rightBreastDuration?.let { append(" + 右${it})") }
                                }
                            }
                        }
                        MilkType.FORMULA -> {
                            buildString {
                                append("配方奶")
                                record.milkAmount?.let { append(" • ${it}ml") }
                            }
                        }
                        null -> "母乳"
                    }
                }
                RecordType.POOP -> {
                    typeIndicator.backgroundTintList = itemView.context.getColorStateList(R.color.md_theme_light_tertiaryContainer)
                    tvRecordIcon.text = "💩"
                    tvRecordType.text = "拉屎"
                    tvRecordDetails.text = buildString {
                        record.poopColor?.let { append("$it") }
                        record.poopConsistency?.let {
                            if (isNotEmpty()) append(" • ")
                            append(it)
                        }
                    }
                }
                RecordType.PEE -> {
                    typeIndicator.backgroundTintList = itemView.context.getColorStateList(R.color.md_theme_light_secondaryContainer)
                    tvRecordIcon.text = "💧"
                    tvRecordType.text = "拉尿"
                    tvRecordDetails.text = record.peeAmount?.let { "尿量: $it" } ?: "已记录"
                }
            }

            tvRecordTime.text = record.getFormattedTime()

            // 处理备注显示
            if (record.notes.isNullOrBlank()) {
                layoutNotes.visibility = View.GONE
            } else {
                tvRecordNotes.text = record.notes
                layoutNotes.visibility = View.GONE  // 初始隐藏
            }

            // 设置展开/收起动画
            layoutNotes.layoutTransition = LayoutTransition()
            layoutNotes.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

            // 点击卡片展开/收起备注（点击删除按钮以外的区域）
            layoutMainContent.setOnClickListener {
                if (!record.notes.isNullOrBlank()) {
                    if (layoutNotes.visibility == View.GONE) {
                        layoutNotes.visibility = View.VISIBLE
                    } else {
                        layoutNotes.visibility = View.GONE
                    }
                }
            }

            // 删除按钮点击事件
            btnDelete.setOnClickListener {
                onDeleteClick(record)
            }
        }
    }

    class RecordDiffCallback : DiffUtil.ItemCallback<BabyRecord>() {
        override fun areItemsTheSame(oldItem: BabyRecord, newItem: BabyRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BabyRecord, newItem: BabyRecord): Boolean {
            return oldItem == newItem
        }
    }
}
