package com.denizd.qwark.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.model.*
import com.denizd.qwark.util.*
import java.util.*

class QwarkRepository(app: Application) {

    private val dao: QwarkDao = QwarkDatabase.getInstance(app.applicationContext).dao()
    val prefs: QwarkPreferences = QwarkPreferences(app.applicationContext)

    // misc
    private val days: Array<String> = arrayOf("mon", "tue", "wed", "thu", "fri")
    // monday: 1, increment by 1 for each day
    val dayId: Int = when (prefs.getParticipationDay()) {
        0 -> with (Calendar.getInstance()) {
            if (get(Calendar.DAY_OF_WEEK) - 1 in 1..5) get(Calendar.DAY_OF_WEEK) - 1 else 1
        }
        else -> prefs.getParticipationDay()
    }
    fun initParticipationTables() {
        for (day in days) {
            dao.insert(SchoolDay(day, 0, prefs.getSchoolYear()))
        }
    }
    fun link(courseId: Int) { dao.link(courseId, getDayType(), prefs.getSchoolYear()) }
    fun removeLink(relationId: Int) { dao.removeLink(relationId) }
    fun copyCourses(yearId: Int) {
        val currentSchoolYearId = prefs.getCurrentSchoolYearId()
        val courses = dao.getCoursesByYearIdAsList(yearId)
        for (course in courses) {
            dao.insert(
                Course(
                    name = course.name,
                    advanced = course.advanced,
                    icon = course.icon,
                    colour = course.colour,
                    average = if (prefs.getGradeType() == QwarkUtil.GRADE_PRECISE) "0.00" else "0",
                    oralWeighting = course.oralWeighting,
                    gradeCount = course.gradeCount,
                    time = System.currentTimeMillis(),
                    courseId = 0,
                    yearId = currentSchoolYearId
                )
            )
        }
    }
    fun initNewFinalScore(name: String) {
        val finalScoreId = getMaxFinalScoreId(true)
        val finalGrade = FinalGrade("", "-1", "basic", "x 1", -1, 0, finalScoreId)
        dao.insert(ScoreProfile(name, finalScoreId))

        val values = ArrayList<FinalGrade>()
        repeat(24) { values.add(finalGrade.copy(type = "basic", note = "x 1")) }
        repeat(2) {
            repeat(4) { values.add(finalGrade.copy(type = "adv", note = "x 2")) }
            values.add(finalGrade.copy(note = "x 1"))
        }
        for (i in 1..5) {
            values.add(finalGrade.copy(type = "exam", note = "P$i x ${if (i == 5) "2" else "5"}"))
        }

        dao.insertAll(values.toTypedArray())
    }

    // insert
    fun insert(course: Course) { dao.insert(course) }
    fun insert(grade: Grade) { dao.insert(grade) }
    fun insert(note: Note) { dao.insert(note) }
    fun insert(historicalAvg: HistoricalAvg) { dao.insert(historicalAvg) }
    fun insert(schoolYear: SchoolYear) { dao.insert(schoolYear) }
    fun insert(scoreProfile: ScoreProfile) { dao.insert(scoreProfile) }
    fun insert(schoolDay: SchoolDay) { dao.insert(schoolDay) }
    fun insert(participation: Participation) { dao.insert(participation) }

    // insert multiple
    fun insertAll(finalGrades: Array<FinalGrade>) { dao.insertAll(finalGrades) }

    // set
    fun setCurrentScoreProfile(scoreProfile: ScoreProfile) {
        prefs.setInt(PreferenceKey.SCORE_PROFILE_ID, scoreProfile.scoreProfileId)
            .setString(PreferenceKey.SCORE_PROFILE_NAME, scoreProfile.name)
    }

    fun setCurrentSchoolYear(schoolYear: SchoolYear) {
        prefs.setInt(PreferenceKey.SCHOOL_YEAR_ID, schoolYear.yearId)
            .setString(PreferenceKey.SCHOOL_YEAR_NAME, schoolYear.year)
    }

