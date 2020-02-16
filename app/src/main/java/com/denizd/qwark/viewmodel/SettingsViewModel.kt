package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.database.QwarkClient
import com.denizd.qwark.util.QwarkPreferences
import com.denizd.qwark.database.SettingsRepository
import com.denizd.qwark.model.ScoreProfile
import com.denizd.qwark.model.SchoolYear

class SettingsViewModel(private val app: Application) : QwarkViewModel(app) {

    private val repo = SettingsRepository(app)
    val allYears: LiveData<List<SchoolYear>> = repo.allYears
    val allScoreProfiles: LiveData<List<ScoreProfile>> = repo.allScoreProfiles
    private val prefs: QwarkPreferences = QwarkPreferences.getPrefs(app.applicationContext)

    // Preference getters
    fun getAppTheme() = repo.getAppTheme()
    fun getShowGradeAverage() = repo.getShowGradeAverage()
    fun getCourseSortType(): Int = prefs.getCourseSortType()
    fun getGradeType() = repo.getGradeType()
    fun getScoreProfileName() = repo.getScoreProfileName()
    fun getCurrentSchoolYear(): String = repo.getCurrentSchoolYear()
    fun getCurrentSchoolYearId(): Int = repo.getCurrentSchoolYearId()
    fun getParticipationDisplay(): Int = repo.getParticipationDisplay()
    fun getParticipationDay(): Int = repo.getParticipationDay()

    // Preference setters
    fun setAppTheme(value: Int) { repo.setAppTheme(value) }
    fun setShowGradeAverage(value: Boolean) { repo.setShowGradeAverage(value) }
    fun setCourseSortType(value: Int) { prefs.setCourseSortType(value) }
    fun setGradeType(value: Int) { repo.setGradeType(value) }
    fun setCurrentScoreProfile(scoreProfile: ScoreProfile) { repo.setCurrentScoreProfile(scoreProfile) }
    fun updateSchoolYear(schoolYear: SchoolYear) { repo.setCurrentSchoolYear(schoolYear) }
    fun setParticipationDisplay(value: Int) { repo.setParticipationDisplay(value) }
    fun setParticipationDay(value: Int) { repo.setParticipationDay(value) }

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