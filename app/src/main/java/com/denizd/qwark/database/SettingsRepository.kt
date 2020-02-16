package com.denizd.qwark.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.util.PreferenceKey
import com.denizd.qwark.util.QwarkPreferences
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.FinalGrade
import com.denizd.qwark.model.ScoreProfile
import com.denizd.qwark.model.SchoolYear

class SettingsRepository(app: Application) {

    private val dao: QwarkDao = QwarkDatabase.getInstance(app).dao()
    private val prefs: QwarkPreferences = QwarkPreferences.getPrefs(app.applicationContext)
    val allYears: LiveData<List<SchoolYear>> = dao.allYears
    val allScoreProfiles: LiveData<List<ScoreProfile>> = dao.allScoreProfiles

    // Preference getters
    fun getAppTheme(): Int = prefs.getInt(PreferenceKey.APP_THEME, 2)
    fun getShowGradeAverage(): Boolean = prefs.getBool(PreferenceKey.SHOW_GRADE_AVERAGE)
    fun getGradeType(): Int = prefs.getInt(PreferenceKey.GRADE_TYPE)
    fun getCurrentSchoolYear(): String = prefs.getString(PreferenceKey.SCHOOL_YEAR_NAME)
    fun getCurrentSchoolYearId(): Int = prefs.getInt(PreferenceKey.SCHOOL_YEAR_ID)
    private fun getScoreProfileId(): Int = prefs.getInt(PreferenceKey.SCORE_PROFILE_ID, 0)
    fun getScoreProfileName(): String = prefs.getString(PreferenceKey.SCORE_PROFILE_NAME)
    fun getParticipationDisplay(): Int = prefs.getParticipationDisplay()
    fun getParticipationDay(): Int = prefs.getParticipationDay()

    private fun getMaxFinalScoreId(increment: Boolean): Int {
        if (increment) {
            prefs.putInt(PreferenceKey.MAX_SCORE_PROFILE_ID, getMaxFinalScoreId(false) + 1)
                .putInt(PreferenceKey.SCORE_PROFILE_ID, getMaxFinalScoreId(false) + 1)
        }
        return prefs.getInt(PreferenceKey.MAX_SCORE_PROFILE_ID)
    }

    // Preference setters
    fun setAppTheme(value: Int) { prefs.putInt(PreferenceKey.APP_THEME, value) }
    fun setShowGradeAverage(value: Boolean) { prefs.putBool(PreferenceKey.SHOW_GRADE_AVERAGE, value) }
    fun setGradeType(value: Int) { prefs.putInt(PreferenceKey.GRADE_TYPE, value) }
    fun setParticipationDisplay(value: Int) { prefs.setParticipationDisplay(value) }
    fun setParticipationDay(value: Int) { prefs.setParticipationDay(value) }

    fun setCurrentScoreProfile(scoreProfile: ScoreProfile) {
        prefs.putInt(PreferenceKey.SCORE_PROFILE_ID, scoreProfile.scoreProfileId)
            .putString(PreferenceKey.SCORE_PROFILE_NAME, scoreProfile.name)
    }

    fun setCurrentSchoolYear(schoolYear: SchoolYear) {
        prefs.putInt(PreferenceKey.SCHOOL_YEAR_ID, schoolYear.yearId)
            .putString(PreferenceKey.SCHOOL_YEAR_NAME, schoolYear.year)
    }

    // Database functions
    fun insert(schoolYear: SchoolYear) { dao.insert(schoolYear) }
    fun insert(scoreProfile: ScoreProfile) { dao.insert(scoreProfile) }

    fun initNewFinalScore(name: String) {
        val finalScoreId = getMaxFinalScoreId(true)
        dao.insert(ScoreProfile(name, finalScoreId))

        val values = ArrayList<FinalGrade>()
        for (i in 0 until 24) {
            values.add(FinalGrade("", "-1", "basic", "x 1", -1, 0, finalScoreId))
        }

        for (i in 0 until 3) {
            values.add(FinalGrade("", "-1", "adv", "x 2", -1, 0, finalScoreId))
        }
        values.add(FinalGrade("", "-1", "adv", "x 1", -1, 0, finalScoreId))
        for (i in 0 until 3) {
            values.add(FinalGrade("", "-1", "adv", "x 2", -1, 0, finalScoreId))
        }
        values.add(FinalGrade("", "-1", "adv", "x 1", -1, 0, finalScoreId))

        for (i in 1..5) {
            values.add(FinalGrade("", "-1", "exam", "P$i x ${if (i == 5) "2" else "5"}", -1, 0, finalScoreId))
        }

        dao.insertAll(values.toTypedArray())
    }

    fun updateSchoolYear(name: String) {
        dao.update(SchoolYear(name, getCurrentSchoolYearId()))
        setCurrentSchoolYear(SchoolYear(name, getCurrentSchoolYearId()))
    }

    fun updateScoreProfile(name: String) {
        dao.update(ScoreProfile(name, getScoreProfileId()))
        setCurrentScoreProfile(ScoreProfile(name, getScoreProfileId()))
    }

    fun deleteSchoolYear() {
        dao.deleteSchoolYear(getCurrentSchoolYearId())
        setCurrentSchoolYear(SchoolYear("", 0))
    }

    fun deleteScoreProfile() {
        dao.deleteScoreProfile(getScoreProfileId())
        setCurrentScoreProfile(ScoreProfile("", 0))
    }

    fun copyCourses(yearId: Int) {
        val currentSchoolYearId = getCurrentSchoolYearId()
        val courses = dao.getCoursesByYearIdAsList(yearId)
        for (course in courses) {
            dao.insert(
                Course(
                    name = course.name,
                    advanced = course.advanced,
                    icon = course.icon,
                    colour = course.colour,
                    average = if (getGradeType() == QwarkUtil.GRADE_PRECISE) "0.00" else "0",
                    oralWeighting = course.oralWeighting,
                    gradeCount = course.gradeCount,
                    time = System.currentTimeMillis(),
                    courseId = 0,
                    yearId = currentSchoolYearId
                )
            )
        }
    }
}