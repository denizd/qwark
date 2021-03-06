package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.model.Grade
import com.denizd.qwark.model.HistoricalAvg

class GradeViewModel(application: Application) : QwarkViewModel(application) {

    fun getGradeType(): Int = repo.prefs.getGradeType()

    fun getCourse(courseId: Int) = returnBlocking { repo.getCourse(courseId) }

    fun getGradesByForeignKey(courseId: Int): LiveData<List<Grade>> = returnBlocking {
        repo.getGradesByForeignKey(courseId)
    }

    fun insert(grade: Grade) = doAsync { repo.insert(grade) }
    fun update(grade: Grade) = doAsync { repo.update(grade) }
    fun updateAverage(average: String, gradeCount: Int, courseId: Int) = doAsync {
        repo.updateAverage(average, gradeCount, courseId)
    }
    fun deleteGrade(gradeId: Int) = doAsync { repo.deleteGrade(gradeId) }

    fun insert(avg: HistoricalAvg) = doAsync { repo.insert(avg) }
    fun getGrade(gradeId: Int) = returnBlocking { repo.getGrade(gradeId) }
}