package com.denizd.qwark.viewmodel

import android.app.Application
import android.content.Context
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import com.denizd.qwark.R
import com.denizd.qwark.fragment.*
import com.denizd.qwark.fragment.CourseFragment
import com.denizd.qwark.fragment.FinalsFragment
import com.denizd.qwark.fragment.NoteFragment
import com.denizd.qwark.fragment.ParticipationFragment
import com.denizd.qwark.fragment.SettingsFragment
import com.denizd.qwark.util.QwarkUtil

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(app.applicationContext)

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
        when (prefs.getInt("dark_mode", 2)) {
            0 ->        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 ->        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else ->     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        val barColour = ContextCompat.getColor(context, R.color.colorBackground)
        window.navigationBarColor = barColour
        window.statusBarColor = barColour
    }

    fun checkForFirstTimeYearNotice(context: Context) {
        if (prefs.getBoolean("first_launch", true)) {
            QwarkUtil.createInfoDialog(
                context,
                app.getString(R.string.alert_create_school_year_title),
                app.getString(R.string.alert_create_school_year_desc)
            )
            prefs.edit().putBoolean("first_launch", false).apply()
        }
    }
}