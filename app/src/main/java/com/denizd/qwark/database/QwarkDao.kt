package com.denizd.qwark.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.denizd.qwark.model.*
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.Grade
import com.denizd.qwark.model.HistoricalAvg
import com.denizd.qwark.model.Note
import com.denizd.qwark.model.SchoolYear
import com.denizd.qwark.util.QwarkUtil

@Dao
interface QwarkDao {

    // courses
    @get:Query("SELECT *, -1 AS exam_time FROM course ORDER BY time")
    val allCourses: LiveData<List<CourseExam>>

    @Query("SELECT course.*, (SELECT MIN(exam_time) FROM grade WHERE grade.course_id = course.course_id AND exam_time >= :time) AS exam_time FROM course GROUP BY course.course_id ORDER BY time")
    fun getAllCourseExams(time: Long = QwarkUtil.timeAtMidnight): List<CourseExam>

    @Query("SELECT course.*, (SELECT MIN(exam_time) FROM grade WHERE grade.course_id = course.course_id AND exam_time >= :timeAtMidnight) AS exam_time FROM course WHERE exam_time > -1 GROUP BY course.course_id ORDER BY time")
    fun getAllCoursesWithExams(timeAtMidnight: Long = QwarkUtil.timeAtMidnight): LiveData<List<CourseExam>>

    @Query("SELECT *, -1 AS exam_time FROM course WHERE course_id != :u ORDER BY time")
    fun getAllCourses(u: Int = -1): List<CourseExam> // the u variable is an unnecessary workaround to return a list instead of LiveData

    @Query("SELECT course.*, (SELECT MIN(exam_time) FROM grade WHERE grade.course_id = course.course_id AND exam_time >= :time) AS exam_time FROM course WHERE year_id = :yearId GROUP BY course.course_id ORDER BY time")
    fun getCoursesByYearId(yearId: Int, time: Long = QwarkUtil.timeAtMidnight): LiveData<List<CourseExam>>

    @Query("SELECT *, -1 AS exam_time FROM course WHERE year_id = :yearId ORDER BY time")
    fun getCoursesByYearIdAsList(yearId: Int): List<CourseExam>

    @Query("SELECT *, -1 AS exam_time FROM course WHERE course_id = :courseId")
    fun getCourse(courseId: Int): CourseExam

    @Insert
    fun insert(course: Course)

    @Delete
    fun delete(course: Course)

    @Update
    fun update(course: Course)

    @Query("UPDATE course SET average = :average, grade_count = :gradeCount WHERE course_id = :courseId")
    fun updateAverage(average: String, gradeCount: Int, courseId: Int)

    @Query("UPDATE course SET participation = :participation WHERE course_id = :courseId")
    fun updateParticipation(participation: String, courseId: Int)

    @Query("UPDATE participation SET times_hand_raised = :timesHandRaised, times_spoken = :timesSpoken WHERE participation_id = :participationId")
    fun updateParticipation(timesHandRaised: Int, timesSpoken: Int, participationId: Int)


    // grades
    @get:Query("SELECT * FROM grade ORDER BY time")
    val allGrades: LiveData<List<Grade>>

    @Query("SELECT * FROM grade WHERE course_id = :courseId ORDER BY time")
    fun getGradesByForeignKey(courseId: Int): LiveData<List<Grade>>

    @Insert
    fun insert(grade: Grade)

    @Update
    fun update(grade: Grade)

    @Query("DELETE FROM grade WHERE grade_id = :gradeId")
    fun deleteGrade(gradeId: Int)

    @Query("SELECT * FROM grade WHERE grade_id = :gradeId")
    fun getGrade(gradeId: Int): Grade


    // notes
    @get:Query("SELECT * FROM note ORDER BY time DESC")
    val allNotes: LiveData<List<Note>>

    @Query("SELECT * FROM note ORDER BY time DESC")
    fun getNotesAsList(): List<Note>

    @Insert
    fun insert(note: Note)

    @Query("UPDATE note SET dismissed = :newDismissedStatus WHERE note_id = :noteId")
    fun updateDismissed(newDismissedStatus: Boolean, noteId: Int)

    @Query("DELETE FROM note WHERE note_id = :noteId")
    fun deleteNote(noteId: Int)

    @Query("SELECT * FROM note WHERE category = :category ORDER BY time DESC")
    fun notesForCategory(category: String): LiveData<List<Note>>

