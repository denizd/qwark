package com.denizd.qwark.database

import android.app.Application

import androidx.lifecycle.LiveData
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.HistoricalAvg

internal class CourseRepository(app: Application) {

    private val dao: QwarkDao = QwarkDatabase.getInstance(app).dao()
//    val allCourses: LiveData<List<Course>> = dao.allCourses
//    val allHistoricalAverages: LiveData<List<HistoricalAvg>> = dao.allHistoricalAverages

    fun insert(course: Course) { dao.insert(course) }
    fun update(course: Course) { dao.update(course) }
    fun delete(course: Course) { dao.delete(course) }

    fun insert(avg: HistoricalAvg) { dao.insert(avg) }

    fun getCoursesByYearId(yearId: Int): LiveData<List<CourseExam>> = dao.getCoursesByYearId(yearId)
    fun getAveragesForCourses(courseId: Int): List<HistoricalAvg> = dao.getAveragesForCourses(courseId)

    fun getCourse(courseId: Int): CourseExam = dao.getCourse(courseId)[0]
}