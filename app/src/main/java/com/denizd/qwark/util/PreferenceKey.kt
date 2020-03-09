package com.denizd.qwark.util

enum class PreferenceKey(val value: String) {

    // Strings
    SCHOOL_YEAR_NAME("school_year_name"),
    SCORE_PROFILE_NAME("score_profile_name"),

    // Ints
    APP_THEME("dark_mode"),
    GRADE_TYPE("grade_type"),
    SCHOOL_YEAR_ID("school_year"),
    SCORE_PROFILE_ID("final_score_id"),
    MAX_SCORE_PROFILE_ID("max_final_score_id"),
    COURSE_SORT_TYPE("course_sort_type"),
    PARTICIPATION_DISPLAY("participation_display"),
    PARTICIPATION_DAY("participation_day"),

    // Bools
    SHOW_GRADE_AVERAGE("show_grade_average"),
    FIRST_LAUNCH("first_launch")
}