    @Update
    fun update(note: Note)

    @Query("SELECT DISTINCT category FROM note")
    fun getAllCategories(): List<String>

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNote(noteId: Int): Note


    // historical averages
    @get:Query("SELECT * FROM historical_avg ORDER BY course_id DESC")
    val allHistoricalAverages: LiveData<List<HistoricalAvg>>

    @Insert
    fun insert(historicalAvg: HistoricalAvg)

    @Query("SELECT * FROM historical_avg WHERE course_id = :courseId ORDER BY time DESC")
    fun getAveragesForCourses(courseId: Int): List<HistoricalAvg>


    // years
    @get:Query("SELECT * FROM school_year ORDER BY year_id")
    val allYears: LiveData<List<SchoolYear>>

    @Query("SELECT * FROM school_year WHERE year_id != :u ORDER BY year_id")
    fun getAllYears(u: Int = -1): List<SchoolYear>

    @Insert
    fun insert(schoolYear: SchoolYear)

    @Update
    fun update(schoolYear: SchoolYear)

    @Query("DELETE FROM school_year WHERE year_id = :yearId")
    fun deleteSchoolYear(yearId: Int)


    // final scores
    @get:Query("SELECT * FROM score_profile ORDER BY score_profile_id")
    val allScoreProfiles: LiveData<List<ScoreProfile>>

    @Query("SELECT * FROM score_profile WHERE score_profile_id = :scoreProfileId")
    fun getScoreProfileNames(scoreProfileId: Int): List<ScoreProfile>

    @Insert
    fun insert(scoreProfile: ScoreProfile)

    @Update
    fun update(scoreProfile: ScoreProfile)

    @Query("DELETE FROM score_profile WHERE score_profile_id = :scoreProfileId")
    fun deleteScoreProfile(scoreProfileId: Int)

    @Query("SELECT school_year.year FROM school_year, course WHERE course.year_id = school_year.year_id AND course.course_id = :courseId")
    fun getSchoolYearName(courseId: Int): String


    // final grades
    @Query("SELECT * FROM final_grade WHERE score_profile_id = :scoreProfileId ORDER BY final_grade_id")
    fun getFinalGrades(scoreProfileId: Int): LiveData<List<FinalGrade>>

    @Query("SELECT * FROM final_grade WHERE score_profile_id = :scoreProfileId ORDER BY final_grade_id")
    fun getFinalGradesAsList(scoreProfileId: Int): List<FinalGrade>

    @Query("UPDATE final_grade SET name = :name, grade = :grade WHERE final_grade_id = :finalGradeId")
    fun updateFinalGradeAverage(name: String, grade: Int, finalGradeId: Int)

    @Update
    fun update(finalGrade: FinalGrade)

    @Insert
    fun insertAll(finalGrades: Array<FinalGrade>)

    @Query("SELECT * FROM final_grade WHERE final_grade_id = :finalGradeId")
    fun getFinalGrade(finalGradeId: Int): FinalGrade


    // school days
    @Insert
    fun insert(schoolDay: SchoolDay)


    // participation stuff
    @Query("SELECT course.*, cs.relation_id AS oral_weighting FROM course, course_schoolday_relation AS cs, school_day WHERE course.course_id = cs.course_id AND school_day.day_id = cs.day_id AND school_day.day = :dayType AND school_day.year_id = :yearId")
    fun getCoursesByDayType(dayType: String, yearId: Int): LiveData<List<Course>>

    @Query("SELECT * FROM school_day WHERE year_id = :yearId")
    fun getDays(yearId: Int): List<SchoolDay>

    @Query("INSERT INTO course_schoolday_relation VALUES (:courseId, (SELECT day_id FROM school_day WHERE day = :dayType AND year_id = :yearId), NULL)")
    fun link(courseId: Int, dayType: String, yearId: Int)

    @Query("DELETE FROM course_schoolday_relation WHERE relation_id = :relationId")
    fun removeLink(relationId: Int)

    @Query("SELECT * FROM participation WHERE course_id = :courseId ORDER BY time DESC")
    fun getAllParticipationsForCourse(courseId: Int): LiveData<List<Participation>>

    @Insert
    fun insert(participation: Participation)

    @Delete
    fun delete(participation: Participation)
}