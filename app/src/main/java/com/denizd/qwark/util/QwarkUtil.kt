package com.denizd.qwark.util

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.denizd.qwark.R
import com.denizd.qwark.databinding.ConfirmationDialogBinding
import com.denizd.qwark.model.*
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

object QwarkUtil {

    val drawablesList: ArrayList<CourseIcon> by lazy {
        getDrawablesAsList()
    }

    private val drawables: Map<String, Int> = mapOf(
        "circle" to R.drawable.ic_circle,
        "arts" to R.drawable.ic_arts,
        "biology" to R.drawable.ic_biology,
        "chemistry" to R.drawable.ic_chemistry,
        "chinese" to R.drawable.ic_chinese,
        "cogwheel" to R.drawable.ic_cogwheel,
        "compsci" to R.drawable.ic_compsci,
        "drama" to R.drawable.ic_drama,
        "english" to R.drawable.ic_english,
        "french" to R.drawable.ic_french,
        "geography" to R.drawable.ic_geography,
        "german" to R.drawable.ic_german,
        "gll" to R.drawable.ic_gll,
        "help" to R.drawable.ic_help,
        "history" to R.drawable.ic_history,
        "idea" to R.drawable.ic_idea,
        "information" to R.drawable.ic_information,
        "latin" to R.drawable.ic_latin,
        "maths" to R.drawable.ic_maths,
        "music" to R.drawable.ic_music,
        "pe" to R.drawable.ic_pe,
        "pencil" to R.drawable.ic_pencil,
        "philosophy" to R.drawable.ic_philosophy,
        "physics" to R.drawable.ic_physics,
        "politics" to R.drawable.ic_politics,
        "religion" to R.drawable.ic_religion,
        "spanish" to R.drawable.ic_spanish,
        "tomato" to R.drawable.ic_tomato,
        "turkish" to R.drawable.ic_turkish,
        "wat" to R.drawable.ic_wat
    )

    private val numberedGrades: Map<Int, String> = mapOf(
        15 to "1+",
        14 to "1",
        13 to "1-",
        12 to "2+",
        11 to "2",
        10 to "2-",
        9 to "3+",
        8 to "3",
        7 to "3-",
        6 to "4+",
        5 to "4",
        4 to "4-",
        3 to "5+",
        2 to "5",
        1 to "5-",
        0 to "6"
    )

    private val letteredGrades: Map<Int, String> = mapOf(
        15 to "A+",
        14 to "A",
        13 to "A-",
        12 to "B+",
        11 to "B",
        10 to "B-",
        9 to "C+",
        8 to "C",
        7 to "C-",
        6 to "D+",
        5 to "D",
        4 to "D-",
        3 to "E+",
        2 to "E",
        1 to "E-",
        0 to "F"
    )

    private val lettersToPoints: Map<String, Int> = mapOf(
        "A+" to 15,
        "A" to 14,
        "A-" to 13,
        "B+" to 12,
        "B" to 11,
        "B-" to 10,
        "C+" to 9,
        "C" to 8,
        "C-" to 7,
        "D+" to 6,
        "D" to 5,
        "D-" to 4,
        "E+" to 3,
        "E" to 2,
        "E-" to 1,
        "F" to 0
    )

    private val gradesToPoints: Map<String, Int> = mapOf(
        "1+" to 15,
        "1" to 14,
        "1-" to 13,
        "2+" to 12,
        "2" to 11,
        "2-" to 10,
        "3+" to 9,
        "3" to 8,
        "3-" to 7,
        "4+" to 6,
        "4" to 5,
        "4-" to 4,
        "5+" to 3,
        "5" to 2,
        "5-" to 1,
        "6" to 0
    )

    val pointGradeArray: Array<String> = arrayOf(
        "0", "1", "2", "3", "4", "5", "6",
        "7", "8", "9", "10", "11", "12", "13", "14", "15"
    )
    private val numberedGradeArray: Array<String> = arrayOf(
        "6", "5-", "5", "5+", "4-", "4", "4+",
        "3-", "3", "3+", "2-", "2", "2+", "1-", "1", "1+"
    )
    private val letteredGradeArray: Array<String> = arrayOf(
        "F", "E-", "E", "E+", "D-", "D", "D+",
        "C-", "C", "C+", "B-", "B", "B+", "A-", "A", "A+"
    )

    fun getGradeArray(gradeType: Int): Array<String> = when (gradeType) {
        GRADE_NUMBERED -> numberedGradeArray
        GRADE_LETTERED -> letteredGradeArray
        else -> pointGradeArray
    }

    fun getNewGradeType(grade: Int, type: Int): String = when (type) {
        GRADE_NUMBERED -> numberedGrades[grade] ?: "error"
        GRADE_LETTERED -> letteredGrades[grade] ?: "error"
        else -> "error"
    }

    val timeAtMidnight: Long = GregorianCalendar().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    const val GRADE_PRECISE = 0
    const val GRADE_ROUNDED = 1
    const val GRADE_NUMBERED = 2
    const val GRADE_LETTERED = 3

    const val SORT_COURSES_TIME_ASC = 0
    const val SORT_COURSES_TIME_DESC = 1
    const val SORT_COURSES_NAME_ASC = 2
    const val SORT_COURSES_NAME_DESC = 3
    const val SORT_COURSES_ADV = 4
    const val SORT_COURSES_BASIC = 5
    const val SORT_COURSES_AVG_ASC = 6
    const val SORT_COURSES_AVG_DESC = 7
    const val SORT_COURSES_NEXT_EXAM = 8

    fun getGradeAsString(grade: Int, leadingZero: Boolean): String = (if (leadingZero && grade < 10) "0" else "") + grade
    fun getGradeWithLeadingZero(grade: Int) = "${if (grade < 10) "0" else ""}$grade"

    fun getSimpleDateTimeFormat(): SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.ROOT)
    fun getSimpleDateFormat(): SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    fun getDrawableIntForString(drawable: String): Int {
        return drawables[drawable] ?: 0
    }

    private fun getMapAsCourseIcon(entry: Map.Entry<String, Int>): CourseIcon {
        return CourseIcon(entry.key, entry.value)
    }

    private fun getDrawablesAsList(): ArrayList<CourseIcon> = ArrayList<CourseIcon>().also { list ->
        for (drawable in drawables) {
            list.add(getMapAsCourseIcon(drawable))
        }
    }

    fun getCreatedString(context: Context, time: Long): String {
        return context.getString(R.string.created_at, getSimpleDateTimeFormat().format(Date(time)))
    }

    fun createInfoDialog(context: Context, title: String, body: String) {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = View.inflate(context, R.layout.info_display_dialog, null)
        dialogView.findViewById<TextView>(R.id.info_title_textview).text = title
        dialogView.findViewById<TextView>(R.id.info_body_textview).text = body
        val dialog = dialogBuilder.setView(dialogView).create()
        dialogView.findViewById<MaterialButton>(R.id.confirm_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}