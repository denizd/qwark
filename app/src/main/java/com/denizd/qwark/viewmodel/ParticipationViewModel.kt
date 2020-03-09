package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.Participation

class ParticipationViewModel(app: Application) : QwarkViewModel(app) {

    val allCoursesForToday: LiveData<List<Course>> = repo.allCoursesForToday
    val dayId: Int = repo.dayId

    fun getCourses(): List<CourseExam> = returnBlocking { repo.getCourses() }
    fun link(courseId: Int) = doAsync { repo.link(courseId) }
    fun removeLink(relationId: Int) = doAsync { repo.removeLink(relationId) }
    fun delete(participation: Participation) = doAsync { repo.delete(participation) }
    fun getSchoolYearName() = repo.prefs.getSchoolYearName()
}