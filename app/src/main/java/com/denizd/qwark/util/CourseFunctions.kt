package com.denizd.qwark.util
import com.denizd.qwark.model.Course
import com.denizd.qwark.model.CourseExam

fun List<CourseExam>.getSorted(sortType: Int): List<CourseExam> = when (sortType) { // TODO check what ascending and descending really mean lol
    QwarkUtil.SORT_COURSES_TIME_ASC -> this.asReversed()
    QwarkUtil.SORT_COURSES_NAME_ASC -> this.sortedBy { c -> c.name }
    QwarkUtil.SORT_COURSES_NAME_DESC -> this.sortedByDescending { c -> c.name }
    QwarkUtil.SORT_COURSES_ADV -> this.sortedByDescending { c -> c.advanced }
    QwarkUtil.SORT_COURSES_BASIC -> this.sortedBy { c -> c.advanced }
    QwarkUtil.SORT_COURSES_AVG_ASC -> this.sortedBy { c -> c.average.toDouble() }
    QwarkUtil.SORT_COURSES_AVG_DESC -> this.sortedByDescending { c -> c.average.toDouble() }
    QwarkUtil.SORT_COURSES_NEXT_EXAM -> this.sortedBy { c -> if (c.examTime > 1L) c.examTime else Long.MAX_VALUE }
    else -> this // TIME_DESC
}

fun CourseExam.asCourse(): Course = Course(
    name = name,
    advanced = advanced,
    icon = icon,
    colour = colour,
    average = average,
    oralWeighting = oralWeighting,
    gradeCount = gradeCount,
    time = time,
    courseId = courseId,
    yearId = yearId
)