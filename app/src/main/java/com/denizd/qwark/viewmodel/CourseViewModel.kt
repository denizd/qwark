package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.HistoricalAvg

class CourseViewModel(application: Application) : QwarkViewModel(application) {

    val allCourses: LiveData<List<CourseExam>> = repo.getCoursesByYearId(repo.prefs.getSchoolYear())

    fun getShowGradeAverage(): Boolean = repo.prefs.getShowGradeAverage()
    fun getGradeType(): Int = repo.prefs.getGradeType()
    fun getCourseSortType(): Int = repo.prefs.getCourseSortType()
    fun getSchoolYear(): Int = repo.prefs.getSchoolYear()
    fun getSchoolYearName(): String = repo.prefs.getSchoolYearName()


    fun getAveragesForCourses(courseId: Int): List<HistoricalAvg> = returnBlocking {
        repo.getAveragesForCourses(courseId)
    }

    fun insert(course: Course) = doAsync { repo.insert(course) }
    fun insert(historicalAvg: HistoricalAvg) = doAsync { repo.insert(historicalAvg) }
    fun update(course: Course)  = doAsync { repo.update(course) }
    fun delete(course: Course) = doAsync { repo.delete(course) }

    fun getCourse(courseId: Int): CourseExam = returnBlocking { repo.getCourse(courseId) }
}