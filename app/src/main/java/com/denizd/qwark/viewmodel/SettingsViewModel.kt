package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.database.QwarkClient
import com.denizd.qwark.model.ScoreProfile
import com.denizd.qwark.model.SchoolYear

class SettingsViewModel(private val app: Application) : QwarkViewModel(app) {

    val allYears: LiveData<List<SchoolYear>> = repo.allYears
    val allScoreProfiles: LiveData<List<ScoreProfile>> = repo.allScoreProfiles

    // Preference getters
    fun getAppTheme() = repo.prefs.getAppTheme()
    fun getShowGradeAverage() = repo.prefs.getShowGradeAverage()
    fun getCourseSortType(): Int = repo.prefs.getCourseSortType()
    fun getGradeType() = repo.prefs.getGradeType()
    fun getScoreProfileName() = repo.prefs.getScoreProfileName()
    fun getSchoolYearName(): String = repo.prefs.getSchoolYearName()
    fun getCurrentSchoolYearId(): Int = repo.prefs.getCurrentSchoolYearId()
    fun getParticipationDisplay(): Int = repo.prefs.getParticipationDisplay()
    fun getParticipationDay(): Int = repo.prefs.getParticipationDay()

    // Preference setters
    fun setAppTheme(value: Int) { repo.prefs.setAppTheme(value) }
    fun setShowGradeAverage(value: Boolean) { repo.prefs.setShowGradeAverage(value) }
    fun setCourseSortType(value: Int) { repo.prefs.setCourseSortType(value) }
    fun setGradeType(value: Int) { repo.prefs.setGradeType(value) }
    fun setCurrentScoreProfile(scoreProfile: ScoreProfile) { repo.setCurrentScoreProfile(scoreProfile) }
    fun updateSchoolYear(schoolYear: SchoolYear) { repo.setCurrentSchoolYear(schoolYear) }
    fun setParticipationDisplay(value: Int) { repo.prefs.setParticipationDisplay(value) }
    fun setParticipationDay(value: Int) { repo.prefs.setParticipationDay(value) }

    // Database functions
    fun insert(schoolYear: SchoolYear) = doAsync { repo.insert(schoolYear) }
    fun insert(scoreProfile: ScoreProfile) = doAsync { repo.insert(scoreProfile) }
    fun initNewFinalScore(name: String) = doAsync { repo.initNewFinalScore(name) }
    fun updateSchoolYear(name: String) = doAsync { repo.updateSchoolYear(name) }
    fun updateScoreProfile(name: String) = doAsync { repo.updateScoreProfile(name) }
    fun deleteSchoolYear() = doAsync { repo.deleteSchoolYear() }
    fun deleteScoreProfile() = doAsync { repo.deleteScoreProfile() }
    fun copyCourses(yearId: Int) = doAsync { repo.copyCourses(yearId) }

    fun send(update: () -> Unit) = doAsync { QwarkClient(app.applicationContext).send(update) }
}