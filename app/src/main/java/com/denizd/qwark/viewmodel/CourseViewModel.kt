package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.database.CourseRepository
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.HistoricalAvg
import com.denizd.qwark.util.QwarkPreferences

class CourseViewModel(application: Application) : QwarkViewModel(application) {

    private val repo = CourseRepository(application)
    private val prefs: QwarkPreferences = QwarkPreferences.getPrefs(application)
    val allCourses: LiveData<List<CourseExam>> = repo.getCoursesByYearId(getSchoolYear())

    fun getShowGradeAverage(): Boolean = prefs.getShowGradeAverage()
    fun getGradeType(): Int = prefs.getGradeType()
    fun getCourseSortType(): Int = prefs.getCourseSortType()
    fun getSchoolYear(): Int = prefs.getSchoolYear()
    fun getSchoolYearName(): String = prefs.getSchoolYearName()

    fun insert(avg: HistoricalAvg) = doAsync { repo.insert(avg) }

    fun getAveragesForCourses(courseId: Int): List<HistoricalAvg> = returnBlocking {
        repo.getAveragesForCourses(courseId)
    }

    fun insert(course: Course) = doAsync { repo.insert(course) }
    fun update(course: Course)  = doAsync { repo.update(course) }
    fun delete(course: Course) = doAsync { repo.delete(course) }
}