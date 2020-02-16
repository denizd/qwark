package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.model.HistoricalAvg
import java.util.*

internal class HistoricalAverageAdapter(private var averages: List<HistoricalAvg>?) : RecyclerView.Adapter<HistoricalAverageAdapter.AverageViewHolder>() {

    internal class AverageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val grade: TextView = view.findViewById(R.id.grade)
        val time: TextView = view.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AverageViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.average_card, parent, false)
        return AverageViewHolder(v)
    }

    override fun onBindViewHolder(holder: AverageViewHolder, position: Int) {
        val currentItem = averages!![position]

        val leadingZero = if (currentItem.average.toDouble() < 10.0) "0%s" else "%s"
        holder.grade.text = String.format(leadingZero, currentItem.average) // TODO this conflicts with Points (Precise)
        holder.time.text = QwarkUtil.getSimpleDateTimeFormat().format(Date(currentItem.time))
    }

    override fun getItemCount(): Int = averages?.size ?: 0

    fun setAverages(averages: List<HistoricalAvg>) {
        this.averages = averages
        notifyDataSetChanged()
    }
}