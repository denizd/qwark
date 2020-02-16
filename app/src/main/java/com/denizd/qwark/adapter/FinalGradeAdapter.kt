package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.model.FinalGrade

internal class FinalGradeAdapter(private var finalGrades: List<FinalGrade>, private val onClickListener: FinalGradeClickListener) : RecyclerView.Adapter<FinalGradeAdapter.FinalGradeViewHolder>() {

    internal class FinalGradeViewHolder(view: View, private val clickListener: FinalGradeClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        internal val name: TextView = view.findViewById(R.id.name)
        internal val grade: TextView = view.findViewById(R.id.grade)
        internal val multiplier: TextView = view.findViewById(R.id.multiplier)
        internal lateinit var currentFinalGrade: FinalGrade

        init {
            view.setOnClickListener(this)
//            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onFinalGradeClick(currentFinalGrade)
        }

        override fun onLongClick(v: View?): Boolean {
//            clickListener.onFinalGradeLongClick(currentFinalGrade)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinalGradeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.final_grade_item, parent, false)
        return FinalGradeViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: FinalGradeViewHolder, position: Int) {
        val currentItem = finalGrades[position]

        if (currentItem.grade == "-1") {
            holder.name.text = "---"
            holder.grade.text = "––"
        } else {
            holder.name.text = currentItem.name
            holder.grade.text = currentItem.grade // QwarkUtil.getGradeWithLeadingZero(currentItem.grade.toInt())
        }

        holder.multiplier.text = currentItem.note
        val linkDrawable = if (currentItem.courseId != -1) {
            R.drawable.link
        } else {
            0
        }
        holder.multiplier.setCompoundDrawablesWithIntrinsicBounds(linkDrawable, 0, 0, 0)
        holder.currentFinalGrade = currentItem
    }

    override fun getItemCount(): Int = finalGrades.size

    fun setFinalGrades(finalGrades: List<FinalGrade>) {
        this.finalGrades = finalGrades
        notifyDataSetChanged()
    }

    internal interface FinalGradeClickListener {
        fun onFinalGradeClick(finalGrade: FinalGrade)
//        fun onFinalGradeLongClick(finalGradeId: Int)
    }
}