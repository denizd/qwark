package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.model.SchoolDay

class SchoolDayAdapter(private var days: List<SchoolDay>, private val onClickListener: DayClickListener) : RecyclerView.Adapter<SchoolDayAdapter.DayViewHolder>() {

    class DayViewHolder(view: View, private val clickListener: DayClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val title: TextView = view.findViewById(R.id.title)
        lateinit var currentDay: SchoolDay

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onDayClick(currentDay)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.year_card, parent, false)
        return DayViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val currentItem = days[position]

        holder.title.text = holder.title.context.resources.getStringArray(R.array.days)[dayMap[currentItem.day] ?: 0]
        holder.currentDay = currentItem
    }

    override fun getItemCount(): Int = days.size

    fun setDays(days: List<SchoolDay>) {
        this.days = days
        notifyDataSetChanged()
    }

    private val dayMap: Map<String, Int> = mapOf(
        "mon" to 0,
        "tue" to 1,
        "wed" to 2,
        "thu" to 3,
        "fri" to 4
    )

    interface DayClickListener {
        fun onDayClick(day: SchoolDay)
    }
}