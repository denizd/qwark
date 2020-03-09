package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.databinding.AverageCardBinding
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.model.HistoricalAvg
import com.denizd.qwark.util.getUserDefinedAverage
import java.util.*

class HistoricalAverageAdapter(private val averages: List<HistoricalAvg>, private val gradeType: Int) : RecyclerView.Adapter<HistoricalAverageAdapter.AverageViewHolder>() {

    class AverageViewHolder(val binding: AverageCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AverageViewHolder = AverageViewHolder(
        AverageCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: AverageViewHolder, position: Int) {
        val currentItem = averages[position]

        with (holder.binding) {
            grade.text = currentItem.average.getUserDefinedAverage(gradeType)
            time.text = QwarkUtil.getSimpleDateTimeFormat().format(Date(currentItem.time))
        }
    }

    override fun getItemCount(): Int = averages.size

//    fun setAverages(averages: List<HistoricalAvg>) {
//        this.averages = averages
//        notifyDataSetChanged()
//    }
}