package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.denizd.qwark.model.CourseExam
import com.denizd.qwark.model.FinalGrade
import com.denizd.qwark.model.SchoolYear
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.NumberFormatException

class FinalsViewModel(app: Application) : QwarkViewModel(app) {

    val allFinalGrades: LiveData<List<FinalGrade>> = repo.getFinalGrades(repo.prefs.getFinalScoreId())

    fun update(finalGrade: FinalGrade) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repo.update(finalGrade)
        }
    }

    fun getSchoolYearName(courseId: Int) = returnBlocking { repo.getSchoolYearName(courseId) }

    fun getScoreProfileName(): String = repo.prefs.getScoreProfileName()
    fun getGradeType(): Int = repo.prefs.getGradeType()

    fun getFinalScore(basicGrades: List<FinalGrade>, advancedGrades: List<FinalGrade>, examGrades: List<FinalGrade>): Int = try {
        var finalScore = 0
        for (grade in basicGrades) {
            finalScore += checkGrade(grade)
        }
        for (i in 0..2) {
            finalScore += (checkGrade(advancedGrades[i]) * 2)
        }
        for (i in 4..6) {
            finalScore += (checkGrade(advancedGrades[i]) * 2)
        }
        finalScore += checkGrade(advancedGrades[3]) + checkGrade(advancedGrades[7])
        for (grade in examGrades) {
            finalScore += if (grade.note == "P5 x 2") {
                (checkGrade(grade) * 2)
            } else {
                (checkGrade(grade) * 5)
            }
        }
        finalScore
    } catch (e: Exception) {
        -1
    }

    private fun checkGrade(finalGrade: FinalGrade): Int = if (finalGrade.grade.toInt() > -1) finalGrade.grade.toInt() else throw NumberFormatException()

    fun getFinalGradeScore(points: Int): Double = when (points) {
        in 823..900 -> 1.0
        in 805..822 -> 1.1
        in 787..804 -> 1.2
        in 769..786 -> 1.3
        in 751..768 -> 1.4
        in 733..750 -> 1.5
        in 715..732 -> 1.6
        in 697..714 -> 1.7
        in 679..696 -> 1.8
        in 661..678 -> 1.9
        in 643..660 -> 2.0
        in 625..642 -> 2.1
        in 607..624 -> 2.2
        in 589..606 -> 2.3
        in 571..588 -> 2.4
        in 553..570 -> 2.5
        in 535..552 -> 2.6
        in 517..534 -> 2.7
        in 499..516 -> 2.8
        in 481..498 -> 2.9
        in 463..480 -> 3.0
        in 445..462 -> 3.1
        in 427..444 -> 3.2
        in 409..426 -> 3.3
        in 391..408 -> 3.4
        in 373..390 -> 3.5
        in 355..372 -> 3.6
        in 337..354 -> 3.7
        in 319..336 -> 3.8
        in 301..318 -> 3.9
        else -> 4.0
    }

    // Database
    fun getAllYears(): List<SchoolYear> = returnBlocking { repo.getAllYears() }

    fun getAllCourses(): List<CourseExam> = returnBlocking { repo.getAllCourses() }

    fun updateFinalGradeAverage(courseId: Int, finalGradeId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.updateFinalGradeAverage(courseId, finalGradeId)
            }
        }
    }

    fun updateLinkedGrades(attachObserver: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repo.updateLinkedGrades() }
            withContext(Dispatchers.Main) { attachObserver() }
        }
    }

    fun getCourse(courseId: Int) = returnBlocking { repo.getCourse(courseId) }
    fun getFinalGrade(finalGradeId: Int) = returnBlocking { repo.getFinalGrade(finalGradeId) }
    fun getCourseSortType() = repo.prefs.getCourseSortType()
}