    // update
    fun update(course: Course) { dao.update(course) }
    fun update(finalGrade: FinalGrade) { dao.update(finalGrade) }
    fun updateLinkedGrades() {
        for (finalGrade in dao.getFinalGradesAsList(prefs.getFinalScoreId())) {
            if (finalGrade.courseId != -1) {
                updateFinalGradeAverage(finalGrade.courseId, finalGrade.finalGradeId)
            }
        }
    }
    fun updateFinalGradeAverage(courseId: Int, finalGradeId: Int) {
        dao.getCourse(courseId).also { course ->
            dao.updateFinalGradeAverage(
                course.name,
                course.average.roundToInt(),
                finalGradeId
            )
        }
    }
    fun updateAverage(average: String, gradeCount: Int, courseId: Int) { dao.updateAverage(average, gradeCount, courseId) }
    fun update(grade: Grade) { dao.update(grade) }
    fun update(note: Note) { dao.update(note) }
    fun updateDismissed(newDismissedStatus: Boolean, noteId: Int) { dao.updateDismissed(newDismissedStatus, noteId) }
    fun updateParticipation(participation: String, courseId: Int) {
        dao.updateParticipation(participation, courseId)
    }
    fun updateParticipation(timesHandRaised: Int, timesSpoken: Int, participationId: Int) {
        dao.updateParticipation(timesHandRaised, timesSpoken, participationId)
    }
    fun updateSchoolYear(name: String) {
        dao.update(SchoolYear(name, prefs.getCurrentSchoolYearId()))
        setCurrentSchoolYear(SchoolYear(name, prefs.getCurrentSchoolYearId()))
    }

    fun updateScoreProfile(name: String) {
        dao.update(ScoreProfile(name, prefs.getScoreProfileId()))
        setCurrentScoreProfile(ScoreProfile(name, prefs.getScoreProfileId()))
    }

    // delete
    fun delete(course: Course) { dao.delete(course) }
    fun deleteGrade(gradeId: Int) { dao.deleteGrade(gradeId) }
    fun deleteNote(noteId: Int) { dao.deleteNote(noteId) }
    fun delete(participation: Participation) { dao.delete(participation) }
    fun deleteSchoolYear() {
        dao.deleteSchoolYear(prefs.getCurrentSchoolYearId())
        setCurrentSchoolYear(SchoolYear("", 0))
    }

    fun deleteScoreProfile() {
        dao.deleteScoreProfile(prefs.getScoreProfileId())
        setCurrentScoreProfile(ScoreProfile("", 0))
    }

    // live data
    val allNotes: LiveData<List<Note>> = dao.allNotes
    val allCoursesForToday: LiveData<List<Course>> = dao.getCoursesByDayType(getDayType(), prefs.getSchoolYear())
    val allYears: LiveData<List<SchoolYear>> = dao.allYears
    val allScoreProfiles: LiveData<List<ScoreProfile>> = dao.allScoreProfiles

    // get
    fun getCourse(courseId: Int): CourseExam = dao.getCourse(courseId)
    fun getSchoolYearName(courseId: Int) = dao.getSchoolYearName(courseId)
    fun getFinalGrade(finalGradeId: Int) = dao.getFinalGrade(finalGradeId)
    fun getGrade(gradeId: Int) = dao.getGrade(gradeId)
    fun getNote(noteId: Int) = dao.getNote(noteId)
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
    private fun getMaxFinalScoreId(increment: Boolean): Int {
        if (increment) {
            prefs.setInt(PreferenceKey.MAX_SCORE_PROFILE_ID, getMaxFinalScoreId(false) + 1)
                .setInt(PreferenceKey.SCORE_PROFILE_ID, getMaxFinalScoreId(false) + 1)
        }
        return prefs.getInt(PreferenceKey.MAX_SCORE_PROFILE_ID)
    }

    // get multiple
    fun getCoursesByYearId(yearId: Int): LiveData<List<CourseExam>> = dao.getCoursesByYearId(yearId)
    fun getAllCourses() = dao.getAllCourseExams()
    fun getAveragesForCourses(courseId: Int): List<HistoricalAvg> = dao.getAveragesForCourses(courseId)
    fun getFinalGrades(finalScoreId: Int): LiveData<List<FinalGrade>> = dao.getFinalGrades(finalScoreId)
    fun getAllYears(): List<SchoolYear> = dao.getAllYears()
    fun getGradesByForeignKey(courseId: Int): LiveData<List<Grade>> = dao.getGradesByForeignKey(courseId)
    fun getNotesForCategory(category: String): LiveData<List<Note>> = dao.notesForCategory(category)
    fun getAllCategories(): List<String> = dao.getAllCategories()
    fun getDays(): List<SchoolDay> = dao.getDays(prefs.getSchoolYear())
    fun getCourses(): List<CourseExam> =
        dao.getCoursesByYearIdAsList(prefs.getSchoolYear()).getSorted(prefs.getCourseSortType())
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
}