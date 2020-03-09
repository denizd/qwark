package com.denizd.qwark.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import com.denizd.qwark.R
import com.denizd.qwark.database.QwarkRepository
import com.denizd.qwark.fragment.*
import com.denizd.qwark.fragment.CourseFragment
import com.denizd.qwark.fragment.FinalsFragment
import com.denizd.qwark.fragment.NoteFragment
import com.denizd.qwark.fragment.ParticipationFragment
import com.denizd.qwark.fragment.SettingsFragment
import com.denizd.qwark.util.Dependencies
import com.denizd.qwark.util.QwarkUtil

class MainViewModel(val app: Application) : QwarkViewModel(app) {

    fun getFragmentType(type: Int): String = when (type) { // TODO replace with Fragment#tag
        R.id.courses -> "CourseFragment"
        R.id.notes -> "NoteFragment"
        R.id.participation -> "ParticipationFragment"
        R.id.finals -> "FinalsFragment"
        else -> "SettingsFragment"
    }

    fun getFragment(type: String): QwarkFragment = when (type) {
        "CourseFragment" -> CourseFragment()
        "NoteFragment" -> NoteFragment()
        "ParticipationFragment" -> ParticipationFragment()
        "FinalsFragment" -> FinalsFragment()
        else -> SettingsFragment()
    }

    fun applyTheme(window: Window, context: Context) {
        AppCompatDelegate.setDefaultNightMode(when (repo.prefs.getAppTheme()) {
            0 ->        AppCompatDelegate.MODE_NIGHT_NO
            1 ->        AppCompatDelegate.MODE_NIGHT_YES
            else ->     AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        })
        ContextCompat.getColor(context, R.color.colorBackground).also { barColour ->
            window.navigationBarColor = barColour
            window.statusBarColor = barColour
        }
    }

    fun checkForFirstTimeYearNotice(context: Context) {
        if (repo.prefs.getFirstLaunch()) {
            QwarkUtil.createInfoDialog(
                context,
                app.getString(R.string.alert_create_school_year_title),
                app.getString(R.string.alert_create_school_year_desc)
            )
            repo.prefs.setFirstLaunch(false)
        }
    }

    fun getExamNotificationTime(): Long = repo.prefs.getExamNotificationTime()
}