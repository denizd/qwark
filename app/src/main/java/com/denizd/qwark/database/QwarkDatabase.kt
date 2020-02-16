package com.denizd.qwark.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.denizd.qwark.model.*
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.Grade
import com.denizd.qwark.model.HistoricalAvg
import com.denizd.qwark.model.Note
import com.denizd.qwark.model.SchoolYear

@Database(entities = [Course::class, Grade::class, Note::class, HistoricalAvg::class, SchoolYear::class, FinalGrade::class, ScoreProfile::class, Participation::class, SchoolDay::class, CourseDayRelation::class], version = 9, exportSchema = false)
internal abstract class QwarkDatabase : RoomDatabase() {

    abstract fun dao(): QwarkDao

    companion object {

        // my first successful migration! lol
        private val migrationAddYearIdToDay = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE school_day")
                database.execSQL("CREATE TABLE school_day ( day TEXT NOT NULL, day_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, year_id INT NOT NULL, FOREIGN KEY (year_id) REFERENCES school_year (year_id) ON DELETE CASCADE )")
            }
        }

        private val migrationAddExamTimeToGrade = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE grades ADD COLUMN exam_time INTEGER NOT NULL")
            }
        }

        private val migrationAddGradeCount = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE course ADD COLUMN grade_count INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val migrationAddPrimaryKeyToRelation = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.apply {
                    execSQL("DROP TABLE course_schoolday_relation")
                    execSQL("""
                        CREATE TABLE course_schoolday_relation (
                            course_id INTEGER NOT NULL,
                            day_id INTEGER NOT NULL,
                            relation_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                            FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE CASCADE,
                            FOREIGN KEY (day_id) REFERENCES school_day (day_id) ON DELETE CASCADE
                        )
                    """.trimIndent())
                }
            }
        }

        private val migrationParticipationDateToTime = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.apply {
                    execSQL("DROP TABLE participation")
                    execSQL("""
                        CREATE TABLE participation (
                            times_hand_raised INTEGER NOT NULL,
                            times_spoken INTEGER NOT NULL,
                            time INTEGER NOT NULL,
                            participation_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                            course_id INTEGER NOT NULL,
                            day_id INTEGER NOT NULL,
                            FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE CASCADE,
                            FOREIGN KEY (day_id) REFERENCES school_day (day_id) ON DELETE CASCADE
                        )
                    """.trimIndent())
                }
            }
        }

        private val migrationAddParticipation = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE course ADD COLUMN participation TEXT NOT NULL DEFAULT '--|--'")
            }
        }

        private var instance: QwarkDatabase? = null

        fun getInstance(context: Context): QwarkDatabase {
            if (instance == null) {
                synchronized(QwarkDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        QwarkDatabase::class.java,
                        "qwark_database")
                        .addMigrations(
                            migrationAddGradeCount,
                            migrationAddPrimaryKeyToRelation,
                            migrationParticipationDateToTime,
                            migrationAddParticipation
                        ).build()
                }
            }
            return instance!!
        }
    }
}