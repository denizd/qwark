package com.denizd.qwark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.util.getUserDefinedAverage
import com.denizd.qwark.util.round
import com.denizd.qwark.util.roundToInt

class CourseAdapter(private var courses: List<CourseExam>,
                    private val onClickListener: CourseClickListener,
                    private val gradeType: Int?,
                    private val isParticipationFragment: Boolean = false) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(view: View, private val clickListener: CourseClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        val backgroundView: ConstraintLayout = view.findViewById(R.id.backgroundLayout)
        val titleTextView: TextView = view.findViewById(R.id.title)
        val averageTextView: TextView = view.findViewById(R.id.average)
        val imageView: ImageView = view.findViewById(R.id.image_view)
        lateinit var currentCourse: CourseExam

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onCourseClick(currentCourse)
        }

        override fun onLongClick(v: View?): Boolean {
            clickListener.onCourseLongClick(currentCourse)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.course_card, parent, false)
        return CourseViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentItem = courses[position]
        val context = holder.averageTextView.context

        holder.backgroundView.setBackgroundColor(
            context.getColor(if (currentItem.advanced) R.color.colorAccent else R.color.colorCardBackground)
        )
        val textColour = context.getColor(if (currentItem.advanced) R.color.colorTextInverted else R.color.colorText)

        val titleText = if (currentItem.advanced) context.getString(R.string.advanced_abbreviated_placeholder) else "%s"
        holder.titleTextView.text = String.format(titleText, currentItem.name)
        val averageText = when (currentItem.gradeCount) {
            0 -> context.getString(R.string.average_points_no_grade_placeholder)
            else -> when (gradeType) {
                QwarkUtil.GRADE_PRECISE, QwarkUtil.GRADE_ROUNDED ->
                    context.resources.getQuantityString(R.plurals.average_points_placeholder,
                        if (currentItem.average == "1.00") 1 else 2,
                        currentItem.average.getUserDefinedAverage(gradeType)
                    )
                else -> context.getString(R.string.average_grade_placeholder, currentItem.average.getUserDefinedAverage(gradeType))
            }
        }
        val examText = when (val timeLeft = ((currentItem.examTime.toDouble() - QwarkUtil.timeAtMidnight.toDouble()) / (1000.0 * 60.0 * 60.0 * 24.0)).roundToInt()) {
            0 -> context.getString(R.string.exam_today_placeholder)
            else -> {
                when {
                    timeLeft > 7 -> {
                        context.resources.getQuantityString(
                            R.plurals.exam_weeks_placeholder,
                            2,
                            (timeLeft.toDouble() / 7.0).round(1).toString()
                        )
                    }
                    timeLeft in 1..7 -> context.resources.getQuantityString(R.plurals.exam_days_placeholder, timeLeft, timeLeft.toString())
                    else -> ""
                }
            }
        }
        holder.averageTextView.text = if (isParticipationFragment) {
            val args = currentItem.participation.split('|')
            context.getString(R.string.participation_placeholder, args[0], args[1])
        } else buildString {
                append(averageText)
                append(examText)
        }

        if (currentItem.icon.isNotEmpty()) {
            holder.imageView.setImageDrawable(
                context.getDrawable(QwarkUtil.getDrawableIntForString(currentItem.icon))
            )
        }
        holder.titleTextView.setTextColor(textColour)
        holder.averageTextView.setTextColor(textColour)
        holder.imageView.setColorFilter(textColour)
        holder.currentCourse = currentItem // QwarkUtil.getCourseExamAsCourse(currentItem)
    }

    override fun getItemCount(): Int = courses.size

    fun setCourses(courses: List<CourseExam>) {
        this.courses = courses
        notifyDataSetChanged()
    }

    interface CourseClickListener {
        fun onCourseClick(course: CourseExam)
        fun onCourseLongClick(course: CourseExam)
    }
}