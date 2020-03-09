package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.model.Participation

class ParticipationCourseViewModel(app: Application) : QwarkViewModel(app) {

    fun getAllParticipationsForCourse(courseId: Int): LiveData<List<Participation>> = returnBlocking {
        repo.getAllParticipationsForCourse(courseId)
    }
    fun getParticipationCount(participations: List<Participation>) = repo.getParticipationCount(participations)
    fun updateParticipation(participation: String, courseId: Int) = doAsync {
        repo.updateParticipation(participation, courseId)
    }
    fun updateParticipation(timesHandRaised: Int, timesSpoken: Int, participationId: Int) = doAsync {
        repo.updateParticipation(timesHandRaised, timesSpoken, participationId)
    }
    fun createParticipation(timesHandRaised: Int, timesSpoken: Int, time: Long, courseId: Int) = doAsync {
        repo.insert(timesHandRaised, timesSpoken, time, courseId)
    }
    fun getSchoolYearName() = repo.prefs.getSchoolYearName()
    fun delete(participation: Participation) = doAsync { repo.delete(participation) }
}