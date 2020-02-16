package com.denizd.qwark.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.util.QwarkPreferences
import com.denizd.qwark.model.FinalGrade
import java.math.BigDecimal
import java.math.RoundingMode

class FinalsRepository(app: Application) {

    private val dao: QwarkDao = QwarkDatabase.getInstance(app).dao()
    private val prefs: QwarkPreferences = QwarkPreferences.getPrefs(app)

    fun getScoreProfileName(): String = prefs.getScoreProfileName()
    fun getFinalGrades(finalScoreId: Int): LiveData<List<FinalGrade>> = dao.getFinalGrades(finalScoreId)
    fun getFinalScoreId() = prefs.getScoreProfileId()
    fun getCourseSortType() = prefs.getCourseSortType()
    fun getGradeType() = prefs.getGradeType()

    fun updateFinalGradeAverage(courseId: Int, finalGradeId: Int) {
        val course = dao.getCourse(courseId)[0]
        dao.updateFinalGradeAverage(
            course.name,
            BigDecimal(course.average).setScale(0, RoundingMode.HALF_UP).toInt(),
            finalGradeId
        )
    }

    fun updateLinkedGrades() {
        val finalGrades = dao.getFinalGradesAsList(getFinalScoreId())
        for (finalGrade in finalGrades) {
            if (finalGrade.courseId != -1) {
                updateFinalGradeAverage(finalGrade.courseId, finalGrade.finalGradeId)
            }
        }
    }

    fun getSchoolYearName(courseId: Int) = dao.getSchoolYearName(courseId)

    fun getAllYears() = dao.getAllYears()
    fun getAllCourses() = dao.getAllCourseExams()

    fun getCourse(courseId: Int) = dao.getCourse(courseId)[0]

    fun update(finalGrade: FinalGrade) { dao.update(finalGrade) }
}