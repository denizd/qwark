package com.denizd.qwark.activity

import android.Manifest
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.denizd.qwark.R
import com.denizd.qwark.fragment.*
import com.denizd.qwark.fragment.CourseFragment
import com.denizd.qwark.fragment.ParticipationFragment
import com.denizd.qwark.viewmodel.MainViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

internal class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var toolbarTitle: TextView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 42)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.applyTheme(window, this)
        viewModel.checkForFirstTimeYearNotice(this)

        val actionBar = findViewById<AppBarLayout>(R.id.app_bar_layout)
        toolbarTitle = findViewById(R.id.toolbarTxt)
        bottomNavigationView = findViewById(R.id.bottom_nav)

        findViewById<AppBarLayout>(R.id.app_bar_layout).also { appBarLayout ->
            appBarLayout.setOnApplyWindowInsetsListener { _, insets ->
                (appBarLayout.layoutParams as ViewGroup.MarginLayoutParams)
                    .topMargin = insets.systemWindowInsetTop
                insets
            }
        }

        window.decorView.systemUiVisibility = if (resources.getBoolean(R.bool.isLight)) {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        window.attributes = window.attributes.apply {
            flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        window.statusBarColor = Color.TRANSPARENT

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            actionBar.setExpanded(true)
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
    }

    override fun onResume() {
        super.onResume()
        if (supportFragmentManager.fragments.size == 0) {
            bottomNavigationView.selectedItemId = R.id.courses
            loadFragment(viewModel.getFragmentType(R.id.courses))
        }
    }

    private fun loadFragment(type: String): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag(type) ?: viewModel.getFragment(type)

        if (fragment.lifecycle.currentState != Lifecycle.State.INITIALIZED) {
            return false
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, type)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

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
                bottomNavigationView.visibility = View.INVISIBLE
                findViewById<View>(R.id.view).visibility = View.INVISIBLE
                super.onBackPressed()
            }
            "NoteFragment" -> {
                if ((supportFragmentManager.fragments[0] as NoteFragment).onBackPressed()) {
                    super.onBackPressed()
                }
            }
            else -> {
                bottomNavigationView.visibility = View.VISIBLE
                findViewById<View>(R.id.view).visibility = View.VISIBLE
                super.onBackPressed()
            }
        }
    }
}
