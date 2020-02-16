package com.denizd.qwark.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

internal class QwarkPreferences private constructor(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val edit: SharedPreferences.Editor = prefs.edit()

    companion object {
        private var qwarkPrefs: QwarkPreferences? = null
        internal fun getPrefs(context: Context): QwarkPreferences {
            if (qwarkPrefs == null) {
                synchronized (QwarkPreferences::class) {
                    qwarkPrefs = QwarkPreferences(context)
                }
            }
            return qwarkPrefs!!
        }
    }

    internal fun getString(key: PreferenceKey, default: String = ""): String = prefs.getString(key.value, default) ?: default
    internal fun getInt(key: PreferenceKey, default: Int = 0): Int = prefs.getInt(key.value, default)
    internal fun getBool(key: PreferenceKey, default: Boolean = false): Boolean = prefs.getBoolean(key.value, default)

    internal fun putString(key: PreferenceKey, value: String): QwarkPreferences {
        edit.putString(key.value, value).apply()
        return this
    }
    internal fun putInt(key: PreferenceKey, value: Int): QwarkPreferences {
        edit.putInt(key.value, value).apply()
        return this
    }
    internal fun putBool(key: PreferenceKey, value: Boolean): QwarkPreferences {
        edit.putBoolean(key.value, value).apply()
        return this
    }

    internal fun getShowGradeAverage(): Boolean = getBool(PreferenceKey.SHOW_GRADE_AVERAGE)
    internal fun getGradeType(): Int = getInt(PreferenceKey.GRADE_TYPE)
    internal fun getCourseSortType(): Int = getInt(PreferenceKey.COURSE_SORT_TYPE, 1)
    internal fun setCourseSortType(value: Int) { putInt(PreferenceKey.COURSE_SORT_TYPE, value) }
    internal fun getSchoolYear(): Int = getInt(PreferenceKey.SCHOOL_YEAR_ID)
    internal fun getSchoolYearName(): String = getString(PreferenceKey.SCHOOL_YEAR_NAME)
    internal fun getScoreProfileName(): String = getString(PreferenceKey.SCORE_PROFILE_NAME)
    internal fun getScoreProfileId(): Int = getInt(PreferenceKey.SCORE_PROFILE_ID)
    fun getParticipationDisplay(): Int = getInt(PreferenceKey.PARTICIPATION_DISPLAY)
    fun getParticipationDay(): Int = getInt(PreferenceKey.PARTICIPATION_DAY)

    fun setParticipationDisplay(value: Int) { putInt(PreferenceKey.PARTICIPATION_DISPLAY, value) }
    fun setParticipationDay(value: Int) { putInt(PreferenceKey.PARTICIPATION_DAY, value) }
}