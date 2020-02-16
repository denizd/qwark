package com.denizd.qwark.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.denizd.qwark.util.QwarkPreferences
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.Participation
import com.denizd.qwark.model.SchoolDay
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.util.round
import com.denizd.qwark.util.roundToInt
import java.util.*

class ParticipationRepository(app: Application) {

    private val days: Array<String> = arrayOf("mon", "tue", "wed", "thu", "fri")
    private val dao: QwarkDao = QwarkDatabase.getInstance(app).dao()
    private val prefs: QwarkPreferences = QwarkPreferences.getPrefs(app.applicationContext)
    // monday: 1, increment by 1 for each day
    val dayId: Int = when (prefs.getParticipationDay()) {
        0 -> if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 in 1..5)
            Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 else 1
        else -> prefs.getParticipationDay()
    }
    val allCoursesForToday: LiveData<List<Course>> = dao.getCoursesByDayType(getDayType(), prefs.getSchoolYear())

    fun initParticipationTables() {
        for (day in days) {
            dao.insert(SchoolDay(day, 0, prefs.getSchoolYear()))
        }
    }

    fun getParticipationCount(participations: List<Participation>) = if (prefs.getParticipationDisplay() == 0) {
        arrayOf(
            (participations.map { it.timesHandRaised }.sum().toDouble() / participations.size.toDouble()).round(),
            (participations.map { it.timesSpoken }.sum().toDouble() / participations.size.toDouble()).round()
        )
    } else {
        arrayOf(
            (participations.map { it.timesHandRaised }.sum()),
            (participations.map { it.timesSpoken }.sum())
        )
    }

    fun getDayType(): String = if (dayId in 1..5) days[dayId - 1] else days[1]
    fun getDays(): List<SchoolDay> = dao.getDays(prefs.getSchoolYear())
    fun getCourses(): List<CourseExam> = QwarkUtil.getCoursesSorted(
        dao.getCoursesByYearIdAsList(prefs.getSchoolYear()),
        prefs.getCourseSortType()
    )
    fun link(courseId: Int) { dao.link(courseId, getDayType(), prefs.getSchoolYear()) }
    fun removeLink(relationId: Int) { dao.removeLink(relationId) }
    fun getAllParticipationsForCourse(courseId: Int): LiveData<List<Participation>> =
        dao.getAllParticipationsForCourse(courseId)
    fun insert(timesHandRaised: Int, timesSpoken: Int, time: Long, courseId: Int) {
        dao.insert(
            Participation(
                timesHandRaised,
                timesSpoken,
                time,
                0,
                courseId,
                dayId
            )
        )
    }
    fun delete(participation: Participation) { dao.delete(participation) }
    fun updateParticipation(participation: String, courseId: Int) { dao.updateParticipation(participation, courseId) }
    fun getSchoolYearName() = prefs.getSchoolYearName()
}