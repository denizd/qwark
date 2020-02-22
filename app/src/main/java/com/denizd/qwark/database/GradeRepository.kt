package com.denizd.qwark.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.model.Grade
import com.denizd.qwark.model.HistoricalAvg
import com.denizd.qwark.util.*
import com.denizd.qwark.util.QwarkPreferences

class GradeRepository(app: Application) {

    private val dao: QwarkDao = QwarkDatabase.getInstance(app).dao()
    private val prefs: QwarkPreferences = QwarkPreferences.getPrefs(app)

    fun getGradeType(): Int = prefs.getInt(PreferenceKey.GRADE_TYPE)

    // TODO this might crash
    fun getCourse(courseId: Int) = dao.getCourse(courseId)[0]

    fun insert(grade: Grade) { dao.insert(grade) }
    fun update(grade: Grade) { dao.update(grade) }
    fun delete(gradeId: Int) { dao.deleteGrade(gradeId) }

    fun insert(avg: HistoricalAvg) { dao.insert(avg) }
    fun updateAverage(average: String, gradeCount: Int, courseId: Int) {
        dao.updateAverage(average, gradeCount, courseId)
    }

    fun getGradesByForeignKey(courseId: Int): LiveData<List<Grade>> = dao.getGradesByForeignKey(courseId)
    fun getGrade(gradeId: Int) = dao.getGrade(gradeId)[0]
}