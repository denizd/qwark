package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.denizd.qwark.database.ParticipationRepository
import com.denizd.qwark.model.Participation

class ParticipationCourseViewModel(app: Application) : QwarkViewModel(app) {

    private val repo = ParticipationRepository(app)

    fun getAllParticipationsForCourse(courseId: Int): LiveData<List<Participation>> = returnBlocking {
        repo.getAllParticipationsForCourse(courseId)
    }
    fun getParticipationCount(participations: List<Participation>) = repo.getParticipationCount(participations)
    fun updateParticipation(participation: String, courseId: Int) = doAsync { repo.updateParticipation(participation, courseId) }
    fun createParticipation(timesHandRaised: Int, timesSpoken: Int, time: Long, courseId: Int) = doAsync {
        repo.insert(timesHandRaised, timesSpoken, time, courseId)
    }
    fun getSchoolYearName() = repo.getSchoolYearName()
    fun delete(participation: Participation) = doAsync { repo.delete(participation) }
}