package com.denizd.qwark.util

import android.content.Context
import com.denizd.qwark.R
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.Grade
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.RoundingMode

fun Double.round(scale: Int = 2): Double = BigDecimal(this).setScale(scale, RoundingMode.HALF_UP).toDouble()
fun Double.roundToInt(): Int = round(0).toInt()
fun BigDecimal.roundToInt(): Int = setScale(0, RoundingMode.HALF_UP).toInt()
fun String.roundToInt(): Int = BigDecimal(this.toDouble()).setScale(0, RoundingMode.HALF_UP).toInt()
fun String.roundToDouble(): Double = BigDecimal(this.toDouble()).setScale(2, RoundingMode.HALF_UP).toDouble()
fun Double.roundToGrade(): String {
    val result = ((17 - this) / 3).round().toString()
    return if (result.toDouble() < 1.0) "1.0" else result
}

fun List<Grade>.calculateAverage(oralWeighting: Int): String = if (this.isEmpty()) {
    BigDecimal(0.0).setScale(2, RoundingMode.HALF_UP)
} else {
    val oralGrades = this.filter { it.verbal }
    val writtenGrades = this.filter { !it.verbal }

    var oralGrade = 0.0
    var oralPercentage = 0.0
    var writtenGrade = 0.0
    var writtenPercentage = 0.0
    for (g in oralGrades) {
        oralGrade += (g.grade.toDouble() * (g.weighting.toDouble() / 100.0))
        oralPercentage += g.weighting.toDouble()
    }
    for (g in writtenGrades) {
        writtenGrade += (g.grade.toDouble() * (g.weighting.toDouble() / 100.0))
        writtenPercentage += g.weighting.toDouble()
    }

    if (oralPercentage != 100.0) {
        oralGrade = (oralGrade / (oralPercentage / 100.0))
    }
    if (writtenPercentage != 100.0) {
        writtenGrade = (writtenGrade / (writtenPercentage / 100.0))
    }

    // return value for totalGrade
    BigDecimal(
        try {
            when {
                writtenPercentage == 0.0 -> oralGrade
                oralPercentage == 0.0 -> writtenGrade
                else -> (oralGrade * (oralWeighting.toDouble() / 100.0)) + (writtenGrade * ((100.0 - oralWeighting.toDouble()) / 100.0))
            }
        } catch (n: NumberFormatException) {
            0.0
        }
    ).setScale(2, RoundingMode.HALF_UP)
}.toString()

fun List<CourseExam>.calculateTotalAverage(context: Context, gradeType: Int, schoolYearName: String, showGradeAverage: Boolean): String {
    var totalAvg = 0.0
    var totalCourses = 0.0
    var totalGradeCount = 0
    for (course in this) {
        if (course.gradeCount > 0) {
            val courseAverage = if (gradeType == QwarkUtil.GRADE_PRECISE) course.average.toDouble() else course.average.roundToInt().toDouble()
            totalAvg += if (course.advanced) courseAverage * 2.0 else courseAverage
            totalCourses += if (course.advanced) 2.0 else 1.0
            totalGradeCount += 1
        }
    }
    val finalAverage = if (totalCourses > 0.0) {
        totalAvg /= totalCourses
        totalAvg.round()
    } else {
        0.0
    }
    return when {
        schoolYearName.isBlank() -> context.getString(R.string.no_school_year_selected)
        totalGradeCount == 0 -> context.getString(R.string.no_grades_entered_placeholder, schoolYearName)
        gradeType in arrayOf(QwarkUtil.GRADE_PRECISE, QwarkUtil.GRADE_ROUNDED) -> context.getString(
            if (showGradeAverage) R.string.average_and_grade_for_year else R.string.average_for_year,
            schoolYearName,
            finalAverage,
            finalAverage.roundToGrade()
        )
        else -> context.getString(
            R.string.average_grade_for_year,
            schoolYearName,
            finalAverage.toString().getUserDefinedAverage(gradeType)
        )
    }
}

fun String.getUserDefinedAverage(gradeType: Int?, isGradeAdapter: Boolean = false): String = when (gradeType) {
    QwarkUtil.GRADE_PRECISE, null -> this
    QwarkUtil.GRADE_ROUNDED -> {
        if (isGradeAdapter)
            QwarkUtil.getGradeWithLeadingZero(this.roundToInt())
        else
            this.roundToInt().toString()
    }
    else -> QwarkUtil.getNewGradeType(this.roundToInt(), gradeType)
}