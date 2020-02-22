package com.denizd.qwark.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class QwarkPreferences private constructor(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val edit: SharedPreferences.Editor = prefs.edit()

    companion object {
        private var qwarkPrefs: QwarkPreferences? = null
        fun getPrefs(context: Context): QwarkPreferences {
            if (qwarkPrefs == null) {
                synchronized (QwarkPreferences::class) {
                    qwarkPrefs = QwarkPreferences(context)
                }
            }
            return qwarkPrefs!!
        }
    }

    fun getString(key: PreferenceKey, default: String = ""): String = prefs.getString(key.value, default) ?: default
    fun getInt(key: PreferenceKey, default: Int = 0): Int = prefs.getInt(key.value, default)
    fun getBool(key: PreferenceKey, default: Boolean = false): Boolean = prefs.getBoolean(key.value, default)

    fun putString(key: PreferenceKey, value: String): QwarkPreferences {
        edit.putString(key.value, value).apply()
        return this
    }
    fun putInt(key: PreferenceKey, value: Int): QwarkPreferences {
        edit.putInt(key.value, value).apply()
        return this
    }
    fun putBool(key: PreferenceKey, value: Boolean): QwarkPreferences {
        edit.putBoolean(key.value, value).apply()
        return this
    }

    fun getShowGradeAverage(): Boolean = getBool(PreferenceKey.SHOW_GRADE_AVERAGE)
    fun getGradeType(): Int = getInt(PreferenceKey.GRADE_TYPE)
    fun getCourseSortType(): Int = getInt(PreferenceKey.COURSE_SORT_TYPE, 1)
    fun setCourseSortType(value: Int) { putInt(PreferenceKey.COURSE_SORT_TYPE, value) }
    fun getSchoolYear(): Int = getInt(PreferenceKey.SCHOOL_YEAR_ID)
    fun getSchoolYearName(): String = getString(PreferenceKey.SCHOOL_YEAR_NAME)
    fun getScoreProfileName(): String = getString(PreferenceKey.SCORE_PROFILE_NAME)
    fun getScoreProfileId(): Int = getInt(PreferenceKey.SCORE_PROFILE_ID)
    fun getParticipationDisplay(): Int = getInt(PreferenceKey.PARTICIPATION_DISPLAY)
    fun getParticipationDay(): Int = getInt(PreferenceKey.PARTICIPATION_DAY)

    fun setParticipationDisplay(value: Int) { putInt(PreferenceKey.PARTICIPATION_DISPLAY, value) }
    fun setParticipationDay(value: Int) { putInt(PreferenceKey.PARTICIPATION_DAY, value) }
}