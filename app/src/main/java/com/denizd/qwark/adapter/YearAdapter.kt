package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.model.SchoolYear

internal class YearAdapter(private var schoolYears: List<SchoolYear>, private val onClickListener: YearClickListener) : RecyclerView.Adapter<YearAdapter.YearViewHolder>() {

    internal class YearViewHolder(view: View, private val clickListener: YearClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        internal val title: TextView = view.findViewById(R.id.title)
        internal lateinit var currentSchoolYear: SchoolYear

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onYearClick(currentSchoolYear)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.year_card, parent, false)
        return YearViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        val currentItem = schoolYears[position]

        holder.title.text = currentItem.year
        holder.currentSchoolYear = currentItem
    }

    override fun getItemCount(): Int = schoolYears.size

    fun setYears(schoolYears: List<SchoolYear>) {
        this.schoolYears = schoolYears
        notifyDataSetChanged()
    }

    internal interface YearClickListener {
        fun onYearClick(schoolYear: SchoolYear)
    }
}