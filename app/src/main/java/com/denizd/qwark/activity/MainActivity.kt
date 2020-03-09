package com.denizd.qwark.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.denizd.lawrence.util.viewBinding
import com.denizd.qwark.R
import com.denizd.qwark.database.QwarkRepository
import com.denizd.qwark.databinding.ActivityMainBinding
import com.denizd.qwark.fragment.*
import com.denizd.qwark.util.Dependencies
import com.denizd.qwark.util.ExamNotificationWorker
import com.denizd.qwark.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : FragmentActivity() {

    private lateinit var viewModel: MainViewModel
    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(binding.root)

//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 42)

        Dependencies.repo = QwarkRepository(application)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.applyTheme(window, this)
        viewModel.checkForFirstTimeYearNotice(this)

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            binding.appBarLayout.setExpanded(true)
            loadFragment(viewModel.getFragmentType(item.itemId))
        }

        // TODO figure out why this isn't working
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().apply {
//                val fragments = arrayOf(
//                    CourseFragment(),
////                    GradeFragment(),
//                    NoteFragment(),
//                    ParticipationFragment(),
////                    ParticipationCourseFragment(),
//                    FinalsFragment(),
//                    SettingsFragment()
//                )
//                for (fragment in fragments) {
//                    add(R.id.fragment_container, fragment, fragment.name)
//                    if (fragment !is CourseFragment) hide(fragment)
//                }
//            }.commit()
//        }

        // TODO reselected listener for scrolling up

        resources.getStringArray(R.array.greetings).also { array ->
            binding.greetingTextView.text = String.format(
                    array[Random.nextInt(array.size)],
                    Dependencies.repo.prefs.getScoreProfileName()
            )
        }
        // splash screen
//        Handler().postDelayed({
//            binding.splashScreen.apply {
//                val cx = width / 2
//                val cy = height / 2
//                val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
//
//                ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius, 0f).also { anim ->
//                    anim.addListener(object : AnimatorListenerAdapter() {
//                        override fun onAnimationEnd(animation: Animator) {
//                            super.onAnimationEnd(animation)
//                            visibility = View.GONE
//                        }
//                    })
//                    anim.start()
//                }
//            }
//        }, 1200)

        Dependencies.repo.allCoursesWithExams.observe(this, Observer { courses ->
            with (WorkManager.getInstance(this)) {
                for (course in courses) {
                    beginUniqueWork(
                        "${course.name}-${course.courseId}",
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequestBuilder<ExamNotificationWorker>()
                            .setInitialDelay(getTimeUntil(course.examTime), TimeUnit.MILLISECONDS)
                            .setInputData(Data.Builder().putString("course", course.name).build())
                            .build()
                    ).enqueue()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (supportFragmentManager.fragments.size == 0) {
            binding.bottomNav.selectedItemId = R.id.courses
            loadFragment(viewModel.getFragmentType(R.id.courses))
        }
    }

    private fun loadFragment(type: String): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag(type) ?: viewModel.getFragment(type)

        if (fragment.lifecycle.currentState != Lifecycle.State.INITIALIZED) {
            return false
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, type)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()

//        with (supportFragmentManager) {
//            beginTransaction()
//                .hide(fragments[fragments.size - 1]) // should this be 0?
//                .show(findFragmentByTag(type) ?: TODO())
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
//        }
        return true
    }

    override fun onBackPressed() {
//        Log.d("TAG2", "${supportFragmentManager.}") // supportFragmentManager.fragments[0].tag} ${supportFragmentManager.fragments[0].javaClass.simpleName
        when (supportFragmentManager.fragments[0].tag) {
            "GradeFragment", "ParticipationCourseFragment" -> {
                binding.bottomNav.visibility = View.INVISIBLE
                binding.view.visibility = View.INVISIBLE
                super.onBackPressed()
            }
            "NoteFragment" -> {
                if ((supportFragmentManager.fragments[0] as NoteFragment).onBackPressed()) {
                    super.onBackPressed()
                }
            }
            else -> {
                binding.bottomNav.visibility = View.VISIBLE
                binding.view.visibility = View.VISIBLE
                super.onBackPressed()
            }
        }
    }

    // TODO add option to set time of notification (currently: 7 AM)
    private fun getTimeUntil(examTime: Long): Long = examTime + viewModel.getExamNotificationTime() - System.currentTimeMillis()
}
