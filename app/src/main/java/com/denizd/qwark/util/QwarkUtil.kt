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

    private fun Double.getGradeType(map: Map<Int, String>): String = map[this.roundToInt()] ?: ""

    fun getNewGradeType(grade: Int, type: Int): String = when (type) {
        GRADE_NUMBERED -> numberedGrades[grade] ?: "error"
        GRADE_LETTERED -> letteredGrades[grade] ?: "error"
        else -> "error"
    }

    fun getNumberForLetter(grade: String): Int = lettersToPoints[grade] ?: -1

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

    private const val SORT_COURSES_TIME_ASC = 0
    private const val SORT_COURSES_TIME_DESC = 1
    private const val SORT_COURSES_NAME_ASC = 2
    private const val SORT_COURSES_NAME_DESC = 3
    private const val SORT_COURSES_ADV = 4
    private const val SORT_COURSES_BASIC = 5
    private const val SORT_COURSES_AVG_ASC = 6
    private const val SORT_COURSES_AVG_DESC = 7
    private const val SORT_COURSES_NEXT_EXAM = 8

    fun getCoursesSorted(courses: List<CourseExam>, sortType: Int): List<CourseExam> = when (sortType) { // TODO check what ascending and descending really mean lol
        SORT_COURSES_TIME_ASC -> courses.asReversed()
        SORT_COURSES_TIME_DESC -> courses
        SORT_COURSES_NAME_ASC -> courses.sortedBy { c -> c.name }
        SORT_COURSES_NAME_DESC -> courses.sortedByDescending { c -> c.name }
        SORT_COURSES_ADV -> courses.sortedByDescending { c -> c.advanced }
        SORT_COURSES_BASIC -> courses.sortedBy { c -> c.advanced }
        SORT_COURSES_AVG_ASC -> courses.sortedBy { c -> c.average.toDouble() }
        SORT_COURSES_AVG_DESC -> courses.sortedByDescending { c -> c.average.toDouble() }
        else -> courses.sortedBy { c -> if (c.examTime > 1L) c.examTime else Long.MAX_VALUE } // NEXT_EXAM
    }

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

    fun putCourseInBundle(fragment: Fragment, course: CourseExam) {
        val bundle = Bundle(11).apply {
            putString("name", course.name)
            putBoolean("advanced", course.advanced)
            putString("icon", course.icon)
            putString("colour", course.colour)
            putString("average", course.average)
            putInt("oralWeighting", course.oralWeighting)
            putInt("gradeCount", course.gradeCount)
            putLong("time", course.time)
            putInt("courseId", course.courseId)
            putInt("yearId", course.yearId)
            putLong("examTime", course.examTime)
        }
        fragment.arguments = bundle
    }

    fun getCourseFromBundle(bundle: Bundle?) = CourseExam(
        name = bundle?.getString("name") ?: "",
        advanced = bundle?.getBoolean("advanced") ?: false,
        icon = bundle?.getString("icon") ?: "",
        colour = bundle?.getString("colour") ?: "",
        average = bundle?.getString("average") ?: "",
        oralWeighting = bundle?.getInt("oralWeighting") ?: 0,
        gradeCount = bundle?.getInt("gradeCount") ?: 0,
        time = bundle?.getLong("time") ?: 0L,
        courseId = bundle?.getInt("courseId") ?: 0,
        yearId = bundle?.getInt("yearId") ?: 0,
        examTime = bundle?.getLong("examTime") ?: 0L
    )

    fun putFinalGradeInBundle(fragment: Fragment, finalGrade: FinalGrade) {
        val bundle = Bundle(7).apply {
            putString("name", finalGrade.name)
            putInt("courseId", finalGrade.courseId)
            putString("grade", finalGrade.grade)
            putString("type", finalGrade.type)
            putString("note", finalGrade.note)
            putInt("finalGradeId", finalGrade.finalGradeId)
            putInt("scoreProfileId", finalGrade.scoreProfileId)
        }
        fragment.arguments = bundle
    }

    fun getFinalGradeFromBundle(bundle: Bundle?) = FinalGrade(
        name = bundle?.getString("name") ?: "",
        courseId = bundle?.getInt("courseId") ?: 0,
        grade = bundle?.getString("grade") ?: "",
        type = bundle?.getString("type") ?: "",
        note = bundle?.getString("note") ?: "",
        finalGradeId = bundle?.getInt("finalGradeId") ?: 0,
        scoreProfileId = bundle?.getInt("scoreProfileId") ?: 0
    )

    fun putGradeInBundle(fragment: Fragment, grade: Grade) {
        val bundle = Bundle(8).apply {
            putInt("grade", grade.grade)
            putString("note", grade.note)
            putBoolean("verbal", grade.verbal)
            putLong("time", grade.time)
            putInt("weighting", grade.weighting)
            putLong("examTime", grade.examTime)
            putInt("gradeId", grade.gradeId)
            putInt("courseId", grade.courseId)
        }
        fragment.arguments = bundle
    }

    fun getGradeFromBundle(bundle: Bundle?) = Grade(
        grade = bundle?.getInt("grade") ?: 0,
        note = bundle?.getString("note") ?: "",
        verbal = bundle?.getBoolean("verbal") ?: false,
        time = bundle?.getLong("time") ?: 0L,
        weighting = bundle?.getInt("weighting") ?: 0,
        examTime = bundle?.getLong("examTime") ?: 0L,
        gradeId = bundle?.getInt("gradeId") ?: 0,
        courseId = bundle?.getInt("courseId") ?: 0
    )

    fun putNoteInBundle(fragment: Fragment, note: Note) {
        val bundle = Bundle(5).apply {
            putString("content", note.content)
            putBoolean("dismissed", note.dismissed)
            putLong("time", note.time)
            putString("category", note.category)
            putInt("noteId", note.noteId)
        }
        fragment.arguments = bundle
    }

    fun getNoteFromBundle(bundle: Bundle?) = Note(
        content = bundle?.getString("content") ?: "",
        dismissed = bundle?.getBoolean("dismissed") ?: false,
        time = bundle?.getLong("time") ?: 0L,
        category = bundle?.getString("category") ?: "",
        noteId = bundle?.getInt("noteId") ?: 0
    )

    fun getCourseExamAsCourse(c: CourseExam): Course = Course(
        name = c.name,
        advanced = c.advanced,
        icon = c.icon,
        colour = c.colour,
        average = c.average,
        oralWeighting = c.oralWeighting,
        gradeCount = c.gradeCount,
        time = c.time,
        courseId = c.courseId,
        yearId = c.yearId
    )
}