package com.denizd.qwark.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class QwarkPreferences(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val edit: SharedPreferences.Editor = prefs.edit()

    fun getString(key: PreferenceKey, default: String = ""): String = prefs.getString(key.value, default) ?: default
    fun getInt(key: PreferenceKey, default: Int = 0): Int = prefs.getInt(key.value, default)
    fun getBool(key: PreferenceKey, default: Boolean = false): Boolean = prefs.getBoolean(key.value, default)

    fun setString(key: PreferenceKey, value: String): QwarkPreferences {
        edit.putString(key.value, value).apply()
        return this
    }
    fun setInt(key: PreferenceKey, value: Int): QwarkPreferences {
        edit.putInt(key.value, value).apply()
        return this
    }
    fun setBool(key: PreferenceKey, value: Boolean): QwarkPreferences {
        edit.putBoolean(key.value, value).apply()
        return this
    }

    // TODO remove all of these
    fun getShowGradeAverage(): Boolean = getBool(PreferenceKey.SHOW_GRADE_AVERAGE)
    fun getGradeType(): Int = getInt(PreferenceKey.GRADE_TYPE)
    fun getCourseSortType(): Int = getInt(PreferenceKey.COURSE_SORT_TYPE, 1)
    fun getSchoolYear(): Int = getInt(PreferenceKey.SCHOOL_YEAR_ID)
    fun getSchoolYearName(): String = getString(PreferenceKey.SCHOOL_YEAR_NAME)
    fun getScoreProfileName(): String = getString(PreferenceKey.SCORE_PROFILE_NAME)
    fun getScoreProfileId(): Int = getInt(PreferenceKey.SCORE_PROFILE_ID)
    fun getParticipationDisplay(): Int = getInt(PreferenceKey.PARTICIPATION_DISPLAY)
    fun getParticipationDay(): Int = getInt(PreferenceKey.PARTICIPATION_DAY)
    fun getAppTheme(): Int = getInt(PreferenceKey.APP_THEME, 2)
    fun getFinalScoreId(): Int = getInt(PreferenceKey.SCORE_PROFILE_ID)
    fun getFirstLaunch(): Boolean = getBool(PreferenceKey.FIRST_LAUNCH)
    fun getCurrentSchoolYearId(): Int = getInt(PreferenceKey.SCHOOL_YEAR_ID)

    fun setCourseSortType(value: Int) { setInt(PreferenceKey.COURSE_SORT_TYPE, value) }
    fun setParticipationDisplay(value: Int) { setInt(PreferenceKey.PARTICIPATION_DISPLAY, value) }
    fun setParticipationDay(value: Int) { setInt(PreferenceKey.PARTICIPATION_DAY, value) }
    fun setAppTheme(value: Int) { setInt(PreferenceKey.APP_THEME, value) }
    fun setFirstLaunch(value: Boolean) { setBool(PreferenceKey.FIRST_LAUNCH, value) }
    fun setShowGradeAverage(value: Boolean) { setBool(PreferenceKey.SHOW_GRADE_AVERAGE, value) }
    fun setGradeType(value: Int) { setInt(PreferenceKey.GRADE_TYPE, value) }
}