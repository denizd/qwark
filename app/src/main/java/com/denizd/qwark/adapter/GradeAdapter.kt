package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.model.Grade
import com.denizd.qwark.util.getUserDefinedAverage
import java.util.*

class GradeAdapter(private var grades: List<Grade>, private val onClickListener: GradeClickListener, private val gradeType: Int) : RecyclerView.Adapter<GradeAdapter.GradeViewHolder>() {

    class GradeViewHolder(view: View, private val clickListener: GradeClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        val grade: TextView = view.findViewById(R.id.grade)
        val notes: TextView = view.findViewById(R.id.notes)
        val info: TextView = view.findViewById(R.id.info)
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
        lateinit var currentGrade: Grade

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onGradeClick(currentGrade)
        }

        override fun onLongClick(v: View?): Boolean {
            clickListener.onGradeLongClick(currentGrade.gradeId)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.grade_card, parent, false)
        return GradeViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val currentItem = grades[position]

        holder.grade.text = if (currentItem.grade == -1) {
            "??"
        } else {
            QwarkUtil.getGradeAsString(currentItem.grade, true).getUserDefinedAverage(gradeType, true)
        }
        holder.notes.text = if (currentItem.note.isEmpty()) "---" else currentItem.note
        holder.info.text = buildString {
            append(
                holder.info.context.getString(
                    if (currentItem.verbal) R.string.verbal_placeholder else R.string.written_placeholder
                )
            )
            if (currentItem.examTime != -1L) append(
                holder.info.context.getString(
                        R.string.exam_on_placeholder,
                        QwarkUtil.getSimpleDateFormat().format(Date(currentItem.examTime)
                    ).toString()
                )
            )
        }

        holder.progressBar.apply {
            max = 100
            progress = currentItem.weighting
        }
        holder.currentGrade = currentItem
    }

    override fun getItemCount(): Int = grades.size

    fun setGrades(grades: List<Grade>) {
        this.grades = grades
        notifyDataSetChanged()
    }

    interface GradeClickListener {
        fun onGradeClick(grade: Grade)
        fun onGradeLongClick(gradeId: Int)
    }
}